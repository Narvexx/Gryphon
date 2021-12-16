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

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import kodkod.ast.Relation;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * A feature merger may be useful in cases where two classes share a common
 * feature, not necessarily with identical names, that should be captured by the
 * same relation. Usually, this requires the classes to have a common superclass
 * that contains that feature. With the feature merger you can simply specify
 * that two or more features range over the same domain with a single relation.
 * 
 * <p>
 * Suppose you model a Pacman game with three classes: <code>Pacman</code>,
 * <code>Ghost</code>, and <code>Field</code>. Suppose further that the
 * <code>Pacman</code> class and the <code>Ghost</code> class both reference the
 * <code>Field</code> class to indicate on what field <code>Pacman</code> and a
 * <code>Ghost</code> are positioned on. Since both references are distinct the
 * ecore2kodkod translation would yield two relations. However, for the
 * translation it would be convenient to treat both references alike without
 * introducing a common superclass <code>Player</code>. Now, the feature merger
 * allows you to specify for which structural features a common relation should
 * be created.
 * 
 * @author Sebastian Gabmeyer
 * 
 */
public final class FeatureMerger {
  private static final FeatureMerger EMPTY = new FeatureMerger();
  private final Map<EStructuralFeature, StateRelation> mergedFeatures;

  public FeatureMerger() {
    mergedFeatures = new IdentityHashMap<>();
  }

  public FeatureMerger(final int expectedSize) {
    mergedFeatures = new IdentityHashMap<>(expectedSize);
  }

  /**
   * @return an empty feature merger, i.e., a merger that doesn't merge any
   *         features.
   */
  public static FeatureMerger create() {
    return EMPTY;
  }

  public static FeatureMerger create(final String name, final EStructuralFeature... features) {
    return create(name, Arrays.asList(features));
  }

  public static FeatureMerger create(final String name, final List<EStructuralFeature> features) {
    final FeatureMerger fm = new FeatureMerger(features.size());
    fm.merge(name, features);
    return fm;
  }

  public void merge(final String name, final EStructuralFeature... features) {
    merge(name, Arrays.asList(features));
  }

  public void merge(final String name, final List<EStructuralFeature> features) {
    final int BINARY = 2;
    final StateRelation pair = StateRelation.create(name, BINARY);
    for (final EStructuralFeature f : features) {
      final StateRelation old = mergedFeatures.put(f, pair);
      if (old != null)
        throw new IllegalArgumentException("Trying to re-merge feature " + f.getName()
            + " with relation " + pair.name() + " although it is is already merged by relation "
            + old.name() + ".");
    }
  }

  public boolean isMerged(final EStructuralFeature feature) {
    return mergedFeatures.containsKey(feature);
  }

  /**
   * Get the (paired) relation for the merged feature.
   * 
   * @param feature
   * @return
   */
  public StateRelation state(final EStructuralFeature feature) {
    return mergedFeatures.get(feature);
  }

  public Relation relation(final EStructuralFeature feature) {
    if (isMerged(feature))
      return mergedFeatures.get(feature).preState();

    return null;
  }

  public Relation nextRelation(final EStructuralFeature feature) {
    if (isMerged(feature))
      return mergedFeatures.get(feature).postState();

    return null;
  }

  /**
   * @param mergedFeatures2
   * @return
   */
  public static FeatureMerger create(final List<EStructuralFeature> features) {
    if (features == null || features.isEmpty())
      return EMPTY;
    return create(features.get(0).getName(), features);
  }

  /**
   * @param features
   */
  public void merge(final List<EStructuralFeature> features) {
    merge(features.get(0).getName(), features);
  }
}
