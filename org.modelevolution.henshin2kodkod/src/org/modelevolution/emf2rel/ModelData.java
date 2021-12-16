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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import kodkod.ast.Relation;
import kodkod.instance.TupleSet;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.modelevolution.emf2rel.ModelAnalyzer.InOutRefs;
import org.modelevolution.gts2rts.util.IntUtil;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class ModelData {
  private final boolean hasBools;
  private final boolean hasInts;
  private final boolean hasEnums;
  private final Collection<EClass> classes;
  private final Enums enums;
  private final Map<EClass, Collection<EStructuralFeature>> featureMap;
  private final Map<EClass, Integer> lowerObjBounds;
  private final Map<EClass, Integer> upperObjBounds;
  private final Resource instance;
  private final int bitwidth;
  private final Map<EClass, InOutRefs> inOutReferences;
  private final Map<EClass, List<EClass>> superclasses;
  private Map<Relation, TupleSet> initialState;
  private Map<EClass, Collection<EAttribute>> attributes;
  private Map<EClass, Collection<EReference>> references;

  /**
   * @param instance
   *          TODO
   * @param classes
   * @param enums
   *          TODO
   * @param inOutRefMap
   *          TODO
   * @param superclassMap
   *          TODO
   * @param lowerObjBounds
   * @param upperObjBounds
   * @param hasInts
   *          TODO
   * @param hasBools
   *          TODO
   * @param hasEnums
   *          TODO
   * @param bitwidth
   *          TODO
   */
  ModelData(Resource instance, Collection<EClass> classes, Enums enums,
      Map<EClass, Collection<EStructuralFeature>> featureMap, Map<EClass, InOutRefs> inOutRefMap,
      Map<EClass, List<EClass>> superclassMap, Map<EClass, Integer> lowerObjBounds,
      Map<EClass, Integer> upperObjBounds, boolean hasInts, boolean hasBools, boolean hasEnums,
      int bitwidth) {
    this.instance = instance;
    this.classes = classes;
    // this.enums = enums;
    this.featureMap = featureMap;
    this.attributes = new IdentityHashMap<>();
    this.references = new IdentityHashMap<>();
    initAttributesAndReferences(featureMap);
    this.inOutReferences = inOutRefMap;
    this.superclasses = superclassMap;
    this.lowerObjBounds = lowerObjBounds;
    this.upperObjBounds = upperObjBounds;
    this.hasInts = hasInts;
    this.hasBools = hasBools;
    this.enums = enums;
    this.bitwidth = bitwidth;
    this.hasEnums = hasEnums;
  }

  /**
   * @param map
   */
  private void initAttributesAndReferences(Map<EClass, Collection<EStructuralFeature>> map) {
    for (final Entry<EClass, Collection<EStructuralFeature>> e : map.entrySet()) {
      final EClass eClass = e.getKey();
      final Collection<EStructuralFeature> features = e.getValue();
      for (final EStructuralFeature feature : features) {
        if (feature instanceof EAttribute) {
          final EAttribute attr = (EAttribute) feature;
          final Collection<EAttribute> attrs;

          if (attributes.containsKey(eClass))
            attrs = attributes.get(attr.getEContainingClass());
          else {
            attrs = new ArrayList<>();
            attributes.put(eClass, attrs);
          }
          attrs.add(attr);
        } else if (feature instanceof EReference) {
          final EReference ref = (EReference) feature;
          final Collection<EReference> refs;

          if (references.containsKey(eClass))
            refs = references.get(eClass);
          else {
            refs = new ArrayList<>();
            references.put(eClass, refs);
          }
          refs.add(ref);
        }
      }
    }
  }

  public int bitwidth() {
    return bitwidth;
  }

  public Resource instance() {
    return instance;
  }

  public Collection<EClass> classes() {
    return Collections.unmodifiableCollection(this.classes);
  }

  // public Collection<EEnum> enums() {
  // return this.enums.allEEnums();
  // }

  public Enums enums() {
    return this.enums;
  }

  public Integer lookpupLiteralValue(final EEnum e, String literal) {
    return enums.lookupLitValue(e, literal);
  }

  /**
   * @param c
   *          an {@link EClass}
   * @return all incoming references of an {@link EClass} <code>c</code>
   */
  public Collection<EReference> inReferences(EClass c) {
    assert inOutReferences.containsKey(c);
    if (inOutReferences.containsKey(c))
      return Collections.unmodifiableList(inOutReferences.get(c).incoming());
    else
      return Collections.emptyList();
  }

  /**
   * @param c
   *          an {@link EClass}
   * @return all outgoing references of an {@link EClass} <code>c</code>
   */
  public Collection<EReference> outReferences(EClass c) {
    assert inOutReferences.containsKey(c);
    if (inOutReferences.containsKey(c))
      return Collections.unmodifiableList(inOutReferences.get(c).outgoing());
    else
      return Collections.emptyList();
  }

  /**
   * @param subclass
   * @return the list of superclasses for the given subclass
   */
  public List<EClass> superclasses(final EClass subclass) {
    return Collections.unmodifiableList(superclasses.get(subclass));
  }

  public int numOfClassifiers() {
    return classes.size() + enums.size();
  }

  public int numOfFeatures() {
    return featureMap.size();
  }

  public Map<EClass, Collection<EStructuralFeature>> features() {
    return Collections.unmodifiableMap(this.featureMap);
  }

  public boolean hasBools() {
    return this.hasBools;
  }

  public boolean hasIntsBoolsOrEnums() {
    return hasInts || hasBools || hasEnums;
  }

  public int getLowerObjBound(final EClass eClass) {
    if (lowerObjBounds.containsKey(eClass))
      return lowerObjBounds.get(eClass);
    return 0;
  }

  public int getUpperObjBound(final EClass eClass) {
    if (upperObjBounds.containsKey(eClass))
      return upperObjBounds.get(eClass);
    return 0;
  }

  /**
   * @param e
   * @return
   */
  public Collection<String> literals(final EEnum e) {
    return enums.literalNames(e);
  }

  /**
   * @param e
   * @return
   */
  public Collection<Integer> literalValues(EEnum e) {
    return enums.literalValues(e);
  }

  /**
   * @return
   */
  int minInt() {
    return IntUtil.minInt(bitwidth);
  }

  int maxInt() {
    return IntUtil.maxInt(bitwidth);
  }

  /**
   * @return
   */
  public boolean hasInts() {
    return hasInts;
  }

  /**
   * @return
   */
  public int numOfInts() {
    return IntUtil.numOfInts(bitwidth);
  }

  /**
   * @param eenum
   * @param eGet
   * @return
   */
  public Integer lookupEnumValue(final EEnum eenum, final String literalName) {
    return enums.lookupLitValue(eenum, literalName);
  }

  /**
   * Initialize the initial state of the system according to the lower bounds
   * extracted from the instance model.
   * 
   * @param lowers
   *          a map of objects of the {@link ModelData#instance() instance
   *          model} to the set of tuples from the {@link Signature#univ()
   *          universe}
   */
  public void initialState(Map<Relation, TupleSet> lowerBounds) {
    this.initialState = lowerBounds;
  }

  /**
   * Get the initial state, i.e., a map of objects of the
   * {@link ModelData#instance() instance model} to the set of tuples from the
   * {@link Signature#univ() universe}
   * 
   * @return the initial state
   */
  public Map<Relation, TupleSet> initialState() {
    return Collections.unmodifiableMap(initialState);
  }

  /**
   * @param clazz
   * @return
   */
  public Collection<EAttribute> attributes(final EClass clazz) {
    if (clazz == null)
      throw new NullPointerException();
    if (!attributes.containsKey(clazz))
      return Collections.emptyList();
    return Collections.unmodifiableCollection(attributes.get(clazz));
  }

  /**
   * @param clazz
   * @return
   */
  public Collection<EReference> references(final EClass clazz) {
    if (clazz == null)
      throw new NullPointerException();
    if (!references.containsKey(clazz))
      return Collections.emptyList();
    return Collections.unmodifiableCollection(references.get(clazz));
  }

}
