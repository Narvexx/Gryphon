/*
 * henshin2kodkod -- Copyright (c) 2014-present, Sebastian Gabmeyer
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.modelevolution.emf2rel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import kodkod.ast.Relation;
import kodkod.ast.Variable;
import kodkod.util.collections.IdentityHashSet;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.EcorePackage;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public final class StateRegistry {
  /**
   * 
   */
  private static final int BINARY = 2;
  /**
   * 
   */
  private static final int UNARY = 1;
  private static final String INTS = "Ints";
  private static final String BOOLS = "Bools";
  private static final String FEATURES = "Features";
  private final Map<EClassifier, StateRelation> classifierRegistry;
  private StateRelation ints;
  private StateRelation bools;
  private StateRelation features;
  private List<EStructuralFeature> functions;
  private List<EStructuralFeature> partialFunctions;
  private Map<EClassifier, Map<EStructuralFeature, StateRelation>> featureRegistry;
  private Collection<StateRelation> allStates;

  // protected PairedRelationRegistry() {
  // this.registry = new IdentityHashMap<>();
  // }

  private StateRegistry(int expectedsize) {
    if (expectedsize < 0)
      throw new IllegalArgumentException();
    this.classifierRegistry = new LinkedHashMap<EClassifier, StateRelation>(expectedsize);
    this.featureRegistry = new LinkedHashMap<EClassifier, Map<EStructuralFeature, StateRelation>>(
                                                                                            expectedsize);
    this.functions = new ArrayList<>();
    this.partialFunctions = new ArrayList<>();
  }

  /**
   * @param mdata
   * @param merger
   * @return
   */
  static StateRegistry createRelations(final ModelData mdata, final FeatureMerger merger) {
    int size = 0;
    final StateRegistry reg = new StateRegistry(mdata.numOfClassifiers());

    // add relations for Bools and Ints
    if (mdata.hasBools() || true) {
      reg.addBools();
      size++;
    }
    if (mdata.hasIntsBoolsOrEnums()) {
      reg.addInts();
      size++;
    }

    // add relations for classes and their references and attributes
    for (final EClass eClass : mdata.classes()) {
      reg.addUnary(eClass.getName(), eClass);
      size++;
      for (final EReference ref : mdata.references(eClass)) {
        if (merger.isMerged(ref))
          reg.addState(eClass, ref, merger.state(ref));
        else
          reg.addBinary(eClass, ref);
        size++;
        if (!ref.isMany())
          reg.registerFunction(ref);
      }
      for (final EAttribute attr : mdata.attributes(eClass)) {
        if (attr.isMany())
          continue;
        if (merger.isMerged(attr))
          reg.addState(eClass, attr, merger.state(attr));
        else
          reg.addBinary(eClass, attr);
        size++;
        reg.registerFunction(attr);
      }
    }

    // add relations for enums
    for (final EEnum eenum : mdata.enums().allEEnums()) {
      reg.addEnum(eenum);
      size++;
    }
    
    // add relations for features
//    if (features != null && features.size() > 0) {
//    	reg.addFeatures();
//    }
    
    

    reg.allStates = new ArrayList<>(size);
    reg.allStates.addAll(reg.classifierRegistry.values());
    for (final Map<EStructuralFeature, StateRelation> featureStateMap : reg.featureRegistry.values()) {
      reg.allStates.addAll(featureStateMap.values());
    }
    assert reg.allStates.size() == size;
    return reg;
  }
  

  /**
   * @param eClass
   * @param feature
   * @return
   */
  private static String stateName(final EClass eClass, final EStructuralFeature feature) {
    return new StringBuffer(eClass.getName()).append("_").append(feature.getName()).toString();
  }

  public Relation bools() {
    return bools.relation();
  }

  Relation ints() {
    return ints.relation();
  }

  Relation preState(final EClassifier eClass) {
    if (eClass == null)
      throw new NullPointerException();
    if (classifierRegistry.containsKey(eClass))
      return classifierRegistry.get(eClass).preState();
    else
      return null;
  }

  Relation preState(final EClass eClass, final EStructuralFeature feature) {
    if (eClass == null || feature == null)
      throw new NullPointerException();
    if (featureRegistry.containsKey(eClass)) {
      final Map<EStructuralFeature, StateRelation> featureStateMap = featureRegistry.get(eClass);
      if (featureStateMap.containsKey(feature))
        return featureStateMap.get(feature).preState();
      else
        return null;
    } else
      return null;
  }

  Relation postState(final EClassifier eClass) {
    if (eClass == null)
      throw new NullPointerException();
    if (classifierRegistry.containsKey(eClass))
      return classifierRegistry.get(eClass).postState();
    else
      return null;
  }

  Relation postState(final EClass eClass, final EStructuralFeature feature) {
    if (eClass == null || feature == null)
      throw new NullPointerException();
    if (featureRegistry.containsKey(eClass)) {
      final Map<EStructuralFeature, StateRelation> featureStateMap = featureRegistry.get(eClass);
      if (featureStateMap.containsKey(feature))
        return featureStateMap.get(feature).postState();
      else
        return null;
    } else
      return null;
  }

  StateRelation state(final EClassifier eClass) {
    return classifierRegistry.get(eClass);
  }

  StateRelation state(final EClass eClass, final EStructuralFeature feature) {
    if (featureRegistry.containsKey(eClass) && featureRegistry.get(eClass).containsKey(feature))
      return featureRegistry.get(eClass).get(feature);
    else
      return null;
  }

  Collection<StateRelation> allStates() {
    return Collections.unmodifiableCollection(allStates);
  }

  private StateRelation addBinary(EClass eClass, final EStructuralFeature feature) {
    sanityCheck(feature);
    final StateRelation state = StateRelation.create(stateName(eClass, feature), BINARY);
    final Map<EStructuralFeature, StateRelation> featureStateMap;
    if (featureRegistry.containsKey(eClass)) {
      featureStateMap = featureRegistry.get(eClass);
    } else {
      featureStateMap = new LinkedHashMap<>(eClass.getEAllStructuralFeatures().size());
      featureRegistry.put(eClass, featureStateMap);
    }
    featureStateMap.put(feature, state);
    return state;
  }

  Collection<Relation> allPreStates() {
    final Collection<Relation> rs = new ArrayList<>(size());
    for (final StateRelation state : allStates)
      rs.add(state.preState());
    return Collections.unmodifiableCollection(rs);
  }

  Collection<Relation> allPostStates() {
    final Collection<Relation> rs = new ArrayList<>(size());
    for (final StateRelation state : allStates)
      rs.add(state.postState());
    return Collections.unmodifiableCollection(rs);
  }

  int size() {
    // int featureRegistrySize = 0;
    // for (final Entry<EClassifier, Map<EStructuralFeature, StateRelation>> e :
    // featureRegistry.entrySet())
    // featureRegistrySize += e.getValue().size();
    // return registry.size() + featureRegistrySize;
    return allStates.size();
  }

  /**
   * @param feature
   * @requires !ref.isMany()
   */
  private void registerFunction(final EStructuralFeature feature) {
    assert !feature.isMany();
    if (isPartialFunction(feature))
      partialFunctions.add(feature);
    else if (isFunction(feature))
      functions.add(feature);
  }

  /**
   * @param feature
   * @return
   */
  private boolean isPartialFunction(EStructuralFeature feature) {
    if (feature.isMany())
      return false;
    if (feature.getLowerBound() == 0)
      return true;
    return false;
  }

  /**
   * @param ref
   * @return
   */
  private boolean isFunction(EStructuralFeature feature) {
    if (feature.isMany())
      return false;
    if (feature.getLowerBound() == 1)
      return true;
    return false;
  }

  private Relation addInts() {
    ints = StateRelation.createStatic(INTS, UNARY);
    classifierRegistry.put(EcorePackage.Literals.EINT, ints);
    return ints.preState();
  }

  private Relation addBools() {
    bools = StateRelation.createStatic(BOOLS, UNARY);
    classifierRegistry.put(EcorePackage.Literals.EBOOLEAN, bools);
    return bools.preState();
  }

  /**
   * @param element
   */
  private void sanityCheck(final EObject element) {
    if (element == null)
      throw new NullPointerException();
    if (!(element instanceof EClassifier || element instanceof ETypedElement))
      throw new IllegalArgumentException();
  }

  /**
   * @param eClass
   * @param state
   * @param ref
   * @return 
   */
  private StateRelation addState(EClass eClass, final EStructuralFeature feature,
      final StateRelation state) {
    final Map<EStructuralFeature, StateRelation> featureStateMap;
    if (featureRegistry.containsKey(eClass)) {
      featureStateMap = featureRegistry.get(eClass);
    } else {
      featureStateMap = new LinkedHashMap<>(eClass.getEAllStructuralFeatures().size());
      featureRegistry.put(eClass, featureStateMap);
    }
    featureStateMap.put(feature, state);
    return state;
  }

  // // NOTE: This will be (probably) useful once array-attributes are supported
  // @SuppressWarnings("unused")
  // private StateRelation addNary(final String name, final ENamedElement
  // element, final int arity) {
  // check(element);
  // final StateRelation pair = StateRelation.create(name, arity);
  // registry.put(element, pair);
  // return pair;
  // }

  private StateRelation addUnary(final String name, final EClassifier element) {
    sanityCheck(element);
    final StateRelation pair = StateRelation.create(name, UNARY);
    classifierRegistry.put(element, pair);
    return pair;
  }

  /**
   * @param eenum
   * @return 
   */
  private StateRelation addEnum(EEnum eenum) {
    sanityCheck(eenum);
    final StateRelation enumState = StateRelation.createStatic(eenum.getName(), UNARY);
    classifierRegistry.put(eenum, enumState);
    return enumState;
  }
}
