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

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import kodkod.engine.bool.Operator;

import org.modelevolution.aig.builders.AbstractAigBuilder;
import org.modelevolution.aig.builders.AigBuilder;

/**
 * @author Sebastian Gabmeyer
 * 
 */
class AigInfo {
  private boolean nextStateFound = false;
  private boolean backtrack = false;
  private int backtrackLevel = Integer.MAX_VALUE;
  // private int level = 0;
  private final Deque<AigBuilder> commons;
  private int state = -1;
  private final Deque<Operator> opStack;

  /**
   * 
   */
  AigInfo() {
    commons = new ArrayDeque<>();
    opStack = new ArrayDeque<>();
  }

  void addCommon(AigBuilder builder) {
    boolean success = false;
    if (builder != null) success = commons.offerFirst(builder);
    /* builder != null => success */
    assert builder == null | success;
  }

  void state(int value) {
    this.state = value;
  }

  int state() {
    return state;
  }

  Collection<AigBuilder> commons() {
    return commons;
  }

  /**
   * @return
   */
  public AigBuilder popCommon() {
    AigBuilder last = commons.pollFirst();
    assert last != null;
    return last;
  }

  void pushOp(final Operator op) {
    assert op != null;
    // /* homogeneous.peek() == null <=> opStack.peek() == null */
    // assert homogeneous.peek() != null | opStack.peek() == null;
    // assert opStack.peek() != null | homogeneous.peek() == null;
    /* opStack.isEmpty() => Operator.AND */
    assert !opStack.isEmpty() | op == Operator.AND;

    boolean success;
    // boolean h =
    // (homogeneous.peek() == null | level() <= 1) ? true
    // : op == opStack.peek();
    // success = homogeneous.offerFirst(h);
    // assert success;
    success = opStack.offerFirst(op);
    assert success;
  }

  Operator peekOp() {
    final Operator op = opStack.peek();
    assert op != null;
    return op;
  }

  Operator popOp() {
    final Operator op = opStack.pollFirst();
    assert op != null;
    // Boolean h = homogeneous.pollFirst();
    // assert h != null;
    return op;
  }

  void foundNextState(int label) {
    assert state == -1;
    state = label;
    nextStateFound = true;
  }

  void resetNextStateFound() {
    nextStateFound = false;
    state = -1;
    // backtrackLevel = Integer.MAX_VALUE;
  }

  boolean nextStateFound() {
    return nextStateFound;
  }

  boolean doBacktrack() {
    return backtrack;
  }

  /**
   * Memorize current level for backtracking.
   */
  void memLevel() {
    if (level() < backtrackLevel) backtrackLevel = level();
  }

  // AigInfo incLevel() {
  // // level += 1;
  // return this;
  // }

  // void decLevel() {
  // // level -= 1;
  // // return this;
  // }

  int level() {
    return opStack.size();
  }

  // /**
  // * @param b
  // */
  // public void doBacktrack(boolean b) {
  // backtrack = b;
  // }

  void startBacktrack() {
    backtrack = true;
  }

  void endBacktrack() {
    backtrack = false;
  }

  /**
   * 
   */
  void clearCommons() {
    commons.clear();
  }

  /**
   * 
   */
  void reset() {
    clearCommons();
    resetNextStateFound();
  }

  // /**
  // *
  // */
  // public void assembleNextStateCondition() {
  // final Boolean isHomogeneous = homogeneous.peek();
  // if (isHomogeneous != null && isHomogeneous == true) {
  // // this.nextStateCondition.put(state, )
  // }
  // }
}
