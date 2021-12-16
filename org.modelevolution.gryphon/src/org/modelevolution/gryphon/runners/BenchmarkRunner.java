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
package org.modelevolution.gryphon.runners;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.modelevolution.gryphon.input.BenchmarkParams;
import org.modelevolution.gryphon.input.config.WarmupConfig;
import org.modelevolution.gryphon.output.AveragingStats;
import org.modelevolution.gryphon.output.Stats;
import org.modelevolution.gryphon.solver.GenericSolver;
import org.modelevolution.gryphon.solver.Solver;
import org.modelevolution.gryphon.solver.VerificationResult;
import org.modelevolution.rts.Property;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class BenchmarkRunner extends AbstractRunner implements Runner {
  private static final int MAX_NAME_LENGTH = 20;
  private WarmupRunner warmupRunner;
  private int numRuns;

  private static class WarmupRunner implements Runner {

    public static WarmupRunner init(final WarmupConfig config) {
      return null;
    }

    private WarmupRunner() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modelevolution.gryphon.runners.Runner#run()
     */
    @Override
    public void run() {
      throw new UnsupportedOperationException(); // TODO: Implement this!
    }
  }

  /**
   * 
   */
  public BenchmarkRunner(BenchmarkParams params) {
    super(params, false);
    if (params.getNumRuns() < 1)
      throw new IllegalArgumentException();
    if (params.getNumAveragedRuns() > params.getNumRuns())
      throw new IllegalArgumentException();
    numRuns = params.getNumRuns();
    stats = new AveragingStats(params.getRunId(), params.getNumAveragedRuns());
    options.setIsBenchmarking(true);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.gryphon.runners.Runner#run()
   */
  @Override
  public void run() {
    long totalTime = 0;
    for (int idx = 0; idx < initialStates.length; ++idx) {
      final Resource initial = initialStates[idx];
      assert initial != null;
      assert initial.getURI().isFile();

      if (outputFilepaths[idx] == null)
        throw new NullPointerException();
      if (outputFilepaths[idx].length == 0)
        throw new IllegalArgumentException();

      final String aigFilepath = outputFilepaths[idx][AIG_IDX];

      Stats subStats = null;
      final long start = System.currentTimeMillis();
      for (int runCnt = 0; runCnt < numRuns; ++runCnt) {
        subStats = stats.addStatGroup(initial.getURI().lastSegment());
        doRun(initial, aigFilepath, subStats);
      }
      final long end = System.currentTimeMillis();
      totalTime += (end - start);

      final String statsFilepath = outputFilepaths[idx][STATS_IDX];
      try {
        assert subStats != null;
        subStats.printStats(new PrintWriter(new File(statsFilepath)));
      } catch (FileNotFoundException e) {
        System.err.println("An error occured while writing the statistics to " + statsFilepath
            + "No stats file has been generated. Stacktrace follows:");
        e.printStackTrace();
        return;
      }
    }
    System.out.println("TOTAL TIME: " + totalTime);
  }

  @Override
  protected void runVerifier(final String aigFilepath, final List<Property> specification,
      Stats stats) {
    Stats solverStats = stats.addStatGroup("Solver");
    /* final long start = System.currentTimeMillis(); /* System.nanoTime() */
    final Solver iimc = new GenericSolver();
    final String propertyName = buildPropertyName(specification); 
    // VerificationResult res =
    // ic3.solve("/home/sgbmyr/tools/ic3/ic3ref2/IC3 -s " + propIdx
    // + " -f " + aigFilepath);
    final long start = System.currentTimeMillis(); /* System.nanoTime() */
    VerificationResult res = iimc.solve("/Users/mitchellalbers/Tools/iimc/iimc " + aigFilepath);
    final long end = System.currentTimeMillis();
    
    solverStats.record(end - start /* System.nanoTime() */, propertyName);
    res.setName(propertyName);
    results.add(res);

  }

  /**
   * @param specification
   * @return
   */
  protected String buildPropertyName(final List<Property> specification) {
    final StringBuffer sb = new StringBuffer();
    for (final Property property : specification) {
      sb.append(property.name());
    }
    if (sb.length() > MAX_NAME_LENGTH)
      sb.setLength(MAX_NAME_LENGTH);
    final String propertyName = sb.toString();
    return propertyName;
  }
}
