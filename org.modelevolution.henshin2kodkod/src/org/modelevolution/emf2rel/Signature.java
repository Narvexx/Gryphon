/*
 * henshin2kodkod -- Copyright (c) 2014-present, Sebastian Gabmeyer
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.modelevolution.emf2rel;

import static org.modelevolution.gts2rts.util.EcoreUtil.getEClasses;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import kodkod.ast.Formula;
import kodkod.ast.IntConstant;
import kodkod.ast.IntExpression;
import kodkod.ast.Relation;
import kodkod.ast.Variable;
import kodkod.instance.Bounds;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;
import kodkod.instance.Universe;
import kodkod.util.ints.IntIterator;
import kodkod.util.ints.IntSet;
import kodkod.util.ints.IntTreeSet;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.henshin.model.Annotation;
import org.eclipse.emf.henshin.model.Rule;
import org.modelevolution.gts2rts.ParamData;
import org.modelevolution.gts2rts.ParamDataset;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * This class represents a relational signature that consists of a set of
 * relations and a set of corresponding bounds. It offers methods to access
 * these relations and bounds. Further, it offers methods that generate a
 * Signature from an Ecore model and an instance model where the latter captures
 * the lower bounds.
 * 
 * <p>
 * The translation does not support:
 * <ol>
 * <li>many-valued attributes
 * <li>attributes of primitive types other than <code>EInt</code> or
 * <code>EBoolean</code>. Note that enumeration types are supported.
 * </ol>
 * 
 * @author Sebastian Gabmeyer
 * 
 */
public class Signature {
  /**
   * 
   */
  private static final int DEFAULT_BITWIDTH = 0;

  // private static Signature instance;

  public static Signature init(final EPackage model, List<String> features, final Resource initialModel)
      throws IOException {
    return init(model, features, initialModel, new IdentityHashMap<EClass, Integer>(),
                FeatureMerger.create(), null, DEFAULT_BITWIDTH);
  }

  public static Signature init(final EPackage model, List<String> features, final Resource initialModel,
      Map<EClass, Integer> upperObjBounds, FeatureMerger merger, FeatureOmitter omitter,
      int bitwidth) {
    if (model == null || initialModel == null)
      throw new NullPointerException("model == null || initialModel == null.");
    if (upperObjBounds == null)
      upperObjBounds = new IdentityHashMap<>();
    if (merger == null)
      merger = FeatureMerger.create();
    if (omitter == null)
      omitter = FeatureOmitter.EMPTY;
    if (bitwidth < 0)
      bitwidth = DEFAULT_BITWIDTH;
  
    Map<EClass, Integer> upperBounds = initOrCompleteUpperBounds(model, upperObjBounds);
    assert upperBounds.keySet().containsAll(getEClasses(model));

    final ModelData mdata = ModelAnalyzer.analyze(model, initialModel, upperBounds, omitter,
                                                  bitwidth);
    final Universe univ = UniverseBuilder.createUniverse(mdata, features);
    final StateRegistry registry = StateRegistry.createRelations(mdata, merger);
    final Bounds bounds = BoundsBuilder.createBounds(mdata, registry, univ);

    return new Signature(mdata, univ, bounds, registry);
  }

  //
  // public static Signature instance() {
  // if (instance == null)
  // throw new IllegalStateException("Signature is not initialized.");
  // return instance;
  // }

  /**
   * @param pkg
   * @param upperObjBounds
   * @return
   */
  private static Map<EClass, Integer> initOrCompleteUpperBounds(final EPackage pkg,
      final Map<EClass, Integer> upperObjBounds) {
    final EList<EClassifier> classifiers = pkg.getEClassifiers();
    final Map<EClass, Integer> upperBounds = new IdentityHashMap<>(classifiers.size());
    for (final EClassifier c : classifiers)
      if (c instanceof EClass) {
        final EClass clazz = (EClass) c;
        if (upperObjBounds.containsKey(clazz))
          upperBounds.put(clazz, upperObjBounds.get(clazz));
        else
          upperBounds.put(clazz, 0);
      }
    return upperBounds;
  }

  private final ModelData mdata;
  private Universe univ;
  private final Bounds bounds;
  // private final Formula funcDecls;
  public final StateRegistry registry;

  // private final SignatureBuilder sBuilder;
  // private final UniverseBuilder uBuilder;

  private final Map<StateRelation, Collection<StateRelation>> superRelations;

  private final Map<Relation, TupleSet> initialStateIndicesMap;

  private final Formula multiplicityConstraints;

  private Signature(final ModelData data, final Universe univ, final Bounds bounds,
      final StateRegistry registry) {
    this.mdata = data;
    this.univ = univ;
    this.bounds = bounds;
    this.registry = registry;
    this.superRelations = collectSuperRelations();
    initialStateIndicesMap = mdata.initialState();
    multiplicityConstraints = Formula.TRUE; // buildMultiplicityConstraints(mdata.features());
  }

  /**
   * @return
   */
  public int bitwidth() {
    return mdata.bitwidth();
  }

  /**
   * @return the bounds of this signature
   */
  public Bounds bounds() {
    return bounds;
  }

  public Enums enums() {
    return mdata.enums();
  }

  public Map<Relation, TupleSet> initialStateMap() {
    return Collections.unmodifiableMap(initialStateIndicesMap);
  }

  public Collection<EReference> inRefs(final EClass c) {
    return mdata.inReferences(c);
  }

  /**
   * Checks if the <code>variable index</code> of the relation is set in the
   * initial state.
   * 
   * @param r
   * @param index
   *          the index of the variable
   * @return
   * @requires 0 <= index <= bounds.upperBound(r).indexView().max() -
   *           bounds.upperBound(r).indexView().min()
   */
  public boolean isInitial(final Relation r, final int index) {
    if (initialStateIndicesMap.containsKey(r))
      return initialStateIndicesMap.get(r).indexView().contains(index);
    return false;
  }

  public Formula multiplicityConstraint() {
    return multiplicityConstraints;
  }

  public int numRelations() {
    return registry.size();
  }

  public Collection<EReference> outRefs(final EClass c) {
    return mdata.outReferences(c);
  }

  public Relation post(final EClassifier type) {
    if (type == null)
      throw new NullPointerException();

    final StateRelation state = state(type);
    if (state != null)
      return state.postState();
    else
      return null;
  }

  public Relation post(final EClass type, final EStructuralFeature feature) {
    if (type == null || feature == null)
      throw new NullPointerException();

    final StateRelation state = state(type, feature);
    if (state != null)
      return state.postState();
    else
      return null;
  }

  /**
   * @return
   */
  public Collection<Relation> postRelations() {
    return registry.allPostStates();
  }

  public Relation pre(final EClassifier type) {
    if (type == null)
      throw new NullPointerException();

    final StateRelation state = state(type);
    if (state != null)
      return state.preState();
    else
      return null;
  }

  public Relation pre(final EClass type, EStructuralFeature feature) {
    if (type == null || feature == null)
      throw new NullPointerException();

    final StateRelation state = state(type, feature);
    if (state != null)
      return state.preState();
    else
      return null;
  }

  /**
   * Returns, for the given <code>type</code>, the pre/current and the post/next
   * state relation.
   * 
   * @param type
   *          the type is either an {@linkplain EClass}, an
   *          {@linkplain EReference}, or an {@linkplain EAttribute} for which
   *          the {@link StateRelation pre/post relation} was created
   * @return the {@link StateRelation pre/post relation} associated with the
   *         <code>type</code> or <code>null</code> if no such pre/post relation
   *         exists.
   */
  public StateRelation state(final EClassifier type) {
    if (type == null)
      throw new NullPointerException();

    return registry.state(type);
  }

  /**
   * Returns, for the given <code>type</code>, the pre/current and the post/next
   * state relation.
   * 
   * @param type
   *          the type is either an {@linkplain EClass}, an
   *          {@linkplain EReference}, or an {@linkplain EAttribute} for which
   *          the {@link StateRelation pre/post relation} was created
   * @return the {@link StateRelation pre/post relation} associated with the
   *         <code>type</code> or <code>null</code> if no such pre/post relation
   *         exists.
   */
  public StateRelation state(final EClass type, final EStructuralFeature feature) {
    if (type == null || feature == null)
      throw new NullPointerException();

    return registry.state(type, feature);
  }

  /**
   * Get all relations known to this signature ordered by pre/post pairs.
   * 
   * @return
   */
  public Collection<Relation> relations() {
    final Collection<StateRelation> states = registry.allStates();
    final Collection<Relation> all = new ArrayList<>(states.size() * 2);

    for (StateRelation state : states) {
      all.add(state.preState());
      all.add(state.postState());
    }

    return all;
  }

  /**
   * @return
   */
  public Collection<StateRelation> states() {
    return registry.allStates();
  }

  /**
   * @param prePost
   * @return
   */
  public Collection<StateRelation> superStates(final StateRelation prePost) {
    if (prePost == null)
      throw new NullPointerException();
    if (superRelations.containsKey(prePost))
      return Collections.unmodifiableCollection(superRelations.get(prePost));
    else
      return Collections.emptySet();
  }

  @Override
  public String toString() {
    final String indent = " ";
    final String newLine = "\n";
    final String univHeader = "universe: \n";
    final String boundsString = bounds.toString();
    final String univString = univ.toString();
    final StringBuffer sb = new StringBuffer(univHeader.length() + indent.length()
        + univString.length() + newLine.length() + boundsString.length());
    sb.append(univHeader);
    sb.append(indent);
    sb.append(univString);
    sb.append(newLine);
    sb.append(boundsString);
    return sb.toString();
  }

  /**
   * @return the universe of this signature
   */
  public Universe univ() {
    return univ;
  }
  
  public void setUniverse(List<Object> universe) {
	  this.univ = new Universe(universe);
  }

  /**
   * @param eClass
   *          TODO
   * @param feature
   * @return
   */
  private Formula boundAbove(final EClass eClass, final EStructuralFeature feature) {
    final Relation domain = registry.postState(eClass);
    final Variable d = Variable.unary(domain.name());
    final Relation r = registry.postState(eClass, feature);
    final IntConstant upperBound = IntConstant.constant(feature.getUpperBound());
    return d.join(r).count().lte(upperBound).forAll(d.oneOf(domain));
  }

  /**
   * @param eClass
   *          TODO
   * @param feature
   * @return
   */
  private Formula boundBelow(final EClass eClass, final EStructuralFeature feature) {
    final Relation domain = registry.postState(feature.getEContainingClass());
    final Variable d = Variable.unary(domain.name());
    final Relation r = registry.postState(eClass, feature);
    final IntConstant lowerBound = IntConstant.constant(feature.getLowerBound());
    return d.join(r).count().gte(lowerBound).forAll(d.oneOf(domain));
  }

  /**
   * @param eClass
   *          TODO
   * @param feature
   * @return
   */
  private Formula boundBelowAbove(final EClass eClass, final EStructuralFeature feature) {
    final Relation domain = registry.postState(feature.getEContainingClass());
    final Variable d = Variable.unary(domain.name());
    final Relation r = registry.postState(eClass, feature);
    final IntConstant lowerBound = IntConstant.constant(feature.getLowerBound());
    final IntConstant upperBound = IntConstant.constant(feature.getUpperBound());
    final IntExpression objectCount = d.join(r).count();
    return objectCount.gte(lowerBound).and(objectCount.lte(upperBound)).forAll(d.oneOf(domain));
  }

  /**
   * @param initialStateMap
   *          TODO
   * @return
   */
  private Map<Relation, IntSet> buildNormalizedInitialState(Map<Relation, TupleSet> initialStateMap) {
    final Map<Relation, IntSet> initialStateIndicesMap = new HashMap<>(initialStateMap.size());
    for (final Entry<Relation, TupleSet> e : initialStateMap.entrySet()) {
      final Relation r = e.getKey();
      assert r != null;
      final TupleSet tuples = bounds.upperBound(r);
      assert !tuples.isEmpty();

      final int minTupleIndex = tuples.indexView().min();
      final IntSet initialIndices = new IntTreeSet();
      final TupleSet rawInitialTupleSet = e.getValue();
      final IntIterator it = rawInitialTupleSet.indexView().iterator();
      while (it.hasNext()) {
        final int varIndex = it.next();
        final int normalizedTupleIndex = varIndex - minTupleIndex;
        assert normalizedTupleIndex >= 0 : normalizedTupleIndex + " R: " + r + " T: " + tuples
            + " var: " + varIndex + " min: " + minTupleIndex;
        initialIndices.add(normalizedTupleIndex);
      }
      initialStateIndicesMap.put(r, initialIndices);
    }
    return initialStateIndicesMap;
  }

  /**
   * @param featureMap
   * @return
   */
  private Formula buildMultiplicityConstraints(
      final Map<EClass, Collection<EStructuralFeature>> featureMap) {
    final List<Formula> constraint = new ArrayList<>(featureMap.size());
    for (final Entry<EClass, Collection<EStructuralFeature>> e : featureMap.entrySet()) {
      final EClass eClass = e.getKey();
      final Collection<EStructuralFeature> features = e.getValue();
      for (final EStructuralFeature f : features) {
        final int lower = f.getLowerBound();
        final int upper = f.getUpperBound();
        assert lower >= -1 && upper >= -1;
        assert upper != 0;
        assert lower <= upper || upper == -1;
        if (lower == 0) {
          if (upper == 1)
            constraint.add(partial(eClass, f));
          else if (upper != -1)
            constraint.add(boundAbove(eClass, f));
        } else if (lower == 1) {
          if (upper == 1)
            constraint.add(function(eClass, f));
          else if (upper == -1)
            constraint.add(boundBelow(eClass, f));
          else
            constraint.add(boundBelowAbove(eClass, f)); // lower == 1 && upper
                                                        // != -1
        } else if (lower > 1)
          if (upper != -1)
            constraint.add(boundBelowAbove(eClass, f));
          else
            constraint.add(boundBelow(eClass, f));
      }
    }
    return Formula.and(constraint);
  }

  /**
   * @return
   */
  private Map<StateRelation, Collection<StateRelation>> collectSuperRelations() {
    final Map<StateRelation, Collection<StateRelation>> superRelationMap;
    superRelationMap = new IdentityHashMap<>(mdata.classes().size());

    for (final EClass clazz : mdata.classes()) {
      final StateRelation relation = state(clazz);
      final Collection<EClass> superclasses = mdata.superclasses(clazz);
      final Collection<StateRelation> superRelations = new ArrayList<>(superclasses.size());
      superRelationMap.put(relation, superRelations);
      for (final EClass superclass : superclasses) {
        final StateRelation superRelation = state(superclass);
        superRelations.add(superRelation);
      }
    }
    return superRelationMap;
  }

  /**
   * @param eClass
   * @param feature
   * @return
   */
  private Formula function(final EClass eClass, final EStructuralFeature feature) {
    final Relation r = registry.postState(eClass, feature);
    final Relation domain = registry.postState(eClass);
    final Relation range = registry.postState(feature.getEType());
    return r.function(domain, range);
  }

  /**
   * @param eClass
   *          TODO
   * @param feature
   * @return
   */
  private Formula partial(final EClass eClass, final EStructuralFeature feature) {
    final Relation r = registry.postState(eClass, feature);
    final Relation domain = registry.postState(eClass);
    final Relation range = registry.postState(feature.getEType());
    return r.partialFunction(domain, range);
  }

  /**
   * @param affectedStates
   */
  public void optimizeBounds(final Set<StateRelation> affectedStates) {
    for (final StateRelation s : states()) {
      if (s.isStatic() || affectedStates.contains(s))
        continue;
      TupleSet lowerBounds = mdata.initialState().get(s.preState());
      if (lowerBounds == null && !affectedStates.contains(s))
        lowerBounds = univ.factory().noneOf(s.arity());
      else if (lowerBounds == null)
        continue;
      bounds.boundExactly(s.preState(), lowerBounds);
      bounds.boundExactly(s.postState(), lowerBounds);
    }
  }

  /**
   * @param params
   */
  public void bindParams(final ParamDataset params) {
    for (final ParamData param : params) {
      if (param.type() == null)
        throw new IllegalStateException("Untyped rule parame: param.type() == null.");
      final Relation relation = this.pre(param.type());
      assert relation != null;
      TupleSet upperParamBound = this.bounds().upperBound(relation);
      this.bounds().bound(param.var(), upperParamBound);
    }
  }
}
