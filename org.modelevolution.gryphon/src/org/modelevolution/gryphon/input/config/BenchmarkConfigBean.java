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
public class BenchmarkConfigBean extends AbstractBaseConfig implements BenchmarkConfig {
  /*
   * name=name runs=10 startcounting=5 workingdir=/home initial=init1.xmi
   * initial=init2.xmi metamodel=mm.ecore transformation=transformation.henshin
   * rules=rule1,rule2 properties=propName1,propName2 output=output.txt
   */

  private String name;
  private int runs;
  private int startCounting;

  /**
   * 
   */
  public BenchmarkConfigBean() {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.gryphon.batch.BenchmarkConfig#getName()
   */
  @Override
  public final String getName() {
    return name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.gryphon.batch.BenchmarkConfig#setName(java.lang.String)
   */
  @Override
  public final void setName(String name) {
    this.name = name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.gryphon.batch.BenchmarkConfig#getRuns()
   */
  @Override
  public final int getRuns() {
    return runs;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.gryphon.batch.BenchmarkConfig#setRuns(int)
   */
  @Override
  public final void setRuns(int runs) {
    this.runs = runs;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.gryphon.batch.BenchmarkConfig#getStartCounting()
   */
  @Override
  public final int getAvgruns() {
    return startCounting;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.gryphon.batch.BenchmarkConfig#setStartCounting(int)
   */
  @Override
  public final void setAvgruns(int startCounting) {
    this.startCounting = startCounting;
  }
}
