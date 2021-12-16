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
package org.modelevolution.gryphon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class SolverCallerTester {
  public List<String> runSolver(final String cmd) throws IOException {
    final List<String> output = new ArrayList<>();

    // Execute a command and get its process handle
    final Process proc = Runtime.getRuntime().exec(cmd);

    // Get the handle for the processes InputStream
    final InputStream istr = proc.getInputStream();

    // Create a BufferedReader and specify it reads
    // from an input stream.
    final BufferedReader br = new BufferedReader(new InputStreamReader(istr));
    String str; // Temporary String variable

    while ((str = br.readLine()) != null)
      output.add(str);

    // Wait for process to terminate and catch any Exceptions.
    int rv = -1;
    try {
      rv = proc.waitFor();
    } catch (InterruptedException e) {
      System.err.println("Process was interrupted");
    }
    System.out.println("rv: " + rv);
    // Note: proc.exitValue() returns the exit value.
    // (Use if required)

    // br.close(); // Done.

    // Convert the list to a string and return
    return output;
  }

  @Test
  public void runCommandTester() {
    final List<String> output;
    try {
      output = runSolver("/home/sgbmyr/tools/ic3/ic3ref2/IC3 -v -s 0 -f /home/sgbmyr/fame/relic3/org.modelevolution.models/model/pacman/pacman-corrected_Game.aag");
      for (final String line : output)
        System.out.println(line);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return;
    }
  }
}
