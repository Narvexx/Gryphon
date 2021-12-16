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

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Collections;

import kodkod.ast.Formula;
import kodkod.ast.LeafExpression;
import kodkod.ast.Relation;

import org.junit.Test;
import org.modelevolution.gryphon.solver.IC3Solver;
import org.modelevolution.gryphon.solver.VerificationResult;
import org.modelevolution.gryphon.solver.VerificationResult.Result;
import org.modelevolution.rts.BadState;
import org.modelevolution.rts.Property;
import org.modelevolution.rts.PropertyFactory;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class SolverTester {

  @Test
  public void testIC3Solver() {
    final String filename = "/home/sgbmyr/fame/relic3/org.modelevolution.models/model/pacman/pacman_pacman-corrected.aag";
    IC3Solver solver = new IC3Solver(filename);
    final Collection<LeafExpression> dummyRelations = Collections.emptyList();
    final Collection<Formula> dummyFormulas = Collections.emptyList();
    final Property badDummy = PropertyFactory.create(BadState.PREFIX + "dummy", dummyRelations,
                                                     null);
    final VerificationResult rv = solver.solve(0);
    assertEquals(Result.UNREACHABLE, rv.result()); // ???
  }
}
