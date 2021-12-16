/* 
 * org.modelevolution.gryphon -- Copyright (c) 2015-present, Sebastian Gabmeyer
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

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.henshin.model.Rule;
import org.modelevolution.emf2rel.FeatureMerger;
import org.modelevolution.emf2rel.FeatureOmitter;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public abstract class AbstractInputParams implements InputParameterizable {

  protected String[][] outputFilepaths;
  protected EPackage metamodel;
  protected Resource[] initialStates;
  protected Collection<Rule> transitionRules;
  protected int bitwidth;
  protected Map<EClass, Integer> upperBounds;
  protected FeatureOmitter omitter;
  protected FeatureMerger merger;
  protected Collection<Rule> specification;
  protected String runId;

  @Override
  public final String[][] getOutputFilepaths() {
    return outputFilepaths;
  }

  @Override
  public final EPackage getMetamodel() {
    return metamodel;
  }

  @Override
  public final Resource[] getInitialStates() {
    return initialStates;
  }

  @Override
  public final Collection<Rule> getTransitionRules() {
    return transitionRules;
  }

  @Override
  public final int getBitwidth() {
    return bitwidth;
  }

  @Override
  public final Map<EClass, Integer> getUpperBounds() {
    return upperBounds;
  }

  @Override
  public final FeatureOmitter getOmitter() {
    return omitter;
  }

  @Override
  public final FeatureMerger getMerger() {
    return merger;
  }

  @Override
  public Collection<Rule> getSpecification() {
    return specification;
  }

  @Override
  public String getRunId() {
    return runId;
  }

  protected final void setOutputFilepaths(String[][] outputFilepaths) {
    if (outputFilepaths == null)
      throw new NullPointerException();
    if (outputFilepaths.length == 0)
      throw new IllegalArgumentException();
    this.outputFilepaths = outputFilepaths;
  }

  protected final void setMetamodel(EPackage metamodel) {
    if (metamodel == null)
      throw new NullPointerException();
    this.metamodel = metamodel;
  }

  protected final void setInitialStates(Resource[] initialStates) {
    if (initialStates == null)
      throw new NullPointerException();
    if (initialStates.length < 1)
      throw new IllegalArgumentException();
    this.initialStates = initialStates;
  }

  protected final void setTransitionRules(Collection<Rule> transitions) {
    if (transitions == null)
      throw new NullPointerException();
    if (transitions.size() < 1)
      throw new IllegalArgumentException();
    this.transitionRules = transitions;
  }

  protected final void setBitwidth(int bitwidth) {
    if (bitwidth < 0)
      throw new IllegalArgumentException();
    this.bitwidth = bitwidth == 0 ? 1 : bitwidth;
  }

  protected final void setUpperBounds(Map<EClass, Integer> upperBounds) {
    if (upperBounds == null)
      this.upperBounds = Collections.emptyMap();
    else
      this.upperBounds = upperBounds;
  }

  protected final void setOmitter(FeatureOmitter omitter) {
    if (omitter == null)
      this.omitter = FeatureOmitter.create();
    else
      this.omitter = omitter;
  }

  protected final void setMerger(FeatureMerger merger) {
    if (merger == null)
      this.merger = FeatureMerger.create();
    else
      this.merger = merger;
  }

  protected final void setSpecification(Collection<Rule> specification) {
    if (specification == null)
      throw new NullPointerException();
    if (specification.isEmpty())
      throw new IllegalArgumentException();
    this.specification = specification;
  }

  protected final void setRunId(String runId) {
    this.runId = runId;
  }

}
