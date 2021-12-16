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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.modelevolution.gryphon.input.config.VerificationConfig;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class VerificationParams extends AbstractInputParams implements InputParameterizable {

  // private Collection<Rule> invariants;
  //
  // private Collection<Rule> badProperties;
  //
  // private Collection<Rule> reachabiltyProperties;

  /**
   * 
   */
  private VerificationParams() {
  }

  public static VerificationParams create(final VerificationConfig config) throws IOException {
    final VerificationParams params = new VerificationParams();
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
    return params;
  }
  // public final Collection<Rule> getInvariants() {
  // return invariants;
  // }
  //
  // public final Collection<Rule> getBadProperties() {
  // return badProperties;
  // }
  //
  // public final Collection<Rule> getReachabiltyProperties() {
  // return reachabiltyProperties;
  // }

}
