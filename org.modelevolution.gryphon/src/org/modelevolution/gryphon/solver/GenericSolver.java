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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class GenericSolver implements Solver {

  private List<String> output;
  private int exitValue;

  /**
   * The method expects a single string argument which contains the command line
   * call to the solver including all arguments. The method returns with the
   * exit value of the called command or, in case of an exception,
   * <code>-1</code>. The line-wise output of the called command is available
   * through {@link GenericSolver#output()}. Note that additional parameters
   * <code>params</code> are ignored.
   * 
   * @see org.modelevolution.gryphon.solver.Solver#solve(String,
   *      java.lang.Object[])
   */
  @Override
  public VerificationResult solve(String cmd, Object... params) {
    // if (params == null)
    // throw new NullPointerException();
    // if (params.length < 1)
    // throw new IllegalArgumentException();
    // if (!(params[0] instanceof String))
    // throw new IllegalArgumentException();

    output = new ArrayList<>();
    /* Execute a command and get its process handle */
    try {
      final Process proc = Runtime.getRuntime().exec(cmd);

      /* Get the handle for the processes InputStream */
      final InputStream istream = proc.getInputStream();

      /* Create a BufferedReader and specify it reads from an input stream. */
      final BufferedReader breader = new BufferedReader(new InputStreamReader(istream));
      String str; /* Temporary String variable */

      while ((str = breader.readLine()) != null)
        output.add(str);

      /* Wait for process to terminate and catch any Exceptions. */
      exitValue = proc.waitFor();

      breader.close();
      // /* The last line of the output contains the result */
      // int rv = Integer.parseInt(output.get(output.size() - 1));
      // DEBUG: System.out.println("Output: " + output);
      return new GenericVerificationResult(exitValue, output);
    } catch (InterruptedException e) {
      System.err.println("[Gryphon] Solver process was interrupted: " + cmd);
      return new GenericVerificationResult(e);
    } catch (IOException e) {
      System.err.println("[Gryphon] IOException occured in solver GenericSolver: " + cmd);
      return new GenericVerificationResult(e);
    }
  }

  public List<String> output() {
    return output;
  }

  /**
   * @return the solver's return value
   */
  public int rv() {
    return exitValue;
  }
}
