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
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import kodkod.util.collections.IdentityHashSet;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.modelevolution.emf2rel.FeatureOmitter.FilteredFeatures;
import org.modelevolution.gts2rts.util.IntUtil;

/**
 * @author Sebastian Gabmeyer
 * 
 */
final class ModelAnalyzer {
  // private final Resource instance;
  private boolean hasBools = false;
  private boolean hasInts = false;
  private boolean hasEnums = false;
  private int bitwidth;
  private final FeatureOmitter omitter;

  // private final Set<EClass> classes = new IdentityHashSet<>();
  // private final Map<EEnum, List<String>> enums = new IdentityHashMap<>();
  // private final Map<EClass, Integer> lowerObjBounds;
  // private final Map<EClass, Integer> upperObjBounds;

  private ModelAnalyzer(FeatureOmitter omitter, int bitwidth) {
    this.omitter = omitter;
    this.bitwidth = bitwidth;
  }

  /**
   * @param omitter
   * @param bitwidth
   * 
   */
  static ModelData analyze(final EPackage model, final Resource instance,
      final Map<EClass, Integer> upperObjBounds, FeatureOmitter omitter, final int bitwidth) {
    // this.instance = instance;
    assert model != null;
    assert instance != null;
    assert upperObjBounds != null;

    final ModelAnalyzer analyzer = new ModelAnalyzer(omitter, bitwidth);

    Map<EClass, Integer> lowerObjBounds = analyzer.lowerObjBoundsFrom(instance);
    analyzer.updateUpperBounds(lowerObjBounds, upperObjBounds);
    
    assert upperObjBounds.keySet().containsAll(lowerObjBounds.keySet());
    
    final ModelComponents components = analyzer.extractCheckAnalyze(model);

    return new ModelData(instance, components.classes(), components.enums(),
                         components.featureMap(), components.inOutRefMap(),
                         components.superClassMap(), lowerObjBounds, upperObjBounds,
                         analyzer.hasInts, analyzer.hasBools, analyzer.hasEnums, analyzer.bitwidth);
  }

  /**
   * @param upperObjBounds
   * @return
   * @assume upperObjBounds.keySet().containsAll(lowerObjBounds.keySet())
   */
  private Map<EClass, Integer> updateUpperBounds(final Map<EClass, Integer> lowerObjBounds,
      final Map<EClass, Integer> upperObjBounds) {
    assert upperObjBounds.keySet().containsAll(lowerObjBounds.keySet());
    for (final EClass c : upperObjBounds.keySet()) {
      if (lowerObjBounds.containsKey(c))
        if (lowerObjBounds.get(c) > upperObjBounds.get(c))
          upperObjBounds.put(c, lowerObjBounds.get(c));
    }

    return upperObjBounds;
  }

  /**
   * Extract all {@link EClass EClasses} and {@link EEnum EEnums} from the
   * <code>model</code> and check if no other {@link EClassifier classifiers}
   * are contained in the model; and analyze if some EClass contains
   * {@link EcorePackage.Literals#EBOOLEAN boolean} or
   * {@link EcorePackage.Literals#EINT integer} attributes.
   * 
   * @param model
   * @return
   */
  private ModelComponents extractCheckAnalyze(final EPackage model) {
    final ModelComponents cs = new ModelComponents();
    for (final EClassifier classifier : model.getEClassifiers()) {
      if (classifier instanceof EClass) {
        final EClass eClass = (EClass) classifier;
        final FilteredFeatures filteredFeatures = omitter.filter(eClass.getEAllStructuralFeatures());
        anlyzeAttributes(filteredFeatures.attributes());

        cs.addClassAndRegisterReferences(eClass, filteredFeatures.references());
        cs.addFeatures(eClass, filteredFeatures.features());
      } else if (classifier instanceof EEnum) {
        final EEnum eenum = (EEnum) classifier;
        hasEnums = true;
        // List<String> literals = extractLiterals(eenum);
        cs.addEnum(eenum);
      } else {
        throw new IllegalArgumentException();
      }
    }
    return cs;
  }

  // /**
  // * @param features
  // * @return
  // */
  // private Collection<EStructuralFeature>
  // filter(Collection<EStructuralFeature> features) {
  // final Collection<EStructuralFeature> filtered = new
  // ArrayList<>(features.size() >> 2);
  // for (final EStructuralFeature a : features) {
  // if (omitter.isOmitted(a))
  // continue;
  // filtered.add(a);
  // }
  // return filtered;
  // }

  /**
   * @param attributes
   *          TODO
   */
  private void anlyzeAttributes(Collection<EAttribute> attributes) {
    for (final EAttribute attr : attributes) {
      if (attr.isMany())
        throw new IllegalArgumentException("Multi-valued attributes are not "
            + "supported. Attribute: " + attr.getName());
      if (isEInt(attr)) {
        if (bitwidth < 1)
          throw new IllegalArgumentException("bitwidth < 1.");
        hasInts = true;
      } else if (isEBoolean(attr)) {
        hasBools = true;
        adjustBitwidth(2);
      } else if (!isEEnum(attr))
        throw new IllegalArgumentException("The type of EAttribute " + attr.getName() + "(EClass: "
            + attr.getEContainingClass().getName() + ") is not supported.");
    }
  }

  // /**
  // * @param eenum
  // * @return
  // */
  // private List<String> extractLiterals(final EEnum eenum) {
  // final EList<EEnumLiteral> lits = eenum.getELiterals();
  // final List<String> literalNames = new ArrayList<>(lits.size());
  // for (final EEnumLiteral l : lits) {
  // literalNames.add(l.getLiteral());
  // }
  // return literalNames;
  // }

  /**
   * This method adjusts the bitwidth such that the required number of integers
   * can be represented by integers in the range
   * <code>-(2 ^ (bitwidth - 1))</code> to <code>(2 ^ (bitwidth - 1)) - 1</code>
   * . The method returns the minimal bitwidth necessary to represent the
   * required number of ints (<code>reqNumOfInts</code>). As a side-effect the
   * method updates this.bitwidth accordingly.
   * 
   * @param size
   * @return
   */
  private int adjustBitwidth(final int reqNumOfInts) {
    bitwidth = IntUtil.checkCapacity(bitwidth, reqNumOfInts) ? bitwidth
        : IntUtil.requiredNumOfBits(reqNumOfInts);
    return bitwidth;
  }

  /**
   * Builds a map of {@link EClass classes} associating each class with the
   * number of instance objects occurring in the <code>instance</code> model;
   * counting starts at one.
   * 
   * @param instance
   *          - the instance model
   * @return a map of classes and number of object instances
   */
  private Map<EClass, Integer> lowerObjBoundsFrom(final Resource instance) {
    final Map<EClass, Integer> lowerObjBounds;
    lowerObjBounds = new IdentityHashMap<>();
    final TreeIterator<EObject> it;
    for (it = instance.getAllContents(); it.hasNext();) {
      final EObject obj = it.next();
      if (lowerObjBounds.containsKey(obj.eClass())) {
        int cnt = lowerObjBounds.get(obj.eClass()) + 1;
        lowerObjBounds.put(obj.eClass(), cnt);
      } else {
        lowerObjBounds.put(obj.eClass(), 1);
      }
    }
    return lowerObjBounds;
  }

  //
  // private boolean hasIntsBoolsOrEnums() {
  // return hasInts || hasBools || hasEnums;
  // }

  // /**
  // * Counts objects per EClass, starts counting at one.
  // *
  // * @param obj
  // * @return
  // */
  // private static int registerObject(final EObject obj) {
  // final int cnt;
  // if (lowerObjBounds.containsKey(obj.eClass())) {
  // cnt = lowerObjBounds.get(obj.eClass()) + 1;
  // lowerObjBounds.put(obj.eClass(), cnt);
  // } else {
  // cnt = 1;
  // lowerObjBounds.put(obj.eClass(), cnt);
  // }
  // return cnt;
  // }

  /**
   * A container class that collects {@link EClass classes} and a map of
   * {@link EEnum enums} associated with the list of literal names.
   * 
   * @author Sebastian Gabmeyer
   * 
   */
  private final class ModelComponents {
    // private final Map<EEnum, List<String>> enums = new IdentityHashMap<>();
    private final Enums enums = new Enums();
    private final Collection<EClass> classes = new IdentityHashSet<>();
    private final Map<EClass, Collection<EStructuralFeature>> featureMap = new IdentityHashMap<>();
    private final Map<EClass, List<EClass>> superMap = new IdentityHashMap<>();
    private final Map<EClass, InOutRefs> inOutRefMap = new IdentityHashMap<>();

    // private final Set<EClass> detectedClasses;

    /**
     * 
     */
    ModelComponents() {
      // this.detectedClasses = detectedClasses;
    }

    Enums enums() {
      return enums;
    }

    // /**
    // * @param completeUpperObjBounds
    // */
    // void reduce(Map<EClass, Integer> completeUpperObjBounds) {
    // for (EClass c : classes) {
    // if (completeUpperObjBounds.get(c) == 0)
    // classes.remove(c);
    // }
    // }

    Collection<EClass> classes() {
      return classes;
    }

    Map<EClass, Collection<EStructuralFeature>> featureMap() {
      return featureMap;
    }

    Map<EClass, List<EClass>> superClassMap() {
      return superMap;
    }

    Map<EClass, InOutRefs> inOutRefMap() {
      return inOutRefMap;
    }

    void addEnum(final EEnum e) {
      final EList<EEnumLiteral> lits = e.getELiterals();
      int minVal = IntUtil.minInt(adjustBitwidth(lits.size()));
      enums.add(e, lits, minVal);
      // enums.put(e, literalNames);
    }

    void addClassAndRegisterReferences(final EClass c, final Collection<EReference> references) {
      superMap.put(c, c.getEAllSuperTypes());
      final InOutRefs inOutRefsOfC = inOutRefs(c);
      for (final EReference r : references) {
        /* add c's references to the in-list of the respective referenceType */
        inOutRefs(r.getEReferenceType()).addIncoming(r);
        /* add all of c's references to the its out-list */
        inOutRefsOfC.addOutgoing(r);
        // }
      }
      classes.add(c);
    }

    void addFeatures(EClass eClass, final Collection<EStructuralFeature> features) {
      this.featureMap.put(eClass, features);
    }

    // void addReferences(final Collection<EReference> refs) {
    // features.addAll(refs);
    // }

    /**
     * @param clazz
     * @return
     */
    private InOutRefs inOutRefs(final EClass clazz) {
      final InOutRefs inoutRefs;
      if (inOutRefMap.containsKey(clazz)) {
        inoutRefs = inOutRefMap.get(clazz);
      } else {
        inoutRefs = new InOutRefs();
        inOutRefMap.put(clazz, inoutRefs);
      }
      return inoutRefs;
    }

    //
    // Map<EEnum, List<String>> enums() {
    // return enums;
    // }
    //
    // Collection<EClass> classes() {
    // return classes;
    // }
  }

  static class InOutRefs {
    private final List<EReference> incoming = new ArrayList<>();
    private final List<EReference> outgoing = new ArrayList<>();

    boolean addIncoming(final EReference in) {
      assert in != null;
      return incoming.add(in);
    }

    boolean addOutgoing(final EReference out) {
      assert out != null;
      return outgoing.add(out);
    }

    List<EReference> incoming() {
      return Collections.unmodifiableList(incoming);
    }

    List<EReference> outgoing() {
      return Collections.unmodifiableList(outgoing);
    }
  }
}
