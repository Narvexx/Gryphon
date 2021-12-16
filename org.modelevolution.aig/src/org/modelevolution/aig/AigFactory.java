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
package org.modelevolution.aig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.modelevolution.aig.AigLatch.AigLatchProxy;
import org.modelevolution.aig.builders.AbstractLatchBuilder;
import org.modelevolution.aig.builders.LatchedActivator;
import org.modelevolution.aig.builders.AbstractAigBuilder;
import org.modelevolution.aig.builders.AigBuilder;
import org.modelevolution.aig.builders.LatchBuilder;
import org.modelevolution.commons.logic.Operator.Nary;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public final class AigFactory {

  private AigLit lastCreated;
  private final Map<AigBuilder, AigExpr> builderCache;
  private final List<AigExpr> aigAnds;
  private AigAnd lastAndExpr;

  /**
   * 
   */
  public AigFactory() {
    lastCreated = AigExpr.FALSE;
    aigAnds = new ArrayList<>(500);
    lastAndExpr = null;
    builderCache = new IdentityHashMap<>();
    cache(AigBuilder.FALSE, AigExpr.FALSE);
    cache(AigBuilder.TRUE, AigExpr.TRUE);
  }

  /**
   * @return a fresh AIG literal.
   */
  public AigLit aigLiteral() {
    lastCreated = new AigLit(nextVar());
    return lastCreated;
  }

  public AigLatch aigLatch(final AigExpr nextState, final AigExpr initialState) {
    return new AigLatch(aigLiteral(), nextState, initialState);
  }

  public AigLatch aigLatch(final AigExpr nextState) {
    return new AigLatch(aigLiteral(), nextState, AigLit.FALSE);
  }

  public AigLatchProxy aigLatch(final AbstractLatchBuilder latchBuilder) {
    return new AigLatch.AigLatchProxy(aigLiteral(), latchBuilder);
  }

  public AigInput aigInput() {
    return new AigInput(aigLiteral());
  }

  /**
   * @param expr
   * @return
   */
  public AigInv aigInv(AigExpr expr) {
    return new AigInv(expr);
  }

  public AigOutput aigOutput(final AigExpr expr) {
    return new AigOutput(expr);
  }

  /**
   * @param aig
   * @return
   */
  public AigBad aigBad(final AigExpr expr) {
    return new AigBad(expr);
  }

  /**
   * @return the next, consecutive variable value
   */
  private int nextVar() {
    return lastCreated.var() + 1;
  }

  public AigExpr notGate(final AigExpr input) {
    if (input == null)
      throw new NullPointerException("Reason: input == null");
    if (input == AigExpr.FALSE)
      return AbstractAigExpr.TRUE;
    if (input == AigExpr.TRUE)
      return AigExpr.FALSE;
    if (input instanceof AigNot)
      return input.input(0);
    return new AigNot(input);
  }

  public List<AigExpr> andGates() {
    /* Possibly EXPENSIVE assertion */
    assert sorted(aigAnds).equals(aigAnds);
    return aigAnds;
  }

  /**
   * @param aigAnds2
   * @return
   * @specfunc
   */
  private List<AigExpr> sorted(List<AigExpr> aigAnds) {
    final ArrayList<AigExpr> sortedList = new ArrayList<>(aigAnds);
    Collections.sort(sortedList, new Comparator<AigExpr>() {

      @Override
      public int compare(AigExpr o1, AigExpr o2) {
        assert o1 instanceof AigAnd;
        assert o2 instanceof AigAnd;
        if (o1 == null || o2 == null)
          throw new NullPointerException();
        if (o1.var() < o2.var())
          return -1;
        else if (o1.var() > o2.var())
          return 1;
        else
          // (o1.var() == o2.var())
          return 0;
      }
    });
    return sortedList;
  }

  /**
   * @param builder
   * @param andExpr
   * @return
   */
  public AigExpr cache(AigBuilder builder, AigExpr expr) {
    if (expr instanceof AigLatchProxy)
      return cache(builder, (AigLatchProxy) expr);
    if (expr instanceof AigAnd || expr instanceof AigNot && expr.input(0) instanceof AigAnd) {
      assert expr instanceof AigAnd || expr instanceof AigNot || expr instanceof AigLit;
      assert !(expr instanceof AigNot)
          || (expr.input(0) instanceof AigAnd || expr.input(0) instanceof AigLit);
      final AigAnd andExpr;
      if (expr instanceof AigNot)
        andExpr = (AigAnd) expr.input(0);
      else {
        assert expr instanceof AigAnd;
        andExpr = (AigAnd) expr;
      }

      lastAndExpr = (AigAnd) collectAllAndGatesBetween(lastAndExpr, andExpr);

      assert lastAndExpr != null;
      assert lastAndExpr.literal() >= andExpr.literal();
      // assert incrementsBy2(aigAnds);
    }

    final AigExpr old;
    // if (expr instanceof AigLatchProxy)
    // old = builderCache.put(builder, ((AigLatchProxy) expr).latch());
    // else
    old = builderCache.put(builder, expr);
    assert old == null;
    assert builderCache.containsKey(builder);
    return expr;
  }

  public AigExpr cache(AigBuilder builder, AigLatchProxy proxy) {
    final AigExpr old = builderCache.put(builder, proxy.latch());
    assert old == null;
    assert builderCache.containsKey(builder);
    return proxy;
  }

  // @specfunc
  // private boolean incrementsBy2(List<AigExpr> aigExprs) {
  // if (aigExprs == null)
  // return false;
  // if (aigExprs.isEmpty() || aigExprs.size() == 1)
  // return true;
  // assert aigExprs.size() > 1;
  // boolean success = true;
  // Iterator<AigExpr> i = aigExprs.iterator();
  // // Iterator<AigExpr> j = aigInputs.iterator();
  // AigExpr current = i.next();
  // do {
  // AigExpr next = i.next();
  // success &= (current.literal() + 2) == (next.literal());
  // current = next;
  // } while (i.hasNext());
  // return success;
  // }

  /**
   * Collects from the given expression <code>expr</code> all AND gates down to
   * the gate <code>g</code> where <code>g.label() <= bottomLabel</code>. Note
   * that the inputs to a gate have a smaller label than their parent gate;
   * thus, descending into the inputs of a gate decreases the label. The
   * <code>bottomLabel</code> is equivalent to the label of the last AND gate
   * added to <code>aigAnds</code>, the list of AND gates. If we encounter this
   * last added gate, it label will be equivalent to <code>bottomLabel</code>
   * and we can stop searching for more AND gates (because those will already be
   * in aigAnds).
   * 
   * @param lastAnd
   * @param currentAnd
   */
  private AigExpr collectAllAndGatesBetween(final AigAnd lastAnd, final AigAnd currentAnd) {
    return currentAnd.accept(new AigVisitor<AigExpr, Collection<AigExpr>>() {

      /*
       * (non-Javadoc)
       * 
       * @see
       * org.modelevolution.aig.AigVisitor#visit(org.modelevolution.aig.AigAnd,
       * java.lang.Object)
       */
      @Override
      public AigExpr visit(AigAnd aigAnd, Collection<AigExpr> ands) {
        if (lastAnd != null && aigAnd.literal() <= lastAnd.literal()) {
          if (aigAnd.literal() < lastAnd.literal())
            return lastAnd;
          else
            return aigAnd;
        }
        aigAnd.input(1).accept(this, ands);
        aigAnd.input(0).accept(this, ands);
        ands.add(aigAnd);
        return aigAnd;
      }

      /*
       * (non-Javadoc)
       * 
       * @see
       * org.modelevolution.aig.AigVisitor#visit(org.modelevolution.aig.AigBad,
       * java.lang.Object)
       */
      @Override
      public AigExpr visit(AigBad aigBad, Collection<AigExpr> ands) {
        /* we should not enter this method */
        assert false;
        return null;
      }

      /*
       * (non-Javadoc)
       * 
       * @see
       * org.modelevolution.aig.AigVisitor#visit(org.modelevolution.aig.AigInput
       * , java.lang.Object)
       */
      @Override
      public AigExpr visit(AigInput aigInput, Collection<AigExpr> ands) {
        return null;
      }

      /*
       * (non-Javadoc)
       * 
       * @see
       * org.modelevolution.aig.AigVisitor#visit(org.modelevolution.aig.AigInv,
       * java.lang.Object)
       */
      @Override
      public AigExpr visit(AigInv aigInv, Collection<AigExpr> ands) {
        /* we should not enter this method */
        assert false;
        return null;
      }

      /*
       * (non-Javadoc)
       * 
       * @see
       * org.modelevolution.aig.AigVisitor#visit(org.modelevolution.aig.AigLatch
       * , java.lang.Object)
       */
      @Override
      public AigExpr visit(AigLatch aigLatch, Collection<AigExpr> ands) {
        return null;
      }

      /*
       * (non-Javadoc)
       * 
       * @see
       * org.modelevolution.aig.AigVisitor#visit(org.modelevolution.aig.AigLit,
       * java.lang.Object)
       */
      @Override
      public AigExpr visit(AigLit aigLit, Collection<AigExpr> ands) {
        return null;
      }

      /*
       * (non-Javadoc)
       * 
       * @see
       * org.modelevolution.aig.AigVisitor#visit(org.modelevolution.aig.AigNot,
       * java.lang.Object)
       */
      @Override
      public AigExpr visit(AigNot aigNot, Collection<AigExpr> ands) {
        return aigNot.input(0).accept(this, ands);
      }

      /*
       * (non-Javadoc)
       * 
       * @see
       * org.modelevolution.aig.AigVisitor#visit(org.modelevolution.aig.AigOutput
       * , java.lang.Object)
       */
      @Override
      public AigExpr visit(AigOutput aigOutput, Collection<AigExpr> ands) {
        /* we should not enter this method */
        assert false;
        return null;
      }
    }, aigAnds);
  }

  /**
   * @param builder
   * @return
   */
  public AigExpr cached(AigBuilder builder) {
    return builderCache.get(builder);
  }

  /**
   * @param builder
   * @return
   */
  public boolean isCached(AigBuilder builder) {
    return builderCache.containsKey(builder);
  }

  /**
   * Create an AIG expression that represents the formula
   * <code>expr1 op expr2 op ... op exprN</code> where
   * <code>exprs = {expr1,..., exprN}</code>.
   * 
   * @param op
   * @param exprs
   * 
   * @return
   */
  public AigExpr gate(final Nary op, final AigExpr... exprs) {
    if (op == null)
      throw new NullPointerException("Reason: op == null.");
    if (exprs == null)
      throw new NullPointerException("Reason: exprs == null.");
    return gate(op, Arrays.asList(exprs));
  }

  /**
   * Create an AIG expression that represents the formula
   * <code>expr1 op expr2 op ... op exprN</code> where
   * <code>exprs = {expr1,..., exprN}</code>.
   * 
   * @param op
   * @param exprs
   * 
   * @return
   */
  public AigExpr gate(final Nary op, final Collection<AigExpr> exprs) {
    if (op == null)
      throw new NullPointerException("Reason: op == null.");
    if (op == Nary.AND)
      return andGate(exprs);
    else if (op == Nary.OR)
      return orGate(exprs);
    else
      throw new IllegalArgumentException("Reason: op != Nary.AND & " + "op != Nary.OR. Operator '"
          + op.toString() + "' is not supported.");
  }

  /**
   * Creates an AIG expression that is equivalent to <code>lhs => rhs</code>.
   * 
   * @param lhs
   *          the antecedent
   * @param rhs
   *          the consequent
   */
  public AigExpr implicationGate(final AigExpr lhs, final AigExpr rhs) {
    if (lhs == null)
      throw new NullPointerException("Reason: lhs == null.");
    if (rhs == null)
      throw new NullPointerException("Reason: rhs == null.");
    return notGate(andGate(lhs, notGate(rhs)));
  }

  /**
   * Creates an AIG expression that is equivalent to
   * <code>expr1 AND expr2 AND ... AND exprN</code> where
   * <code>exprs = {expr1,..., exprN}</code>.
   * 
   * @param exprs
   * 
   * @return an AIG expression equivalent to
   *         <code>expr1 AND expr2 AND ... AND exprN</code>
   */
  public AigExpr andGate(final Collection<AigExpr> exprs) {
    if (exprs == null)
      throw new NullPointerException("exprs == null.");
    return new AndAssembler(this, exprs).assemble();
  }

  public AigExpr andGate(final AigExpr... exprs) {
    if (exprs == null)
      throw new NullPointerException("exprs == null.");
    return new AndAssembler(this, exprs).assemble();
  }

  /**
   * Creates an AIG expression that is equivalent to
   * <code>expr1 OR expr2 OR ... OR exprN</code> where
   * <code>exprs = {expr1,..., exprN}</code>.
   * 
   * @param exprs
   * 
   * @return an AIG expression equivalent to
   *         <code>expr1 OR expr2 OR ... OR exprN</code>
   */
  public AigExpr orGate(final Collection<AigExpr> exprs) {
    if (exprs == null)
      throw new NullPointerException("exprs == null.");
    return new OrAssembler(this, exprs).assemble();
  }

  public AigExpr orGate(final AigExpr... exprs) {
    if (exprs == null)
      throw new NullPointerException("exprs == null.");
    return new OrAssembler(this, exprs).assemble();
  }

  AigExpr andGate(final AigExpr expr1, final AigExpr expr2) {
    if (expr1 == null | expr2 == null)
      throw new NullPointerException();
    final AigLit aigLit = aigLiteral();
    if (expr1.literal() > expr2.literal())
      return new AigAnd(aigLit, expr1, expr2);
    else
      return new AigAnd(aigLit, expr2, expr1);
  }
}
