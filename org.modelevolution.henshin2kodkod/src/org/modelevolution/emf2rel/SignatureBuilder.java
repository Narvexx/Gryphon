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

//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.IdentityHashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import kodkod.ast.Formula;
//import kodkod.ast.Relation;
//import kodkod.instance.Bounds;
//import kodkod.instance.Tuple;
//import kodkod.instance.TupleFactory;
//import kodkod.instance.TupleSet;
//import kodkod.instance.Universe;
//
//import org.eclipse.emf.ecore.EAttribute;
//import org.eclipse.emf.ecore.EClass;
//import org.eclipse.emf.ecore.EClassifier;
//import org.eclipse.emf.ecore.EDataType;
//import org.eclipse.emf.ecore.EEnum;
//import org.eclipse.emf.ecore.EEnumLiteral;
//import org.eclipse.emf.ecore.EObject;
//import org.eclipse.emf.ecore.EPackage;
//import org.eclipse.emf.ecore.EReference;
//import org.eclipse.emf.ecore.EcorePackage;
//import org.eclipse.emf.ecore.resource.Resource;

/**
 * This class represents a relational signature that consists of a set of relations and a set of
 * corresponding bounds. It offers methods to access these relations and bounds. Further, it offers
 * methods that generate a Signature from an Ecore model and an instance model where the latter captures the
 * lower bounds.
 * 
 * <p>
 * The translation does not support:
 * <ol>
 * <li>many-valued attributes
 * <li>attributes of primitive types other than <code>EInt</code> or <code>EBoolean</code>. Note
 * that enumeration types are supported.
 * </ol>
 * 
 * @author Sebastian Gabmeyer
 * 
 */
@Deprecated
final class SignatureBuilder {
  /*
   * private final class FunctionProxy {
   * 
   * private final Relation domain; private final EClass rangeType; private final Relation relation;
   * 
   * public FunctionProxy(final Relation relation, final Relation domain, final EClass rangeType) {
   * this.relation = relation; this.domain = domain; this.rangeType = rangeType; }
   * 
   * Formula toFunction() { final Relation range = registry.get(rangeType); if (range == null) throw
   * new IllegalStateException(); return relation.function(domain, range); }
   * 
   * }
   */

//  private final EPackage pkg;
//  private final Resource inst;
//  private final UniverseBuilder uBuilder;
//  private final Map<EObject, Relation> registry = new IdentityHashMap<>();
//  private final List<EObject[]> funcs = new ArrayList<>();
//  private final Map<Relation, List<String[]>> binaryLowerBounds = new IdentityHashMap<>();
//  private final Map</* EObject */Relation, List<EObject[]>> binaryUpperBounds = new IdentityHashMap<>();
//  //private final Map<EStructuralFeature, Relation> mergedFeatures;
//  private final FeatureMerger merger;
//  private final Map<EClass, List<EClass>> inheritanceBounds = new IdentityHashMap<>();
//  private boolean hasInts;
//  private boolean hasBools;

//  /**
//   * @param classes TODO
//   * @param enums TODO
//   * @param merger TODO
//   */
//  SignatureBuilder(final EPackage pkg, List<EClass> classes, List<EEnum> enums, final Resource instance, final UniverseBuilder uBuilder, FeatureMerger merger) {
//    this.pkg = pkg;
//    this.inst = instance;
//    this.uBuilder = uBuilder;
//    //this.mergedFeatures = mergedFeatures;
//    this.merger = merger;
//  }
//
//  Collection<Relation> relations() {
//    return registry.values();
//  }
//
//  /**
//   * @param type
//   * @return
//   * @throws IllegalArgumentException
//   *           iff !this.registry.containsKey(type)
//   */
//  Relation relation(final EObject type) {
//    if (registry.containsKey(type)) { return registry.get(type); }
//    throw new IllegalArgumentException();
//  }
//
//  /**
//   * @param modelpath
//   * @param instancepath
//   * @return
//   * @throws IllegalStateException
//   *           iff (\exists c: pkg.getEClassifiers() | c instanceof EDataType)
//   * @throws IllegalStateException
//   *           iff (\exists a: pkg.getEClassifiers().getEAttributes() | a.isMany() ||
//   *           (a.getEAttributeType != EBoolean && a.getEAttributeType != EInt))
//   * @throws IOException
//   *           iff the {@link Resource} fails to load from <code>instancepath</code>.
//   */
//  Signature build() throws IOException {
//    ModelInfo info = null;
//    // final EPackage pkg = loadModel(modelpath);
//    // final Resource inst = loadInstance(pkg, instancepath);
//    PairedRelationRegistry rr = PairedRelationRegistry.create(info , merger);
////    createRelations(pkg);
////    if (hasInts) {
////      //createAndRegisterInts();
////    }
////    if (hasBools) {
////      //createAndRegisterBools();
////    }
//
//    assert Arrays.asList(uBuilder.lowerObjBounds().keySet().toArray(),
//        uBuilder.enumBounds().keySet().toArray()).containsAll(registry.keySet());
//    assert registry.keySet().containsAll(uBuilder.lowerObjBounds().keySet())
//        && registry.keySet().containsAll(uBuilder.enumBounds().keySet())
//        && registry.size() == uBuilder.lowerObjBounds().size() + uBuilder.enumBounds().size();
//
//    initializeLowerBounds(inst);
//    final Universe univ = uBuilder.univ();
//    final Bounds bounds = createBounds(univ);
//    final Map<Relation, Relation> nextRegistry = createNextRelations(registry.values(), bounds);
//    final Formula funcDecls = buildConstraints();
//    return new Signature(univ, bounds, funcDecls, registry, nextRegistry, uBuilder.nameRegistry());
//  }

//  /**
//   * @param relations
//   * @param bounds
//   * @return
//   */
//  private Map<Relation, Relation> createNextRelations(Collection<Relation> relations, Bounds bounds) {
//    final int size = relations.size() - (hasBools ? 1 : 0) - (hasInts ? 1 : 0);
//    final Map<Relation, Relation> nextRegistry = new IdentityHashMap<>(size);
//    final Iterator<Relation> it = relations.iterator();
//    // do not create next relations for Ints and Bools
//    if(hasInts) it.next();
//    if(hasBools) it.next();
//    
//    while (it.hasNext()) {
//      final Relation currentR = it.next();
//      final Relation nextR = Relation.nary(currentR.name() + "'", currentR.arity());
//      nextRegistry.put(currentR, nextR);
//      bounds.bound(nextR, bounds.upperBound(currentR));
//    }
//    return nextRegistry;
//  }

//  /**
//   * 
//   */
//  private void createAndRegisterBools() {
//    final Relation bool = Relation.unary("Bools");
//    registry.put(EcorePackage.Literals.EBOOLEAN, bool);
//  }
//
//  /**
//   * 
//   */
//  private void createAndRegisterInts() {
//    final Relation ints = Relation.unary("Ints");
//    registry.put(EcorePackage.Literals.EINT, ints);
//  }

//  /**
//   * @param pkg
//   */
//  private void createRelations(final EPackage pkg) {
//    for (final EClassifier ec : pkg.getEClassifiers()) {
//      if (ec instanceof EClass) {
//        final EClass clazz = (EClass) ec;
////        createAndRegisterClass(clazz);
////        collectSuperClassBounds(clazz);
//        createAndRegisterReferences(clazz);
//        createAndRegisterAttributes(clazz);
//      } else if (ec instanceof EEnum) {
//        final EEnum eenum = (EEnum) ec;
//        createRegisterAndBound(eenum);
//      } else if (ec instanceof EDataType) { throw new IllegalStateException(); }
//    }
//  }
//
//  private Bounds createBounds(final Universe univ) {
//    final Bounds bounds = new Bounds(univ);
//    final TupleFactory tf = univ.factory();
//
//    // bound integers and booleans
//    addIntsAndBools(bounds, tf);
//    // bound classes
//    addClassBounds(bounds, tf);
//    // bound enums
//    addEnumBounds(bounds, tf);
//    // bound super classes
//    addSuperClassBounds(bounds, tf);
//    // bound references and attributes
//    addAttributeAndReferenceBounds(bounds, tf);
//
//    return bounds;
//  }
//
//  /**
//   * @param bounds
//   * @param tf
//   */
//  private void addIntsAndBools(final Bounds bounds, final TupleFactory tf) {
//    int numOfInts = 0;
//    if (hasInts) {
//      assert registry.containsKey(EcorePackage.Literals.EINT);
//      assert uBuilder.bitwidth() > 0;
//
//      final Relation ints = registry.get(EcorePackage.Literals.EINT);
//
//      numOfInts = 1 << uBuilder.bitwidth();
//      final int lowInt = 0 - (1 << (uBuilder.bitwidth() - 1));
//      //final int maxInt = ~lowInt;
//      final TupleSet intTuples = tf.range(tf.tuple(1, 0), tf.tuple(1, numOfInts - 1));
//      bounds.boundExactly(ints, intTuples);
//      for (int i = 0; i < numOfInts; ++i)
//        bounds.boundExactly(lowInt + i, tf.setOf(tf.tuple(1, i)));
//    }
//
//    if (hasBools) {
//      assert registry.containsKey(EcorePackage.Literals.EBOOLEAN);
//
//      final Relation bool = registry.get(EcorePackage.Literals.EBOOLEAN);
//      // Note: the Boolean tuples start right after the int tuples @see UniverseBuilder#univ()
//      final TupleSet boolTuples = tf.range(tf.tuple(1, numOfInts), tf.tuple(1, numOfInts + 1));
//      bounds.boundExactly(bool, boolTuples);
//    }
//  }
//
//  /**
//   * @param bounds
//   * @param tf
//   */
//  private void addAttributeAndReferenceBounds(final Bounds bounds, final TupleFactory tf) {
//    assert bounds != null;
//    assert tf != null;
//    for (final Entry<Relation, List<EObject[]>> e : binaryUpperBounds.entrySet()) {
//      final TupleSet srcUpperTuples = tf.noneOf(1);
//      final TupleSet tgtUpperTuples = tf.noneOf(1);
//      for (final EObject[] obj : e.getValue()) {
//        assert registry.containsKey(obj[0]);
//        assert registry.containsKey(obj[1]);
//
//        final Relation srcR = registry.get(obj[0]);
//        final Relation tgtR = registry.get(obj[1]);
//
//        assert bounds.relations().contains(srcR);
//        assert bounds.relations().contains(tgtR);
//        srcUpperTuples.addAll(bounds.upperBound(srcR));
//        tgtUpperTuples.addAll(bounds.upperBound(tgtR));
//      }
//      final TupleSet upper = srcUpperTuples.product(tgtUpperTuples);
//      final TupleSet lower = tf.noneOf(2);
//      if (binaryLowerBounds.containsKey(e.getKey())) {
//        for (final String[] bound : binaryLowerBounds.get(e.getKey())) {
//          lower.add(tf.tuple((Object[]) bound));
//        }
//      }
//      bounds.bound(e.getKey(), lower, upper);
//    }
//  }
//
//  /**
//   * @param bounds
//   * @param tf
//   */
//  private void addSuperClassBounds(final Bounds bounds, final TupleFactory tf) {
//    assert bounds != null;
//    assert tf != null;
//    for (final Entry<EClass, List<EClass>> e : inheritanceBounds.entrySet()) {
//      assert registry.containsKey(e.getKey());
//      assert registry.keySet().containsAll(e.getValue());
//
//      final TupleSet ts = tf.noneOf(1);
//      for (final EClass sub : e.getValue()) {
//        assert bounds.upperBound(registry.get(sub)) == null ? sub.isAbstract() : true;
//        assert sub.isAbstract() ? bounds.upperBound(registry.get(sub)) == null : true;
//
//        if (sub.isAbstract()) continue;
//
//        ts.addAll(bounds.upperBound(registry.get(sub)));
//      }
//      bounds.boundExactly(registry.get(e.getKey()), ts);
//    }
//  }
//
//  /**
//   * @param bounds
//   * @param tf
//   */
//  private void addEnumBounds(final Bounds bounds, final TupleFactory tf) {
//    assert bounds != null;
//    assert tf != null;
//    for (final Entry<EEnum, List<String>> e : uBuilder.enumBounds().entrySet()) {
//      assert registry.containsKey(e.getKey());
//
//      final Tuple from = tf.tuple(e.getValue().get(0));
//      final Tuple to = tf.tuple(e.getValue().get(e.getValue().size() - 1));
//      final TupleSet ts = tf.range(from, to);
//      bounds.boundExactly(registry.get(e.getKey()), ts);
//    }
//  }
//
//  /**
//   * @param bounds
//   * @param tf
//   */
//  private void addClassBounds(final Bounds bounds, final TupleFactory tf) {
//    assert bounds != null;
//    assert tf != null;
//    for (final Entry<EClass, Integer> e : uBuilder.lowerObjBounds().entrySet()) {
//      assert registry.containsKey(e.getKey());
//
//      // if(!e.getKey().isAbstract() && e.getValue() == 0) {
//      // throw new IllegalStateException(e.getKey() + " is unbounded.");
//      // }
//      final TupleSet ts;
//      if (e.getValue() > 0) {
//        final Tuple from = tf.tuple(e.getKey().getName() + "1");
//        final Tuple to = tf.tuple(e.getKey().getName() + e.getValue());
//        ts = tf.range(from, to);
//      } else {
//        ts = tf.noneOf(1);
//      }
//      bounds.boundExactly(registry.get(e.getKey()), ts);
//    }
//  }
//
//  /**
//   * @param inst
//   */
//  private void initializeLowerBounds(final Resource inst) {
//    final List<EObject> worklist = new ArrayList<>();
//    worklist.add(inst.getContents().get(0));
//
//    int index = 0;
//    while (index < worklist.size()) {
//      final EObject obj = worklist.get(index);
//      final EClass clazz = obj.eClass();
//
//      assert registry.containsKey(clazz);
//
//      final String objName = uBuilder.name(obj);
//      // unaryBounds.get(clazz).add(objName);
//      collectLowerAttributeBounds(obj, objName);
//      collectLowerReferenceBounds(obj, objName);
//
//      worklist.addAll(obj.eContents());
//      index += 1;
//    }
//  }
//
//  /**
//   * @param obj
//   * @param objName
//   */
//  private void collectLowerAttributeBounds(EObject obj, final String objName) {
//    final EClass clazz = obj.eClass();
//    for (final EAttribute attr : clazz.getEAttributes()) {
//      if(attr.isMany())
//        continue;
//      assert registry.containsKey(attr);
//
//      final String value;
//      if (attr.getEType() == EcorePackage.Literals.EBOOLEAN) {
//        value = String.valueOf((Boolean) obj.eGet(attr));
//      } else if (attr.getEType() == EcorePackage.Literals.EINT) {
//        value = String.valueOf((Integer) obj.eGet(attr));
//      } else if (attr.getEType() == EcorePackage.Literals.EENUM) {
//        final EEnum eenum = (EEnum) attr.getEType();
//        value = eenum.getEEnumLiteral((Integer) obj.eGet(attr)).getLiteral();
//      } else {
//        throw new IllegalStateException();
//      }
//      final List<String[]> bounds;
//      if (binaryLowerBounds.containsKey(registry.get(attr))) {
//        bounds = binaryLowerBounds.get(registry.get(attr));
//      } else {
//        bounds = new ArrayList<>();
//        binaryLowerBounds.put(registry.get(attr), bounds);
//      }
//      bounds.add(new String[] { objName, value });
//    }
//  }
//
//  /**
//   * @param obj
//   * @param objName
//   */
//  @SuppressWarnings("unchecked")
//  private void collectLowerReferenceBounds(final EObject obj, final String objName) {
//    final EClass clazz = obj.eClass();
//    for (final EReference ref : clazz.getEReferences()) {
//      assert registry.containsKey(ref);
//      final Relation refR = registry.get(ref);
//
//      final List<EObject> refs;
//      if (ref.isMany()) {
//        refs = (List<EObject>) obj.eGet(ref);
//      } else {
//        final EObject tgtObj = (EObject) obj.eGet(ref);
//        refs = (tgtObj == null ? (List<EObject>) Collections.EMPTY_LIST : Collections
//            .singletonList(tgtObj));
//      }
//      for (final EObject targetObj : refs) {
//        final List<String[]> bounds;
//        if (binaryLowerBounds.containsKey(refR)) {
//          bounds = binaryLowerBounds.get(refR);
//        } else {
//          bounds = new ArrayList<>();
//          binaryLowerBounds.put(refR, bounds);
//        }
//        bounds.add(new String[] { objName, uBuilder.name(targetObj) });
//      }
//    }
//  }
//  
//  /**
//   * @param clazz
//   */
//  private void collectSuperClassBounds(final EClass clazz) {
//    for (final EClass superClazz : clazz.getEAllSuperTypes()) {
//      if (inheritanceBounds.containsKey(superClazz)) {
//        inheritanceBounds.get(superClazz).add(clazz);
//      } else {
//        final ArrayList<EClass> subClasses = new ArrayList<EClass>();
//        subClasses.add(clazz);
//        inheritanceBounds.put(superClazz, subClasses);
//      }
//    }
//  }
//
//  /**
//   * @param eenum
//   */
//  private void createRegisterAndBound(final EEnum eenum) {
//    final Relation eenumR = Relation.unary(eenum.getName());
//    registry.put(eenum, eenumR);
//    final List<String> literals = new ArrayList<>(eenum.getELiterals().size());
//    for (final EEnumLiteral l : eenum.getELiterals()) {
//      literals.add(l.getName());
//    }
//    uBuilder.register(eenum);
//    // we can already set the bounds for enums here
//    // unaryBounds.put(eenum, literals);
//  }
//
//  /**
//   * @param clazz
//   * @return
//   */
//  private void createAndRegisterClass(final EClass clazz) {
//    final Relation clazzR = Relation.unary(clazz.getName());
//    registry.put(clazz, clazzR);
//    uBuilder.register(clazz);
//  }
//
//  /**
//   * @param clazz
//   */
//  private void createAndRegisterAttributes(final EClass clazz) {
//    for (final EAttribute attr : clazz.getEAttributes()) {
//      // skip many-valued attributes, ie, arrays 
//      if(attr.isMany())
//        continue;
//      
//      final Relation attrR;
//      final List<EObject[]> bounds;
//      if(merger.isMerged(attr)) {
//        attrR = merger.relation(attr);
//      } else {
//        attrR = Relation.binary(clazz.getName() + "_" + attr.getName());
//      }
//      registry.put(attr, attrR);
//      if(binaryUpperBounds.containsKey(attrR)) {
//        bounds = binaryUpperBounds.get(attrR);
//      } else {
//        bounds = new ArrayList<>();
//        binaryUpperBounds.put(attrR, bounds);
//      }
//      if (attr.getEAttributeType() == EcorePackage.Literals.EINT) {
//        hasInts = true;
//        bounds.add(new EObject[] { clazz, EcorePackage.Literals.EINT });
//      } else if (attr.getEAttributeType() == EcorePackage.Literals.EBOOLEAN) {
//        hasBools = true;
//        bounds.add(new EObject[] { clazz, EcorePackage.Literals.EBOOLEAN });
//      } else if (attr.getEAttributeType() instanceof EEnum) {
//        bounds.add(new EObject[] { clazz, attr.getEAttributeType() });
//      } else {
//        throw new IllegalStateException();
//      }
//      assert !attr.isMany();
//      if(!attr.isMany()) {
//        funcs.add(new EObject[] { attr, clazz, attr.getEAttributeType() });
//      }
//    }
//  }
//  
//  private void createAndRegisterReferences(final EClass srcClass) {
//    for (final EReference ref : srcClass.getEReferences()) {
//      final EClass tgtClass = ref.getEReferenceType();
//      if (merger.isMerged(ref)) {
//        final Relation mergedR = merger.relation(ref);
//        registry.put(ref, mergedR);
//        final List<EObject[]> bounds;   
//        if(binaryUpperBounds.containsKey(mergedR)) {
//          bounds = binaryUpperBounds.get(mergedR);
//        } else {
//          bounds = new ArrayList<>();
//          binaryUpperBounds.put(mergedR, bounds);
//        }
//        bounds.add(new EObject[] { srcClass, tgtClass });
//      } else {
//        final Relation refR = Relation.binary(srcClass.getName() + "_" + ref.getName());
//        registry.put(ref, refR);
//        final List<EObject[]> bounds = Collections.singletonList(new EObject[] { srcClass, tgtClass });
//        binaryUpperBounds.put(/* ref */refR, bounds);
//        if (!ref.isMany()) {
//          /* constraints.add(refR.function(srcClazzR, registry.get(tgtClazz))); */
//          funcs.add(new EObject[] { ref, srcClass, tgtClass });
//        }
//      }
////      if (ref.getEOpposite() != null && !registry.containsKey(ref.getEOpposite())) {
////        final Relation oppR = Relation.binary(tgtClass.getName() + "_" + ref.getEOpposite().getName());
////        registry.put(ref.getEOpposite(), oppR);
////        binaryUpperBounds.put(/* ref.getEOpposite() */oppR,
////            new EObject[] { tgtClass, srcClass });
////        // final Formula f = invRefRel.eq(refR.transpose());
////        // constraints.add(f);
////      }
//    }
//  }
//  
//  private Formula buildConstraints() {
//    final Formula f = Formula.TRUE;
//    for (final EObject[] c : funcs) {
//      final EObject ref = c[0];
//      final EObject srcClass = c[1];
//      final EObject tgtClass = c[2];
//      f.and(registry.get(ref).function(registry.get(srcClass), registry.get(tgtClass)));
//    }
//    return f;
//  }
}
