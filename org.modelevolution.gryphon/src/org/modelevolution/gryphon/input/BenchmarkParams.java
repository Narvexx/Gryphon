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

import java.io.IOException;

import org.modelevolution.gryphon.input.config.BenchmarkConfig;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class BenchmarkParams extends AbstractInputParams implements BenchmarkParameterizable {
  private int numRuns;

  private int numAveragedRuns;

  private BenchmarkParams() {

  }

  public static BenchmarkParams create(final BenchmarkConfig config, final boolean verbose)
      throws IOException {
    final BenchmarkParams params = new BenchmarkParams();
    final String[] enabledRules;
    if (config.getRules() != null)
      enabledRules = config.getRules().replaceAll("\\s*", "").split(",");
    else
      enabledRules = null;
    final String[] enabledProps;
    if (config.getProperties() != null)
      enabledProps = config.getProperties().replaceAll("\\s*", "").split(",");
    else
      enabledProps = null;
    final InputLoader loader = new InputLoader(config.getName(), config.getWorkdir(),
                                               config.getTransformation(), config.getInitial(),
                                               enabledRules, enabledProps);
    params.setMetamodel(loader.metamodel());
    params.setInitialStates(loader.instances());
    params.setTransitionRules(loader.transitionRules());
    params.setSpecification(loader.specificationRules());
    params.setBitwidth(config.getBitwidth());
    params.setUpperBounds(loader.getUpperBounds(config.getUbound()));
    params.setOmitter(loader.getFeatureOmitter(config.getOmit()));
    params.setMerger(loader.getFeatureMerger(config.getMerge()));
    params.setRunId(loader.getRunId());
    params.setOutputFilepaths(loader.getOutputFilepaths());
    params.setNumRuns(config.getRuns());
    params.setNumAveragedRuns(config.getAvgruns());
    return params;
  }

  /**
   * @return the numRuns
   */
  @Override
  public int getNumRuns() {
    return numRuns;
  }

  /**
   * @param numRuns
   *          the numRuns to set
   */
  protected void setNumRuns(int numRuns) {
    /* It does not make much sense to benchmark over something less than 2. */
    if (numRuns < 2)
      throw new IllegalArgumentException("numRuns < 2.");
    this.numRuns = numRuns;
  }

  /**
   * @return the numAveragedRuns
   */
  @Override
  public int getNumAveragedRuns() {
    return numAveragedRuns;
  }

  /**
   * @param numAveragedRuns
   *          the numAveragedRuns to set
   */
  protected void setNumAveragedRuns(int numAveragedRuns) {
    if (numAveragedRuns > numRuns)
      throw new IllegalArgumentException("numAveragedRuns > numRuns.");
    /* It does not make much sense to benchmark over something less than 2. */
    if (numAveragedRuns < 2)
      throw new IllegalArgumentException("numAveragedRuns < 2.");
    this.numAveragedRuns = numAveragedRuns;
  }
}
