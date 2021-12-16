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

import java.util.Collections;
import java.util.List;

import org.modelevolution.rts.GoodState;
import org.modelevolution.rts.Invariant;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class GenericVerificationResult implements VerificationResult {

  private final Result rv;
  private final List<String> output;
  private String name;
  private final int exitCode;

  /**
   * @param exitValue
   *          if exitValue == 0, then rv == RetVal.FAIL; if exitVal > 1, then rv
   *          == RetVal.SUCCESS; if exitVal == -1, then rv == RetVal.UNKNOWN
   * @param output
   */
  public GenericVerificationResult(final int exitValue, final List<String> output) {
    if (output == null)
      throw new NullPointerException("output == null.");
    if (output.isEmpty())
      throw new IllegalArgumentException("output.isEmpty().");
    int result = 0;
    for (int i = 0; i < output.size(); ++i) {
      final String line = output.get(i);
      if (line.startsWith("u") || line.startsWith("rlim"))
        continue;
      result = Integer.parseInt(line);
      break;
    }
    switch (result) {
    case 1:
      rv = Result.REACHABLE;
      break;
    case 0:
      rv = Result.UNREACHABLE;
      break;
    default:
      rv = Result.ERROR;
      break;
    }
    this.output = output;
    this.exitCode = exitValue;
  }

  /**
   * @param e
   */
  public GenericVerificationResult(Exception e) {
    rv = Result.ERROR;
    exitCode = 1;
    output = Collections.singletonList(e.getMessage());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.gryphon.solver.SolverResult#returnValue()
   */
  @Override
  public Result result() {
    return rv;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.gryphon.solver.SolverResult#output()
   */
  @Override
  public List<String> output() {
    return output;
  }

  @Override
  public String name() {
    return name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.gryphon.solver.SolverResult#setName(java.lang.String)
   */
  @Override
  public void setName(final String name) {
    this.name = name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.gryphon.solver.VerificationResult#exitCode()
   */
  @Override
  public int exitCode() {
    return exitCode;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer(name);
    sb.append(": ");
    sb.append(result());
    sb.append(System.lineSeparator());
    if (result() == Result.REACHABLE) {
      for (final String line : output()) {
        sb.append(line);
        sb.append(System.lineSeparator());
      }
    }
    return sb.toString();
  }

}
