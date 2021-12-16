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
package org.modelevolution.gryphon.solver;

import java.util.List;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public interface VerificationResult {
  /**
   * A solver's return value (rv). By convention, if the solver's exit code is
   * <ul>
   * <li>0, the result is FAIL
   * <li>1, the result is SUCCESS
   * <li>otherwise, the result is ERROR.
   * </ul>
   * 
   * @author Sebastian Gabmeyer
   * 
   */
  public enum Result {
    UNREACHABLE, REACHABLE, UNKNOWN, ERROR, ;
  }

  Result result();

  /**
   * @return the exit code as returned by the solver
   */
  int exitCode();

  /**
   * @return the solver's output
   */
  List<String> output();

  /**
   * @param name
   */
  void setName(String name);

  /**
   * @return
   */
  String name();
}
