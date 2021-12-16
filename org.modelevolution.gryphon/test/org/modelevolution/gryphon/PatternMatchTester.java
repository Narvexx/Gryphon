/* 
 * org.modelevolution.furnace -- Copyright (c) 2015-present, Sebastian Gabmeyer
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
package org.modelevolution.gryphon;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class PatternMatchTester {
  @Test
  public void testStringSplitting() {
    final String test1 = "name1={1,2,3}";
    final String test2 = "name1 =  { 1 ,   2 ,3}";
    final String[] actuals1 = test2.split("\\s*=\\s*");
    String[] expecteds1 = new String[] { "name1", "{ 1 ,   2 ,3}" };
    assertArrayEquals(expecteds1, actuals1);
    String[] expecteds2 = new String[] { "name1", "1", "2", "3" };
    final String[] actuals2 = test2.split("(\\s*=\\s*\\{\\s*)|(\\s*,\\s*)|(\\s*\\}\\s*)");
    assertArrayEquals(print(actuals2), expecteds2, actuals2);
    final String[] actuals3 = test2.replaceAll("\\s*", "").split("\\W+");
    assertArrayEquals(print(actuals3), expecteds2, actuals3);
    final String[] actuals4 = test2.replaceAll("\\s*", "").split("=\\{|,|\\}");
    assertArrayEquals(print(actuals4), expecteds2, actuals4);
  }

  /**
   * @param actuals2
   * @return
   */
  private String print(String[] strings) {
    StringBuffer sb = new StringBuffer(strings.length*2+2);
    sb.append("[");
    int i = 0;
    for (; i < strings.length - 1; i++)
      sb.append(strings[i]).append(",");
    sb.append(strings[i]).append("]");
    return sb.toString();
  }

}
