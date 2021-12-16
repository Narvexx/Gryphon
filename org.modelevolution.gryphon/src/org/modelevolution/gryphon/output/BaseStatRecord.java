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
package org.modelevolution.gryphon.output;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class BaseStatRecord implements StatRecord {
  private final String name;
  private final double elapsedTime;

  /**
   * 
   */
  public BaseStatRecord(final double elapsedTime, final String name) {
    if (elapsedTime < 0)
      throw new IllegalArgumentException("time < 0.");
    if (name == null)
      throw new NullPointerException("name == null.");
    if (name.isEmpty())
      throw new IllegalArgumentException("name.isEmpty().");
    this.elapsedTime = elapsedTime;
    this.name = name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.gryphon.runners.StatRecord#name()
   */
  @Override
  public String name() {
    return name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.gryphon.runners.StatRecord#time()
   */
  @Override
  public double elapsedTime() {
    return elapsedTime;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer(name).append(": ").append(elapsedTime);
    return sb.toString();
  }
}
