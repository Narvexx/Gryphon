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

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Collections;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class IC3Solver implements Solver {

  static {
    NativeLibrary.addSearchPath("ic3", "lib");
    // NativeLibrary.addSearchPath("aiger", "lib");
  }
  /**
   * 
   */
  private static final int VERBOSITY = 2;
  /**
   * 
   */
  private static final boolean DO_RANDOM = false;
  /**
   * 
   */
  private static final boolean DO_BASIC = false;
  private static final int ERR_AIG_READ = -2;

  // public interface AigLibrary extends Library {
  // public Structure aiger_init();
  //
  // public String aiger_open_and_read_from_file(Structure aig, String
  // filename);
  // }

  public interface IC3Library extends Library {
    /* Model * modelFromAiger(aiger * aig, unsigned int propertyIndex); */
    // public Structure getModelFromAiger(Structure aig, int propertyIndex);

    /*
     * bool check(Model & model, int verbose = 0, // 0: silent, 1: stats, 2:
     * informative bool basic = false, // simple inductive generalization bool
     * random = false); // random runs for statistical profiling
     */
    // boolean check(Structure model, int verbose, boolean basic, boolean
    // random);
    int solve(String filename, int propertyIndex, int verbose);
  }

  // private AigLibrary aiger = (AigLibrary) Native.loadLibrary("aiger",
  // AigLibrary.class);
  private IC3Library ic3 = (IC3Library) Native.loadLibrary("ic3", IC3Library.class);
  // private final Structure aig;
  private final String filename;

  /**
   * @param filename
   * 
   */
  public IC3Solver(final String filename) {
    this.filename = filename;
    // aig = aiger.aiger_init();
    // aiger.aiger_open_and_read_from_file(aig, filename);
  }

  /**
   * @param propertyIndex
   * @return
   * 
   */
  @SuppressWarnings("unchecked")
  public VerificationResult solve(final int propertyIndex) {
    // final Structure model = ic3.getModelFromAiger(aig, propertyIndex);
    // return ic3.check(model, VERBOSITY, DO_BASIC, DO_RANDOM);
    // Structure model =
    int rv = ic3.solve(filename, propertyIndex, 2);
    if (rv == ERR_AIG_READ)
      return new GenericVerificationResult(new SolverException("An error occured while "
          + "reading the file " + filename));
    // return rv > 0 ? true : false;
    return new GenericVerificationResult(rv, new ArrayList<String>(0));
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.gryphon.solver.Solver#solve(java.lang.Object[])
   */
  @Override
  public VerificationResult solve(String command, Object... params) {
    if (params == null)
      throw new NullPointerException();
    if (params.length < 1)
      throw new IllegalArgumentException();
    if (!(params[0] instanceof Integer))
      throw new IllegalArgumentException();
    return solve((Integer) params[0]);
  }
}
