/*
 * KodkodMod -- Copyright (c) 2014-present, Sebastian Gabmeyer
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

import static org.modelevolution.commons.logic.OpConverter.convert;

import java.util.Map;

import kodkod.engine.bool.BooleanFormula;
import kodkod.engine.bool.BooleanVariable;
import kodkod.engine.bool.BooleanVisitor;
import kodkod.engine.bool.ITEGate;
import kodkod.engine.bool.MultiGate;
import kodkod.engine.bool.NotGate;
import kodkod.engine.bool.Operator;

import org.modelevolution.aig.builders.AbstractAigBuilder;
import org.modelevolution.aig.builders.AigBuilder;
import org.modelevolution.aig.builders.AigBuilderFactory;

/**
 * @author Sebastian Gabmeyer
 * 
 */
@Deprecated
public final class ConsequentTranslator2 {

  private final AigBuilderFactory factory;
  private int TOP;
  private final AigBuilderCache cache;
  private AbstractAigBuilder premise;

  /**
   * @param factory
   */
  public ConsequentTranslator2(final AigBuilderFactory factory,
      final AigBuilderCache cache) {
    this.factory = factory;
    this.cache = cache;
  }

  /**
   * @param formula
   * @return
   */
  public Map<Integer, AbstractAigBuilder> translate(final BooleanFormula formula) {
    AigInfo info = new AigInfo();
    assert formula.op() == Operator.AND || formula.op() == Operator.OR
        || formula.op() == Operator.ITE;
    assert formula.op() != Operator.AND || checkAndGate((MultiGate) formula);
    assert formula.op() != Operator.OR || checkOrGate((MultiGate) formula);
    assert formula.op() != Operator.ITE || checkITEGate((ITEGate) formula);
    TOP = formula.op() == Operator.AND ? 2 : 1;
    formula.accept(new ConsequentVisitor(), info);
    return null;
  }

  // /**
  // * @param iterator
  // * @return
  // */
  // private boolean checkOrGates(Iterator<BooleanFormula> it) {
  // boolean success = true;
  // while (it.hasNext()) {
  // BooleanFormula subformula = it.next();
  // assert subformula.op() == Operator.OR;
  // assert subformula instanceof MultiGate;
  // success &= checkOrGate((MultiGate) subformula);
  // }
  // return success;
  // }

  /**
   * @param subformula
   * @return
   */
  private boolean checkOrGate(MultiGate formula) {
    assert formula.op() == Operator.OR;
    boolean nextStateFound = false;
    nextStateFound = formula.accept(new BooleanVisitor<Boolean, Boolean>() {

      @Override
      public Boolean visit(MultiGate multigate, Boolean nextStateFound) {
        for (BooleanFormula input : multigate) {
          boolean containsNextState = input.accept(this, nextStateFound);
          if (nextStateFound && containsNextState)
            return false;
          else
            nextStateFound |= containsNextState;
        }
        return nextStateFound;
      }

      @Override
      public Boolean visit(ITEGate ite, Boolean nextStateFound) {
        /*
         * We do not expect to find a next state label in an ITE gate that is an
         * input to an OR gate; thus, we check that this ITE gate does not
         * contain a next state label: if it does not, we return false, because
         * no next state was found (in this case, this is a good thing);
         * otherwise, we throw an assertion error, because we did not expect to
         * find a next state label in this ITE gate.
         */
        if (containsNoNextState(ite))
          return false;
        else
          throw new AssertionError("Unexpected occurence of a next state "
              + "label in an ITE gate.");
      }

      @Override
      public Boolean visit(NotGate negation, Boolean found) {
        for (BooleanFormula input : negation) {
          found |= input.accept(this, found);
        }
        return found;
      }

      @Override
      public Boolean visit(BooleanVariable variable, Boolean found) {
        return cache.isPostLabel(variable.label());
      }
    }, nextStateFound);

    return nextStateFound;
  }

  private boolean checkITEGate(ITEGate ite) {
    int nextState = ite.accept(new BooleanVisitor<Integer, Object>() {

      @Override
      public Integer visit(MultiGate multigate, Object arg) {
        boolean[] containsNextState = new boolean[multigate.size()];
        int res = 0;
        int i = 0;
        for (BooleanFormula input : multigate) {
          int tmp = input.accept(this, null);
          containsNextState[i] = cache.isPostLabel(tmp);
        }
        boolean success = true;
        for (int j = 0; j < containsNextState.length - 1; j++) {

        }
        return res;
      }

      @Override
      public Integer visit(ITEGate ite, Object arg) {
        assert containsNoNextState(ite.input(0));
        int thenResult = ite.input(1).accept(this, null);
        int elseResult = ite.input(2).accept(this, null);
        if (thenResult != 0 && elseResult != 0 && thenResult + elseResult == 0)
          return thenResult;
        else if (thenResult != 0 && elseResult != 0 && thenResult + elseResult != 0)
          throw new AssertionError();
        else if (thenResult != 0 ^ elseResult != 0)
          return (thenResult != 0 ? thenResult : elseResult);
        else
          return 0;
      }

      @Override
      public Integer visit(NotGate negation, Object arg) {
        int res = negation.input(0).accept(this, null);
        if (res != 0)
          return -res;
        else
          return 0;
      }

      @Override
      public Integer visit(BooleanVariable variable, Object arg) {
        if (cache.isPostLabel(variable.label()))
          return variable.label();
        else
          return 0;
      }
    }, null);

    return nextState != 0;
  }

  private boolean containsNoNextState(BooleanFormula formula) {
    return formula.accept(new BooleanVisitor<Boolean, Object>() {

      @Override
      public Boolean visit(MultiGate multigate, Object arg) {
        boolean noNextStateFound = true;
        for (BooleanFormula input : multigate) {
          noNextStateFound &= input.accept(this, null);
        }
        return noNextStateFound;
      }

      @Override
      public Boolean visit(ITEGate ite, Object arg) {
        boolean noNextStateFound = true;
        noNextStateFound &= ite.input(0).accept(this, null);
        noNextStateFound &= ite.input(1).accept(this, null);
        noNextStateFound &= ite.input(2).accept(this, null);
        return noNextStateFound;
      }

      @Override
      public Boolean visit(NotGate negation, Object arg) {
        return negation.input(0).accept(this, null);
      }

      @Override
      public Boolean visit(BooleanVariable variable, Object arg) {
        return !cache.isPostLabel(variable.label());
      }
    }, null);
  }

  /**
   * @param formula
   * @return
   */
  private boolean checkAndGate(MultiGate formula) {
    assert formula.op() == Operator.AND;
    boolean success = true;
    success = formula.accept(new BooleanVisitor<Boolean, Object>() {

      @Override
      public Boolean visit(MultiGate multigate, Object arg) {
        boolean success = true;
        if (multigate.op() == Operator.AND) {
          for (BooleanFormula input : multigate)
            success &= input.accept(this, null);
        } else if (multigate.op() == Operator.OR) {
          return checkOrGate(multigate);
        } else {
          throw new AssertionError();
        }
        return success;
      }

      @Override
      public Boolean visit(ITEGate ite, Object arg) {
        return checkITEGate(ite);
      }

      @Override
      public Boolean visit(NotGate negation, Object arg) {
        return negation.input(0).accept(this, null);
      }

      @Override
      public Boolean visit(BooleanVariable variable, Object arg) {
        /* We must not visit a Boolean variable with this visitor */
        throw new AssertionError();
      }
    }, null);

    return success;
  }

  private class ConsequentVisitor implements BooleanVisitor<AigBuilder, AigInfo> {

    /*
     * (non-Javadoc)
     * 
     * @see
     * kodkod.engine.bool.BooleanVisitor#visit(kodkod.engine.bool.MultiGate,
     * java.lang.Object)
     */
    @Override
    public AigBuilder visit(MultiGate multigate, AigInfo arg) {
      arg.pushOp(multigate.op());
      for (BooleanFormula input : multigate) {
        AigBuilder builder = input.accept(this, arg);
        if (arg.nextStateFound()) {
          if (arg.level() > TOP) {
            /*
             * we've reached the multigate that contains the next state var;
             * thus, remove the current gate's operator from the stack and
             * continue with the next top-level gate
             */
            arg.popOp();
            return null;
          }
          arg.resetNextStateFound();
        } else if (arg.doBacktrack()) {
          if (arg.level() >= TOP) { /* Note the >= instead of > */
            arg.popOp();
            return null;
          }
          arg.endBacktrack();
        } else {
          assert builder != null;
          arg.addCommon(builder);
        }
      }
      if (arg.level() > TOP) {
        assert multigate.op() == Operator.AND || multigate.op() == Operator.OR;
        AigBuilder builder = factory.gate(convert(multigate.op()), multigate.label(),
            arg.commons());
        arg.clearCommons();
        arg.addCommon(builder);
      } else if (arg.level() == TOP) {
        assert arg.nextStateFound();
        assignNextStateConditionToLatch(arg.state(), premise,
            factory.andGate(multigate.label(), arg.commons()));
        // cache.addThenCondition(arg.state(), factory.andGate(arg.commons()));
        arg.reset();
      }
      arg.popOp();
      return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see kodkod.engine.bool.BooleanVisitor#visit(kodkod.engine.bool.ITEGate,
     * java.lang.Object)
     */
    @Override
    public AigBuilder visit(ITEGate ite, AigInfo arg) {
      arg.pushOp(ite.op());
      AigBuilder ifBuilder = ite.input(0).accept(this, arg);
      assert ifBuilder != null;
      assert containsNoNextState(ite.input(0));
      arg.addCommon(ifBuilder);

      ite.input(1).accept(this, arg);
      assert arg.nextStateFound() || arg.doBacktrack();
      if (arg.nextStateFound()) {
        /*
         * Negated next state variable found at the bottom of the then-branch;
         * thus, create a latch condition.
         */
        assignNextStateConditionToLatch(arg.state(), premise,
            factory.andGate(ite.label(), arg.commons()).not());
        // cache.addThenCondition(arg.state(), factory.andGate(arg.commons()));
        arg.resetNextStateFound();
        /*
         * Remove the last common condition, i.e., the if-condition, negate it,
         * and explore the else-branch.
         */
        AigBuilder builder = arg.popCommon();
        arg.addCommon(builder.not());
      } else if (arg.doBacktrack()) {
        /*
         * Positive next state variable found at the bottom of the then-branch;
         * thus, skip it.
         */
        AigBuilder builder;
        do {
          builder = arg.popCommon();
        } while (builder != ifBuilder);
        assert builder == ifBuilder;
        arg.addCommon(builder.not());
        arg.endBacktrack();
      }

      ite.input(2).accept(this, arg);
      assert arg.nextStateFound() || arg.doBacktrack();
      if (arg.nextStateFound()) {
        assignNextStateConditionToLatch(arg.state(), premise,
            factory.andGate(ite.label(), arg.commons()));
        // cache.addThenCondition(arg.state(), factory.andGate(arg.commons()));
        arg.resetNextStateFound();
        arg.startBacktrack(); // reached end, so start backtrack
      }

      assert arg.doBacktrack();
      arg.popCommon();
      arg.popOp();
      return null;
    }

    /**
     * @param label
     * @param premise
     * @param consequent
     * @return
     */
    private void assignNextStateConditionToLatch(int label, AbstractAigBuilder premise,
        AigBuilder consequent) {
      AigBuilder condition = factory.implicationGate(label, premise, consequent);
      // cache.latchCache().addNextStateCondition(label, condition);
    }

    /*
     * (non-Javadoc)
     * 
     * @see kodkod.engine.bool.BooleanVisitor#visit(kodkod.engine.bool.NotGate,
     * java.lang.Object)
     */
    @Override
    public AigBuilder visit(NotGate negation, AigInfo arg) {
      arg.pushOp(negation.op());
      AigBuilder input = negation.input(0).accept(this, arg);
      arg.popOp();
      // if (arg.level() == TOP) arg.addCommon(input);
      return arg.doBacktrack() || arg.nextStateFound() ? null : input.not();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * kodkod.engine.bool.BooleanVisitor#visit(kodkod.engine.bool.BooleanVariable
     * , java.lang.Object)
     */
    @Override
    public AigBuilder visit(BooleanVariable variable, AigInfo arg) {
      final int label = variable.label();
      if (cache.isPostLabel(label)) {
        if (arg.peekOp() == Operator.NOT)
          arg.foundNextState(label);
        else
          arg.startBacktrack();
        return null;
      } else {
        // AigBuilder builder = cache.get(label);
        // if (arg.level() == TOP) arg.addCommon(builder);
        return cache.get(label);
      }
    }
  }

  /**
   * @param premise
   */
  public void setPremise(final AbstractAigBuilder premise) {
    this.premise = premise;
  }
}