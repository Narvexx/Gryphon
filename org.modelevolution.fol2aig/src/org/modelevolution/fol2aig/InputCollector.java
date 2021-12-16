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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.modelevolution.fol2aig;

import static kodkod.engine.bool.Operator.AND;
import static kodkod.engine.bool.Operator.ITE;
import static kodkod.engine.bool.Operator.OR;
import static kodkod.engine.bool.Operator.NOT;

import java.security.acl.NotOwnerException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NoPermissionException;

import kodkod.engine.bool.BooleanFormula;
import kodkod.engine.bool.Operator;
import kodkod.engine.bool.Operator.Nary;

import org.modelevolution.aig.builders.AbstractAigBuilder;
import org.modelevolution.aig.builders.AigBuilder;
import org.modelevolution.aig.builders.AigBuilderFactory;
import org.modelevolution.aig.builders.InputBuilder;
import org.modelevolution.aig.builders.LatchBuilder;

/**
 * @author Sebastian Gabmeyer
 * 
 */
class InputCollector {

  private static AigBuilderFactory factory;

  public static void init(final AigBuilderFactory factory) {
    InputCollector.factory = factory;
  }

  static InputCollector create(final BooleanFormula gate, final InputCollector parent) {
    final InputCollector collector;
    if (parent == null) {
      /* if parent == null, this is the top input collector */
      collector = new InputCollector(gate);
      assert !collector.hasRoot() && !collector.isRoot();
      if (gate.op() == ITE || gate.op() == OR)
        collector.setRoot(true);
    } else {
      /*
       * Note: the collector from the above if clause (ie, parent == null) will
       * be the parent of the next created input collector; yet,
       * parent.hasRoot() will return false and thus we enter the following if
       * block even though the parent status of the above created collector is
       * already confirmed
       */
      if (!parent.hasRoot() && !parent.isRoot()) {
        if (parent.nop() == AND && gate.op() != AND)
          parent.setRoot(true);
      }
      collector = new InputCollector(gate, parent);
      assert !collector.isRoot();
      if (!collector.hasRoot() && (collector.nop() == ITE || collector.nop() == OR))
        collector.setRoot(true);
    }
    return collector;
  }

  // private Deque<Operator> opStack = new ArrayDeque<>();
  private final Operator op;

  private boolean isRoot;

  private final boolean hasRoot;

  // private Nary multiOp = null;
  // private Nary prevMultiOp = null;
  // private boolean isIfCollector = false;
  // private boolean isITECollector = false;
  private boolean isNegated;
  // private boolean tmpNegated;
  // private final int size;
  private final LatchBindings latchVars;
  private final List<AigBuilder> commons;
  private final List<AigBuilder> memoryCommons;
  // private int top = UNKNOWN_TOP;
  private final BooleanFormula gate;

  private final Operator nop;

  /**
   * @param gate
   * @param op2
   */
  private InputCollector(final BooleanFormula gate) {
    assert gate != null;
    this.gate = gate;
    isNegated = (gate.op() == NOT ? true : false);
    this.op = gate.op();
    this.nop = getNop(gate.op());
    /*
     * As this c'tor creates a top input collector only, this.op and this.nop
     * should be equal
     */
    assert op == nop;
    latchVars = new LatchBindings(factory);
    commons = new ArrayList<>();
    memoryCommons = new ArrayList<>();
    assert op != null;
    isRoot = false; /* this is always set from outside */
    hasRoot = false;
  }

  private InputCollector(final BooleanFormula gate, final InputCollector parent) {
    assert gate != null;
    assert parent != null;
    this.gate = gate;
    if (parent.op() == ITE && parent.gate().input(0) == gate) {
      /*
       * this gate is the IF part of the parent ITE gate; thus, we clear the
       * negation flag. Observe: !(i ? t : e) === i ? !t : !e, that is, the
       * negation has no effect on the if-condition
       */
      isNegated = false;
    } else {
      isNegated = (gate.op() == NOT ? !parent.isNegated() : parent.isNegated());

    }
    this.op = gate.op();
    this.nop = getNop(gate.op());
    // this.op = gate.op();
    // this.size = gate.size();
    latchVars = new LatchBindings(factory);
    commons = ((parent.op() == ITE && op == ITE) ? new ArrayList<AigBuilder>(parent.commons)
                                                : new ArrayList<AigBuilder>());
    memoryCommons = ((parent.op() == ITE && op == ITE)
                                                      ? new ArrayList<AigBuilder>(
                                                                                  parent.memoryCommons)
                                                      : new ArrayList<AigBuilder>());
    isRoot = false; /* this is always set from outside */
    hasRoot = (parent.isRoot() || parent.hasRoot());
  }

  /**
   * Determine the negation aware operator.
   * 
   * @param op
   * @return
   * @see #nop();
   */
  private Operator getNop(final Operator op) {
    if (op instanceof Nary && isNegated)
      return ((Nary) op).complement();
    else
      return op;
  }

  /**
   * @param value
   * 
   */
  private void setRoot(final boolean value) {
    isRoot = value;
  }

  /**
   * @param aigBuilder
   */
  void addCommon(AigBuilder common) {
    assert common != null;
    addCommons(common, common);
  }

  /**
   * @param input
   * @param latch
   */
  void addCommons(final AigBuilder input, final AigBuilder latch) {
    assert input != null;
    assert latch != null;
    this.commons.add((op() == NOT ? input.not() : input));
    this.memoryCommons.add((op() == NOT ? latch.not() : latch));
  }

  /**
   * @param collector
   * @param aigBuilder
   */
  void collectCommons(final InputCollector collector) {
    assert collector != null;
    assert collector.common() != null;
    if (collector.memoryCommon() == null) {
      addCommon(collector.common());
    } else {
      // assert collector.common() instanceof InputBuilder
      // || collector.common() instanceof LatchBuilder;
      // assert collector.common() == collector.memoryCommon()
      // || collector.memoryCommon() instanceof LatchBuilder;
      addCommons(collector.common(), collector.memoryCommon());
    }
  }

  /**
   * @return
   */
  private AigBuilder memoryCommon() {
    if (memoryCommons.isEmpty())
      return null;
    return memoryCommons.get(0);
  }

  /**
   * @param label
   */
  void collectVar(final int label) {
    // if (ptr >= primedVars.length)
    // throw new IllegalStateException("Cannot add more next states: "
    // + "insufficient allocation.");
    // primedVars[ptr] = (negated ? -label : label);
    // ptr += 1;
    latchVars.add((isNegated ? -label : label));
  }

  /**
   * @param vars
   */
  void collectVars(final LatchBindings vars) {
    this.latchVars.addAll(vars);
  }

  /**
   * @param label
   * @return
   * 
   */
  AigBuilder assembleCommons() {
    assert commons != null && !commons.isEmpty();
    final AigBuilder compound;
    final AigBuilder memoryCompound;
    if (op == AND) {
      compound = factory.andGate(commons);
      memoryCompound = factory.andGate(memoryCommons);
    } else if (op == OR) {
      compound = factory.orGate(commons);
      memoryCompound = factory.orGate(memoryCommons);
    } else if (op == ITE) {
      compound = factory.implicationGate(commons);
      memoryCompound = factory.implicationGate(memoryCommons);
    } else {
      throw new IllegalStateException("Cannot call assembleCommons() on a "
          + "collector with a(n) " + op() + " operator.");
    }
    /* commons.size() == 1 => commons.get(0) == compound; */
    assert commons.size() != 1 || commons.get(0) == compound;
    /* memoryCommons.size() == 1 => memoryCommons.get(0) == compound; */
    assert memoryCommons.size() != 1 || memoryCommons.get(0) == memoryCompound;
    commons.clear();
    commons.add(compound);
    memoryCommons.clear();
    memoryCommons.add(memoryCompound);
    return compound;
  }

  /**
   * @return
   */
  AigBuilder common() {
    if (commons.isEmpty())
      return null;
    return commons.get(0);
  }

  // abstract boolean checkStructure();

  /**
   * @return <code>true</code> if any of the gate's inputs contains a latch
   *         variable (ie a variable that represents a latch)
   */
  boolean containsLatchVars() {
    // for (int i = 0; i < primedVarsFound.length; i++) {
    // if (primedVarsFound[i]) return true;
    // }
    // return false;
    return latchVars.size() > 0;
  }

  /**
   * @param childCollector
   */
  void collectVarsOrCommon(final InputCollector childCollector) {
    assert (childCollector.containsLatchVars()) ^ (childCollector.containsCommons());
    if (childCollector.containsLatchVars())
      collectVars(childCollector.latchVars());
    else
      collectCommons(childCollector);
  }

  /**
   * @return
   */
  BooleanFormula gate() {
    return this.gate;
  }

  /**
   * @return
   */
  boolean hasRoot() {
    return hasRoot;
  }

  /**
   * @return
   */
  boolean isNegated() {
    return isNegated;
  }

  boolean isRoot() {
    return isRoot;
  }

  /**
   * @return
   */
  Operator op() {
    return op;
  }

  /**
   * @return the negation aware operator, i.e., nop() == OR if isNegated() &&
   *         op() == AND, nop() == AND if isNegated() && op() == OR, nop() = NOT
   *         if op() == NOT, nop() = ITE if op() == ITE.
   */
  Operator nop() {
    return nop;
  }

  /**
   * @return
   */
  LatchBindings latchVars() {
    return latchVars;
  }

  /**
   * @param cache
   */
  void exportLatches(final AigBuilderCache cache) {
    assert containsLatchVars();
    latchVars.exportTo(cache);
  }

  /**
   * Clear the list of collected common conditions and add the specified common
   * condition. If <code>NULL</code> is provided the current collected common
   * conditions are wiped.
   * 
   * @param common
   */
  void setCommon(AigBuilder common) {
    commons.clear();
    if (common != null)
      commons.add(common);
  }

  /**
   * 
   */
  void assignCommonToVars() {
    assert latchVars.size() > 0;
    assert commons.isEmpty() || commons.size() == 1;
    if (!commons.isEmpty()) {
      latchVars.addCondition(commons.get(0));
      commons.clear();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("[");
    sb.append(op());
    sb.append("(");
    sb.append((isNegated ? "n" : "-"));
    sb.append(nop());
    sb.append(isRoot ? "R" : "-");
    sb.append(hasRoot ? "r" : "-");
    sb.append(")");
    sb.append("c").append(commons.size());
    sb.append("L").append(latchVars.size());
    sb.append("]");
    return sb.toString();
  }

  /**
   * @return
   */
  public boolean containsCommons() {
    return commons.size() > 0;
  }
}