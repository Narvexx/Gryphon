/* 
 * henshin2kodkod -- Copyright (c) 2015-present, Sebastian Gabmeyer
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
package org.modelevolution.emf2rel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public final class FeatureOmitter {
  /**
   * @author Sebastian Gabmeyer
   * 
   */
  public static final class FilteredFeatures {

    private final Collection<EAttribute> attributes;
    private final Collection<EReference> references;

    private FilteredFeatures() {
      attributes = new ArrayList<>();
      references = new ArrayList<>();
    }

    /**
     * @param f
     */
    private void add(EAttribute a) {
      assert a != null;
      attributes.add(a);
    }

    /**
     * @param f
     */
    private void add(final EReference r) {
      assert r != null;
      references.add(r);
    }

    public Collection<EAttribute> attributes() {
      return Collections.unmodifiableCollection(attributes);
    }

    public Collection<EReference> references() {
      return Collections.unmodifiableCollection(references);
    }

    public Collection<EStructuralFeature> features() {
      final Collection<EStructuralFeature> features = new ArrayList<>(attributes.size()
          + references.size());
      features.addAll(attributes);
      features.addAll(references);
      return features;
    }
  }

  public static final FeatureOmitter EMPTY = new FeatureOmitter(0);
  private Set<EStructuralFeature> omittedFeatures;

  private FeatureOmitter(int expectedSize) {
    if (expectedSize <= 0)
      omittedFeatures = new HashSet<>();
    else
      omittedFeatures = new HashSet<>(expectedSize);
  }

  public static FeatureOmitter create(final EStructuralFeature... features) {
    if (features == null || features.length < 1)
      return EMPTY;
    final FeatureOmitter omitter = new FeatureOmitter(features.length);
    for (EStructuralFeature feature : features) {
      if (feature == null)
        continue;
      omitter.omittedFeatures.add(feature);
    }
    return omitter;
  }

  public static FeatureOmitter create(final Collection<EStructuralFeature> features) {
    if (features == null || features.isEmpty())
      return EMPTY;
    final FeatureOmitter omitter = new FeatureOmitter(features.size());
    for (final EStructuralFeature feature : features) {
      if (feature == null)
        continue;
      omitter.omittedFeatures.add(feature);
    }
    return omitter;
  }

  public boolean isOmitted(final EStructuralFeature feature) {
    if (feature == null)
      return false;
    return omittedFeatures.contains(feature);
  }

  public FilteredFeatures filter(final Collection<? extends EStructuralFeature> features) {
    final FilteredFeatures filtered = new FilteredFeatures();
    for (final EStructuralFeature f : features) {
      if (isOmitted(f))
        continue;
      if (f instanceof EAttribute)
        filtered.add((EAttribute) f);
      else if (f instanceof EReference)
        filtered.add((EReference) f);
    }
    return filtered;
  }
}
