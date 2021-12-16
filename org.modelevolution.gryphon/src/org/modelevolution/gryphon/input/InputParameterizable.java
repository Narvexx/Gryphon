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
public interface InputParameterizable {

  public abstract String[][] getOutputFilepaths();

  public abstract EPackage getMetamodel();

  public abstract Resource[] getInitialStates();

  public abstract Collection<Rule> getTransitionRules();

  public abstract int getBitwidth();

  public abstract Map<EClass, Integer> getUpperBounds();

  public abstract FeatureOmitter getOmitter();

  public abstract FeatureMerger getMerger();

  /**
   * @return the specification
   */
  public abstract Collection<Rule> getSpecification();

  public abstract String getRunId();

}