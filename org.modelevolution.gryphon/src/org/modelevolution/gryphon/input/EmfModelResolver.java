/* 
 * org.modelevolution.furnace -- Copyright (c) 2015-present, Sebastian Gabmeyer
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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.modelevolution.gryphon.input;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class EmfModelResolver {
  private EPackage model;

  public EmfModelResolver() {

  }

  /**
   * 
   */
  public EmfModelResolver(final EPackage model) {
    if (model == null)
      throw new NullPointerException("model == null.");
    this.model = model;
  }

  private EPackage _model() {
    if (model == null)
      throw new NullPointerException("this.model == null.");
    return this.model;
  }
  
  public EPackage getModel() {
    return this.model;
  }
  
  public void setModel(final EPackage model) {
    this.model = model;
  }

  public EClass resolveEClass(final String name) {
    final EClassifier classifier = _model().getEClassifier(name);
    if (classifier instanceof EClass)
      return (EClass) classifier;
    else
      return null;
  }

  public Collection<EStructuralFeature> resolveFeatures(final String className,
      final String... featureNames) {
    final EClassifier classifier = _model().getEClassifier(className);
    if (classifier instanceof EClass) {
      final EClass clazz = (EClass) classifier;
      final Collection<EStructuralFeature> features = new ArrayList<>(featureNames.length);
      for (final String name : featureNames) {
        final EStructuralFeature feature = clazz.getEStructuralFeature(name);
        if (feature == null)
          throw new IllegalArgumentException("Unknown feature " + name + " in eClass " + clazz
              + ".");
        features.add(feature);
      }
    }
    throw new IllegalArgumentException("EClass " + className + " not found in ePackage "
        + _model().getName() + ".");
  }

  public Collection<EStructuralFeature> resolveFeatures(final String[] classFeatureNames) {
    final EClassifier classifier = _model().getEClassifier(classFeatureNames[0]);
    if (classifier instanceof EClass) {
      final EClass clazz = (EClass) classifier;
      final Collection<EStructuralFeature> features = new ArrayList<>(classFeatureNames.length - 1);
      for (int i = 1; i < classFeatureNames.length; i++) {
        final EStructuralFeature feature = clazz.getEStructuralFeature(classFeatureNames[i]);
        if (feature == null)
          throw new IllegalArgumentException("Unknown feature " + classFeatureNames[i]
              + " in eClass " + clazz + ".");
        features.add(feature);
      }
      return features;
    }
    throw new IllegalArgumentException("EClass " + classFeatureNames[0] + " not found in ePackage "
        + _model().getName() + ".");
  }
}
