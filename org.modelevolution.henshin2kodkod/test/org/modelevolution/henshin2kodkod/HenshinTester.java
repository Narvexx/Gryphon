/*
 * henshin2kodkod -- Copyright (c) 2014-present, Sebastian Gabmeyer
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.modelevolution.henshin2kodkod;

import java.util.List;

import static org.junit.Assert.*;

import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.junit.Test;
import org.modelevolution.gts2rts.util.HenshinLoader;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class HenshinTester {
  private static final String HENSHIN_FILE_NAME = "pacman.henshin";
  private static final String MODEL_PATH = "model/pacman";

  @Test
  public void testActionNodes() {
    final HenshinLoader loader = new HenshinLoader(MODEL_PATH, HENSHIN_FILE_NAME);

    final List<Rule> activatedRules = loader.getTransformations();
    for (Rule r : activatedRules) {
      System.out.println("Rule " + r.getName() + " [isMulti: " + r.isMultiRule() + "]:");
      System.out.println(" LHS:");
      for (Node n : r.getLhs().getNodes()) {
        printNodeAndEdgeInfo(n);
      }
      System.out.println(" RHS:");
      for (Node n : r.getRhs().getNodes()) {
        printNodeAndEdgeInfo(n);
      }
      System.out.println(" NACs:");
      for (final NestedCondition nac : r.getLhs().getNACs()) {
        for (final Node n : nac.getConclusion().getNodes()) {
          printNacNode(n);
        }
        // System.out.println("---");
        // for (final Node n : nac.getHost().getNodes()) {
        // printNacNode(n);
        // }
      }
      System.out.println(" Kernel & Multi-Rule");
      System.out.println("   Kernel:");
      Rule kernel = r.getKernelRule();
      if (kernel != null) {
        for (Node n : kernel.getActionNodes(null)) {
          printNodeAndEdgeInfo(n);
        }
      }
      for (Rule subrule : r.getMultiRules()) {
        System.out.println("   Sub-Rule [isMulti: " + subrule.isMultiRule() + "]:");
        System.out.println("   Parent-Rule isMulti: " + r.isMultiRule());
        System.out.println("   Root Rule Name: " + subrule.getRootRule().getName());
        System.out.println("   Root Rule of Root Rule exists? "
            + subrule.getRootRule().getRootRule() != null);
        // Graph lhs = subrule.getLhs();
        // System.out.println("   Subrule's LHS graph isLhs()=" + lhs.isLhs());
        for (Node n : subrule.getActionNodes(null)) {
          printNodeAndEdgeInfo(n);
        }
      }
    }
  }

  /**
   * @param n
   */
  public void printNodeAndEdgeInfo(Node n) {
    System.out.print("   " + n.getName());
    if (n.getAction() != null)
      System.out.println(" [Action: " + n.getAction().getType()
          + (n.getAction().isMulti() ? ", isMulti]" : "]"));
    else
      System.out.println();
    for (Edge e : n.getOutgoing()) {
      // assertTrue(e.toString(), e.getAction() != null);
      System.out.println("    " + (e.getAction() != null ? e.getAction().getType() : "none") + ":"
          + e.getType().getName() + " from '" + e.getSource().getName() + "' to '"
          + e.getTarget().getName() + "'");
    }
  }

  /**
   * @param n
   */
  public void printNacNode(final Node n) {
    System.out.println("   " + n.getName() + " [Action: "
        + (n.getAction() != null ? n.getAction().getType() : "none") + "]");
  }
}
