/* 
 * henshin2kodkod -- Copyright (c) 2014-present, Sebastian Gabmeyer
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
package org.modelevolution.gts2rts.util;

import kodkod.ast.IntConstant;
import kodkod.ast.Node;

/**
 * @author Sebastian Gabmeyer
 */
public final class IntUtil {

  /**
   * @param x
   * @return the number of bits required to represent <code>x</code> integers
   * @see http://www.exploringbinary.com/number-of-bits-in-a-decimal-integer/
   * @see Integer#numberOfLeadingZeros(int)
   */
  public static int requiredNumOfBits(final int x) {
    if (x < 0)
      throw new IllegalArgumentException("x < 0.");
    final int numberOfLeadingZeros = Integer.numberOfLeadingZeros(x - 1);
    final int ceilLog2OfX = 32 - numberOfLeadingZeros;
    return ceilLog2OfX;
  }

  /**
   * @param bitwidth
   * @return the number of integers representable with the given bitwidth.
   */
  public static int numOfInts(final int bitwidth) {
    if (bitwidth < 0 || bitwidth > 31)
      throw new IllegalArgumentException("bitwidth out of range (0..31.");
    switch (bitwidth) {
    case 0:
      return 0;
    default:
      return ((1 << bitwidth - 1)) + (1 << bitwidth - 1);
    }
  }

  public static int minInt(final int bitwidth) {
    if (bitwidth < 0 || bitwidth > 31)
      throw new IllegalArgumentException("bitwidth out of range (0..31.");
    switch (bitwidth) {
    case 0:
      return 0;
    default:
      return -(1 << bitwidth - 1);
    }
  }

  public static int maxInt(final int bitwidth) {
    if (bitwidth < 0 || bitwidth > 31)
      throw new IllegalArgumentException("bitwidth out of range (0..31.");
    switch (bitwidth) {
    case 0:
      return 0;
    default:
      return (1 << bitwidth - 1) - 1;
    }
  }

  /**
   * @param bitwidth
   * @param value
   * @return
   */
  public static boolean checkCapacity(final int bitwidth, final int reqCapacity) {
    return reqCapacity < numOfInts(bitwidth);
  }

  public static boolean isInRange(final int value, final int bitwidth) {
    return value >= minInt(bitwidth) && value <= maxInt(bitwidth);
  }

  public static boolean isTrue(Node node) {
    if (node instanceof IntConstant)
      return ((IntConstant) node).value() == -1;
    return false;
  }

  public static boolean isFalse(Node node) {
    if (node instanceof IntConstant)
      return ((IntConstant) node).value() == 0;
    return false;
  }
}
