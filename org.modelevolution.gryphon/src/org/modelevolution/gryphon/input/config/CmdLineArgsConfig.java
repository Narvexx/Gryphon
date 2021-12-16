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
package org.modelevolution.gryphon.input.config;

import java.util.List;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class CmdLineArgsConfig extends AbstractBaseConfig implements VerificationConfig {

  /**
   * 
   */
  public CmdLineArgsConfig(String workdir, String transformationFile, List<String> initialModels,
      int bitwidth, String enabledRules, String enabledProperties, List<String> omittedFeatures,
      List<String> mergedFeatures, List<String> upperBounds) {
    super();
    setWorkdir(workdir);
    setTransformation(transformationFile);
    setInitial(initialModels.toArray(new String[0]));
    setBitwidth(bitwidth);
    setRules(enabledRules);
    setProperties(enabledProperties);
    setOmit(omittedFeatures.toArray(new String[0]));
    setMerge(mergedFeatures.toArray(new String[0]));
    setUbound(upperBounds.toArray(new String[0]));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.gryphon.input.config.VerificationConfig#getName()
   */
  @Override
  public String getName() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.gryphon.input.config.VerificationConfig#setName(java
   * .lang.String)
   */
  @Override
  public void setName(String name) {
    throw new UnsupportedOperationException();
  }

}
