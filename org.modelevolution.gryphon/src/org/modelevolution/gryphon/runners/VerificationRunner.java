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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kodkod.engine.config.Options;
import kodkod.util.ints.IntIterator;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.henshin.model.Rule;
import org.modelevolution.aig.AigFile;
import org.modelevolution.gryphon.input.InputParameterizable;
import org.modelevolution.gryphon.input.VerificationParams;
import org.modelevolution.gryphon.input.config.VerificationConfig;
import org.modelevolution.gryphon.output.BaseStats;
import org.modelevolution.gryphon.output.PrintableStats;
import org.modelevolution.gryphon.output.Stats;
import org.modelevolution.gryphon.solver.GenericSolver;
import org.modelevolution.gryphon.solver.Solver;
import org.modelevolution.gryphon.solver.VerificationResult;
import org.modelevolution.gts2rts.PropertyTranslator;
import org.modelevolution.gts2rts.RuleTranslator;
import org.modelevolution.rts.Property;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public final class VerificationRunner extends AbstractRunner implements Runner {

  /**
   * 
   */
  public VerificationRunner(final VerificationParams params, final boolean verbose) {
    super(params, verbose);
    stats = new BaseStats(params.getRunId());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.gryphon.verifiers.Runner#run()
   */
  @Override
  public void run() {
    for (int idx = 0; idx < initialStates.length; ++idx) {
      final Resource initial = initialStates[idx];
      assert initial != null;
      assert initial.getURI().isFile();
      final Stats subStats = stats.addStatGroup(initial.getURI().lastSegment());

      if (outputFilepaths[idx] == null)
        throw new NullPointerException();
      if (outputFilepaths[idx].length == 0)
        throw new IllegalArgumentException();

      final String aigFilepath = outputFilepaths[idx][AIG_IDX];

      doRun(initial, aigFilepath, subStats);

      final String statsFilepath = outputFilepaths[idx][STATS_IDX];
      try {
        subStats.printStats(new PrintWriter(new File(statsFilepath)));
      } catch (FileNotFoundException e) {
        System.err.println("An error occured while writing the statistics to " + statsFilepath
            + "No stats file has been generated. Stacktrace follows:");
        e.printStackTrace();
        return;
      }
    }
  }

  @Override
  protected void runVerifier(final String aigFilepath, final List<Property> specification,
      Stats stats) {
    Stats solverStats = stats.addStatGroup("Solver");
    /* final long start = System.currentTimeMillis(); /* System.nanoTime() */
    final Solver iimc = new GenericSolver();
    for (int propIdx = 0; propIdx < specification.size(); ++propIdx) {
      final Property property = specification.get(propIdx);
      // VerificationResult res =
      // ic3.solve("/home/sgbmyr/tools/ic3/ic3ref2/IC3 -s " + propIdx
      // + " -f " + aigFilepath);
      final long start = System.currentTimeMillis(); /* System.nanoTime() */
      VerificationResult res = iimc.solve("/Users/mitchellalbers/Tools/iimc/iimc --pi " + propIdx
          + " " + aigFilepath);
      final long end = System.currentTimeMillis();
      final String propertyName = property.name();
      solverStats.record(end - start /* System.nanoTime() */, propertyName);
      res.setName(propertyName);
      results.add(res);
    }
  }
}
