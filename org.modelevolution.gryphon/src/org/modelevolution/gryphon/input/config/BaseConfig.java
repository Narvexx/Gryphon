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
public interface BaseConfig {

  public abstract String getWorkdir();

  public abstract void setWorkdir(String workingDir);

  public abstract String[] getInitial();

  public abstract void setInitial(String[] initial);

  public abstract String getTransformation();

  public abstract void setTransformation(String transformation);

  public abstract String getRules();

  public abstract void setRules(String rules);

  public abstract String getProperties();

  public abstract void setProperties(String properties);

  public abstract String getOutput();

  public abstract void setOutput(String output);
  
  public abstract String[] getOmit();
  
  public abstract void setOmit(String[] omit);
  
  public abstract String[] getMerge();
  
  public abstract void setMerge(String[] merge);

  public abstract String[] getUbound();
  
  public abstract void setUbound(String[] ubound);
  
  public abstract int getBitwidth();
  
  public abstract void setBitwidth(int bitwidth);
}