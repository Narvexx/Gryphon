/* 
 * org.modelevolution.fol2aig -- Copyright (c) 2015-present, Sebastian Gabmeyer
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
package org.modelevolution.fol2aig;

import java.util.Map;

import kodkod.ast.Formula;
import kodkod.ast.LeafExpression;
import kodkod.ast.Relation;
import kodkod.engine.bool.BooleanValue;
import kodkod.util.ints.IntSet;

import org.modelevolution.aig.AigAnd;
import org.modelevolution.aig.AigExpr;
import org.modelevolution.aig.AigInput;
import org.modelevolution.aig.AigLatch;
import org.modelevolution.aig.AigLit;
import org.modelevolution.aig.AigNot;
import org.modelevolution.aig.builders.AbstractAigBuilder;
import org.modelevolution.aig.builders.AigBuilder;
import org.modelevolution.emf2rel.StateRelation;
import org.modelevolution.rts.StateChanger;
import org.modelevolution.rts.Transition;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public final class Fol2AigProof {
  private Map<LeafExpression, Map<Integer, AigExpr>> var2inputMap;
  private Map<StateRelation, Map<Integer, AigExpr>> state2latchMap;
  private Map<Relation, IntSet> postStateLabels;
  private Map<Transition, Map<Formula, BooleanValue>> transitionMap;
  private Map<Integer, AigBuilder> bool2builderMap;
  private Map<Integer, AigExpr> bool2aigMap;

  /**
   * @param var2inputMap
   * @param state2latchMap
   * @param postStateLabels
   * @param transitionMap
   * @param bool2builderMap
   * @param bool2aigMap
   */
  private Fol2AigProof(Map<LeafExpression, Map<Integer, AigExpr>> var2inputMap,
      Map<StateRelation, Map<Integer, AigExpr>> state2latchMap,
      Map<Relation, IntSet> postStateLabels,
      Map<Transition, Map<Formula, BooleanValue>> transitionMap,
      Map<Integer, AigBuilder> bool2builderMap, Map<Integer, AigExpr> bool2aigMap) {
    this.var2inputMap = var2inputMap;
    this.state2latchMap = state2latchMap;
    this.postStateLabels = postStateLabels;
    this.transitionMap = transitionMap;
    this.bool2builderMap = bool2builderMap;
    this.bool2aigMap = bool2aigMap;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("Transition Variables -> Input:\n");

    for (final LeafExpression r : var2inputMap.keySet()) {
      sb.append(r.name()).append(":\n");
      final Map<Integer, AigExpr> label2aigMap = var2inputMap.get(r);

      for (final Integer label : label2aigMap.keySet()) {
        sb.append("  ").append(label).append(" <-> ").append(label2aigMap.get(label));
        sb.append("\n");
      }
    }

    for (final StateRelation s : state2latchMap.keySet()) {
      sb.append(s.name()).append(":\n");
      final Map<Integer, AigExpr> label2latchMap = state2latchMap.get(s);

      for (final Integer label : label2latchMap.keySet()) {
        final AigExpr latch = label2latchMap.get(label);
        sb.append("  ").append(label).append(" <-> ").append(latch).append("\n");
        if (latch instanceof AigLatch)
          sb.append("    ").append(printUnrollAigExpr(latch.input(0))).append("\n");
        else if (latch == AigExpr.FALSE || latch == AigExpr.TRUE)
          sb.append("    ").append(printUnrollAigExpr(latch)).append("\n");
      }
    }

    sb.append("Check whether the following formulas are equivalent:\n");
    for (StateChanger t : transitionMap.keySet()) {
      sb.append("* ").append(t.name()).append(" *\n");
      sb.append("  bool -> builder -> aig:\n");
      for (BooleanValue bool : transitionMap.get(t).values()) {
        sb.append("   ");
        sb.append(bool.toString());
        sb.append("\n = ");
        sb.append(bool2builderMap.get(bool.label()));
        sb.append("\n = ");
        sb.append(bool2aigMap.get(bool.label()));
        sb.append("\n");
      }
    }

    return sb.toString();
  }

  /**
   * @param expr
   * @return
   */
  private String printUnrollAigExpr(final AigExpr expr) {
    if (expr instanceof AigLit) {
      return expr.toString();
    } else if (expr instanceof AigNot) {
      return new StringBuffer("!").append(printUnrollAigExpr(expr.input(0))).toString();
    } else if (expr instanceof AigAnd) {
      final StringBuffer sb = new StringBuffer("(");
      sb.append(printUnrollAigExpr(expr.input(0)));
      sb.append("&");
      sb.append(printUnrollAigExpr(expr.input(1)));
      sb.append(")");
      return sb.toString();
    } else if (expr instanceof AigLatch) {
      return String.valueOf(((AigLatch) expr).literal());
    } else if (expr instanceof AigInput) {
      return expr.toString();
    } else {
      return "";
    }
  }

  /**
   * @param var2inputMap
   * @param state2latchMap
   * @param postStateLabels
   * @param transitionMap
   * @param bool2builderMap
   * @param bool2aigMap
   * @return
   */
  public static Fol2AigProof createProof(Map<LeafExpression, Map<Integer, AigExpr>> var2inputMap,
      Map<StateRelation, Map<Integer, AigExpr>> state2latchMap,
      Map<Relation, IntSet> postStateLabels,
      Map<Transition, Map<Formula, BooleanValue>> transitionMap,
      Map<Integer, AigBuilder> bool2builderMap, Map<Integer, AigExpr> bool2aigMap) {
    return new Fol2AigProof(var2inputMap, state2latchMap, postStateLabels, transitionMap,
                            bool2builderMap, bool2aigMap);
  }

}
