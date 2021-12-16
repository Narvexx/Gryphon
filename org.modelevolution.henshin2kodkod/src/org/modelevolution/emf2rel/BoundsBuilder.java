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

import static org.modelevolution.gts2rts.util.EcoreUtil.isEBoolean;
import static org.modelevolution.gts2rts.util.EcoreUtil.isEEnum;
import static org.modelevolution.gts2rts.util.EcoreUtil.isEInt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import kodkod.ast.LeafExpression;
import kodkod.ast.Relation;
import kodkod.ast.Variable;
import kodkod.instance.Bounds;
import kodkod.instance.Tuple;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;
import kodkod.instance.Universe;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * @author Sebastian Gabmeyer
 * 
 */
final class BoundsBuilder {
  private static final String TRUE = "-1";
  private static final String FALSE = "0";
  private final Bounds bounds;
  private final TupleFactory factory;
  private StateRegistry registry;
  /**
   * This map collects the lower bounds of all model elements while iterating
   * through the instance's objects.
   */
  private final Map<Relation, TupleSet> lowers;
  private final Map<EClass, TupleSet> classUppers;
  private final Map<EObject, Integer> nameRegistry;

  private BoundsBuilder(ModelData data, final Universe univ, final StateRegistry registry) {
    this.bounds = new Bounds(univ);
    this.factory = univ.factory();
    this.registry = registry;
    /* no need to allocate space for enums; their bounds are already known */
    this.lowers = new IdentityHashMap<>(registry.size() - data.enums().size());
    this.classUppers = new IdentityHashMap<>(data.classes().size());
    this.nameRegistry = new IdentityHashMap<>();
  }

  static Bounds createBounds(final ModelData mdata, final StateRegistry registry,
      final Universe univ) {
    final BoundsBuilder bb = new BoundsBuilder(mdata, univ, registry);

    bb.buildIntsAndBools(mdata);
    bb.extractLowerBounds(mdata);
    bb.prepareClassUpperBounds(mdata, registry);
    bb.buildClassUpperBounds(mdata.classes());
    bb.buildEnumBounds(mdata, registry);
    bb.buildFeatureUpperBounds(mdata.features());
    
    mdata.initialState(bb.lowers);

    return bb.bounds;
  }
  
//  private void buildFeatureBounds(List<String> features) {
//	  final TupleSet boolTuples = factory.range(factory.tuple(TRUE), factory.tuple(FALSE));
//	  bounds.boundExactly(registry.bools(), boolTuples);  
//  }

  /**
   * @param mdata
   */
  private void buildIntsAndBools(ModelData mdata) {
    int numOfInts = mdata.numOfInts();
    /* bound each integer to its value */
    int minInt = mdata.minInt();
    for (int i = 0; i < numOfInts; ++i)
      bounds.boundExactly(minInt + i, factory.setOf(factory.tuple(1, i)));

    if (mdata.hasInts()) {
      final TupleSet intTuples = factory.range(factory.tuple(1, 0), factory.tuple(1, numOfInts - 1));
      bounds.boundExactly(registry.ints(), intTuples);
    }
    if (mdata.hasBools() || true) {
      final TupleSet boolTuples = factory.range(factory.tuple(TRUE), factory.tuple(FALSE));
      bounds.boundExactly(registry.bools(), boolTuples);
    }
  }

  /**
   * @param classes
   */
  private void buildClassUpperBounds(final Collection<EClass> classes) {
    for (final EClass c : classes) {
      // final TupleSet lower = lowers.containsKey(c) ? lowers.get(c) : factory
      // .noneOf(1);
      assert classUppers.containsKey(c);
      final TupleSet upper = classUppers.get(c);
      // bounds.bound(registry.relation(c), lower, upper);
      bounds.bound(registry.preState(c), upper);
      bounds.bound(registry.postState(c), upper);
    }
  }

  /**
   * @param mdata
   * @param registry
   */
  private void buildEnumBounds(final ModelData mdata, final StateRegistry registry) {
    for (final EEnum e : mdata.enums().allEEnums()) {
      // final Object[] literals = mdata.literalValues(e).toArray();
      final TupleSet upper = factory.noneOf(1);
      // for (int idx = 0; idx < mdata.literalValues(e).size(); idx++) {
      // upper.add(factory.tuple(1, idx));
      // }
      for (Integer lit : mdata.literalValues(e)) {
        upper.add(factory.tuple(lit.toString()));
      }
      bounds.boundExactly(registry.preState(e), upper);
      // bounds.boundExactly(registry.postState(e), upper);
    }
  }

  /**
   * @param featureMap
   */
  private void buildFeatureUpperBounds(final Map<EClass, Collection<EStructuralFeature>> featureMap) {

    for (final Entry<EClass, Collection<EStructuralFeature>> e : featureMap.entrySet()) {
      final EClass eClass = e.getKey();
      final Collection<EStructuralFeature> features = e.getValue();
      for (final EStructuralFeature feature : features) {
        final Relation hostRelation = registry.preState(eClass);
        final Relation refRelation;
        if (feature instanceof EReference) {
          final EReference reference = (EReference) feature;
          refRelation = registry.preState(reference.getEReferenceType());
        } else {
          assert feature instanceof EAttribute;
          final EAttribute attribute = (EAttribute) feature;
          if (isEBoolean(attribute)) {
            refRelation = registry.bools();
          } else if (isEInt(attribute)) {
            refRelation = registry.ints();
          } else {
            assert isEEnum(attribute);
            refRelation = registry.preState(attribute.getEType());
          }
        }
        final TupleSet hostAtoms = bounds.upperBound(hostRelation);
        final TupleSet refAtoms = bounds.upperBound(refRelation);
        final TupleSet upper = hostAtoms.product(refAtoms);
        final StateRelation state = registry.state(eClass, feature);

        if (bounds.upperBound(registry.preState(eClass, feature)) != null)
          upper.addAll(bounds.upperBound(registry.preState(eClass, feature)));

        bounds.bound(state.preState(), upper);
        bounds.bound(state.postState(), upper);
      }
    }
  }

  /**
   * @param mdata
   * @param registry
   */
  private void prepareClassUpperBounds(final ModelData mdata, final StateRegistry registry) {
    for (final EClass c : mdata.classes()) {
      final String name = registry.preState(c).name();
      final int upperObjBound = mdata.getUpperObjBound(c);
      final TupleSet upper;
      if (upperObjBound < 1)
        upper = factory.noneOf(1);
      else
        upper = buildClassUppers(name, upperObjBound);
      registerUpper(c, upper);
      registerAllUppers(c.getEAllSuperTypes(), upper);
    }
  }

  /**
   * @param c
   * @param upper
   */
  private void registerUpper(final EClass c, final TupleSet upper) {
    if (classUppers.containsKey(c)) {
      classUppers.get(c).addAll(upper);
    } else {
      // final TupleSet uppers = factory.setOf(upper);
      classUppers.put(c, upper);
    }
  }

  /**
   * @param allSuperTypes
   * @param upper
   */
  private void registerAllUppers(final Collection<EClass> allSuperTypes, final TupleSet upper) {
    for (final EClass type : allSuperTypes)
      registerUpper(type, upper.clone());
  }

  /**
   * @param name
   * @param upperObjBound
   * @return
   */
  private TupleSet buildClassUppers(final String name, final int upperObjBound) {
    // if (upperObjBound < 1)
    // return factory.noneOf(1);
    Tuple from = factory.tuple(name + "1");
    Tuple to = factory.tuple(name + upperObjBound);
    return factory.range(from, to);
  }

  private void extractLowerBounds(ModelData mdata) {
    final List<EObject> worklist = new ArrayList<>();
    final Resource instance = mdata.instance();
    worklist.add(instance.getContents().get(0));

    int index = 0;
    while (index < worklist.size()) {
      final EObject obj = worklist.get(index);
      final String objName = name(obj);
      final Tuple tuple = factory.tuple(objName);
      final EClass eClass = obj.eClass();
      registerLower(eClass, tuple);
      for (final EClass supereClass : mdata.superclasses(eClass)) {
        registerLower(supereClass, tuple);
      }

      registerAllAttributeLowers(obj, objName, mdata);
      registerAllReferenceLowers(obj, objName, mdata);

      worklist.addAll(obj.eContents());
      index += 1;
    }
  }

  /**
   * @param obj
   * @param tuple
   */
  private void registerLower(final EClassifier obj, final Tuple tuple) {
    registerLower(obj, factory.setOf(tuple));
  }

  private void registerLower(final EClass eClass, final EStructuralFeature feature,
      final Tuple tuple) {
    registerLower(eClass, feature, factory.setOf(tuple));
  }

  /**
   * @param classifier
   * @param tuple
   */
  private void registerLower(final EClassifier classifier, final TupleSet tuples) {
    final Relation r = registry.preState(classifier);
    registerLower(r, tuples);
  }

  private void registerLower(final EClass eClass, final EStructuralFeature feature,
      final TupleSet tuples) {
    final Relation r = registry.preState(eClass, feature);
    registerLower(r, tuples);
  }

  /**
   * @param r
   * @param tuples
   */
  void registerLower(final Relation r, final TupleSet tuples) {
    assert r != null;
    if (lowers.containsKey(r)) {
      lowers.get(r).addAll(tuples);
    } else {
      lowers.put(r, tuples);
    }
  }

  private void registerAllAttributeLowers(final EObject obj, final String objName, ModelData mdata) {
    final EClass eClass = obj.eClass();
    for (final EAttribute attr : mdata.attributes(eClass)) {
      final Object value;
      if (isEBoolean(attr)) {
        value = ((Boolean) obj.eGet(attr)) ? TRUE : FALSE;
      } else if (isEInt(attr)) {
        value = ((Integer) obj.eGet(attr)).toString();
      } else if (isEEnum(attr)) {
        final EEnum eenum = (EEnum) attr.getEType();
        value = mdata.lookupEnumValue(eenum, ((EEnumLiteral) obj.eGet(attr)).getLiteral())
                     .toString();
        // String value2 = eenum.getEEnumLiteral((String)
        // obj.eGet(attr)).getLiteral();
      } else {
        value = null;
        assert false;
      }
      final Tuple tuple = factory.tuple(objName, value);
      registerLower(eClass, attr, tuple);
      for (final EClass superClass : mdata.superclasses(eClass)) {
        assert superClass.isSuperTypeOf(eClass);
        final EStructuralFeature superAttr = superClass.getEStructuralFeature(attr.getName());
        if (superAttr != null) {
          assert superAttr == attr;
          registerLower(superClass, superAttr, tuple);
        }
      }
    }
  }

  /**
   * @param obj
   * @param objName
   * @param mdata
   */
  @SuppressWarnings("unchecked")
  private void registerAllReferenceLowers(final EObject obj, final String objName,
      final ModelData mdata) {
    final EClass eClass = obj.eClass();
    for (final EReference ref : mdata.references(eClass)) {
      assert registry.preState(eClass, ref) != null;

      final List<EObject> targetObjs;
      if (ref.isMany()) {
        targetObjs = (List<EObject>) obj.eGet(ref);
      } else {
        final EObject tgtObj = (EObject) obj.eGet(ref);
        targetObjs = (tgtObj == null ? (List<EObject>) Collections.EMPTY_LIST
                                    : Collections.singletonList(tgtObj));
      }
      if (targetObjs.size() > 0) {
        final TupleSet tuples = factory.noneOf(2);
        for (final EObject tgtObj : targetObjs) {
          tuples.add(factory.tuple(objName, name(tgtObj)));
        }
        registerLower(eClass, ref, tuples);

        for (final EClass superClass : mdata.superclasses(eClass)) {
          assert superClass.isSuperTypeOf(eClass);
          final EStructuralFeature superRef = superClass.getEStructuralFeature(ref.getName());
          if (superRef != null) {
            assert ref == superRef;
            registerLower(superClass, superRef, tuples.clone());
          }
        }
      }
    }
  }

  /**
   * @param obj
   * @return
   */
  private String name(final EObject obj) {
    if (nameRegistry.containsKey(obj))
      return obj.eClass().getName() + nameRegistry.get(obj);

    final int cnt;
    if (nameRegistry.containsKey(obj.eClass()))
      cnt = nameRegistry.get(obj.eClass()) + 1;
    else
      cnt = 1;
    // update the instance counter of the class
    nameRegistry.put(obj.eClass(), cnt);
    // create entry for the new object/counter pair
    nameRegistry.put(obj, cnt);
    return obj.eClass().getName() + nameRegistry.get(obj);
  }

  // private void collectSuperClassBounds(final EClass eClass) {
  // for (final EClass supereClass : eClass.getEAllSuperTypes()) {
  // if (inheritanceBounds.containsKey(supereClass)) {
  // inheritanceBounds.get(supereClass).add(eClass);
  // } else {
  // final ArrayList<EClass> subClasses = new ArrayList<EClass>();
  // subClasses.add(eClass);
  // inheritanceBounds.put(supereClass, subClasses);
  // }
  // }
  // }

}
