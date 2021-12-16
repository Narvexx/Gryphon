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

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class AbstractBaseConfig implements BaseConfig {

  private String workdir;
  private String[] initial;
  // private String metamodel;
  private String transformation;
  private String rules;
  private String properties;
  private String output;
  private String[] omit;
  private String[] merge;
  private String[] ubound;
  private int bitwidth;

  /**
   * 
   */
  public AbstractBaseConfig() {
    super();
  }

  @Override
  public final String getWorkdir() {
    return workdir;
  }

  @Override
  public final void setWorkdir(String workdir) {
    this.workdir = workdir;
  }

  @Override
  public final String[] getInitial() {
    return initial;
  }

  @Override
  public final void setInitial(String[] initial) {
    this.initial = initial;
  }

  // @Override
  // public final String getMetamodel() {
  // return metamodel;
  // }
  //
  // @Override
  // public final void setMetamodel(String metamodel) {
  // this.metamodel = metamodel;
  // }

  @Override
  public final String getTransformation() {
    return transformation;
  }

  @Override
  public final void setTransformation(String transformation) {
    this.transformation = transformation;
  }

  @Override
  public final String getRules() {
    return rules;
  }

  @Override
  public final void setRules(String rules) {
    this.rules = rules;
  }

  @Override
  public final String getProperties() {
    return properties;
  }

  @Override
  public final void setProperties(String properties) {
    this.properties = properties;
  }

  @Override
  public final String getOutput() {
    return output;
  }

  @Override
  public final void setOutput(String output) {
    this.output = output;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.gryphon.config.BasicConfig#getOmit()
   */
  @Override
  public String[] getOmit() {
    return omit;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.gryphon.config.BasicConfig#setOmit(java.lang.String[])
   */
  @Override
  public void setOmit(String[] omit) {
    this.omit = omit;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.gryphon.config.BasicConfig#getMerge()
   */
  @Override
  public String[] getMerge() {
    return merge;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.gryphon.config.BasicConfig#setMerge(java.lang.String[])
   */
  @Override
  public void setMerge(String[] merge) {
    this.merge = merge;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.gryphon.config.BasicConfig#getUbound()
   */
  @Override
  public String[] getUbound() {
    return ubound;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.gryphon.config.BasicConfig#setUbound(java.lang.String[])
   */
  @Override
  public void setUbound(String[] ubound) {
    this.ubound = ubound;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.gryphon.config.BasicConfig#getBitwidth()
   */
  @Override
  public int getBitwidth() {
    return this.bitwidth;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.gryphon.config.BasicConfig#setBitwidth(int)
   */
  @Override
  public void setBitwidth(int bitwidth) {
    this.bitwidth = bitwidth;
  }

}