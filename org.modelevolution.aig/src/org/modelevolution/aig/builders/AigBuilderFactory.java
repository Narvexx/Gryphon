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
package org.modelevolution.aig.builders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.modelevolution.aig.AigExpr;
import org.modelevolution.aig.AigFactory;
import org.modelevolution.commons.logic.Operator.Nary;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class AigBuilderFactory {

  private final AigFactory factory;
  private int lastLabel;

  /**
   * @param startLabel
   * @param factory
   * 
   */
  public AigBuilderFactory(int startLabel, AigFactory factory) {
    if (factory == null)
      throw new NullPointerException();
    if (startLabel < 1)
      throw new IllegalArgumentException();
    this.factory = factory;
    this.lastLabel = startLabel;
  }

  /**
   * @param conditions
   * @return
   */
  public AigBuilder andGate(Collection<AigBuilder> builders) {
    if (lastLabel > Integer.MAX_VALUE)
      throw new ArithmeticException("Overflow.");
    /* Note: if builder.size()<2, no new label is allocated */
    return andGate((builders.size() < 2 ? lastLabel : ++lastLabel), builders);
  }

  /**
   * @param label
   * @param ifthen
   * @param nifelse
   * @return
   */
  public AigBuilder andGate(int label, AigBuilder... builders) {
    if (label > lastLabel)
      throw new IllegalArgumentException("label > lastLabel.");
    if (builders == null)
      throw new NullPointerException("builders == null.");
    switch (builders.length) {
    case 0:
      return AigBuilder.TRUE;
    case 1:
      return builders[0];
    default:
      final AndBuilder and = new AndBuilder(factory, label);
      and.inputs(builders);
      return and;
    }
  }

  public AigBuilder andGate(AigBuilder... builders) {
    if (builders == null)
      throw new NullPointerException("builders == null.");
    if (lastLabel > Integer.MAX_VALUE)
      throw new ArithmeticException("Overflow.");
    switch (builders.length) {
    case 0:
      return AigBuilder.TRUE;
    case 1:
      return builders[0];
    default:
      final AndBuilder and = new AndBuilder(factory, ++lastLabel);
      and.inputs(builders);
      return and;
    }
  }

  /**
   * @param commons
   * @return
   */
  public AigBuilder andGate(int label, Collection<? extends AigBuilder> builders) {
    if (label > lastLabel)
      throw new IllegalArgumentException("label > lastLabel.");
    if (builders == null)
      throw new NullPointerException("builders == null.");
    switch (builders.size()) {
    case 0:
      return AigBuilder.TRUE;
    case 1:
      return builders.iterator().next();
    default:
      final AndBuilder and = new AndBuilder(factory, label);
      and.inputs(builders);
      return and;
    }
  }

  public AndBuilder createAndBuilder(int label) {
    if (label > lastLabel)
      throw new IllegalArgumentException("label > lastLabel.");
    return new AndBuilder(factory, label);
  }

  /**
   * @return
   */
  public BadBuilder createBadBuilder() {
    return new BadBuilder(factory);
  }

  public BadBuilder createBadBuilder(AigBuilder input) {
    final BadBuilder badBuilder = new BadBuilder(factory);
    badBuilder.inputs(input);
    return badBuilder;
  }

  public InputBuilder createInputBuilder(int label) {
    if (label > lastLabel)
      throw new IllegalArgumentException("label > lastLabel.");
    return new InputBuilder(factory, label);
  }

  public LatchBuilder createLatchBuilder(int label, final AigExpr initialState) {
    if (label > lastLabel)
      throw new IllegalArgumentException("label > lastLabel.");
    return new LatchBuilder(factory, label, this, initialState);
  }

  NotBuilder createNotBuilder() {
    return new NotBuilder(factory);
  }

  public NotBuilder createNotBuilder(final AigBuilder input) {
    final NotBuilder notBuilder = new NotBuilder(factory);
    notBuilder.inputs(input);
    return notBuilder;
  }

  OutputBuilder createOutputBuilder() {
    return new OutputBuilder(factory);
  }

  public OutputBuilder createOutputBuilder(final AigBuilder input) {
    final OutputBuilder outputBuilder = new OutputBuilder(factory);
    outputBuilder.inputs(input);
    return outputBuilder;
  }

  public AigBuilder gate(Nary op, Collection<? extends AigBuilder> builders) {
    if (lastLabel + builders.size() > Integer.MAX_VALUE)
      throw new ArithmeticException("Overflow.");
    return gate(op, ++this.lastLabel, builders);
  }

  /**
   * @param op
   * @param label
   * @param builders
   * @return
   */
  public AigBuilder gate(Nary op, int label, AigBuilder... builders) {
    if (op == null)
      throw new NullPointerException("op == null.");
    if (op == Nary.AND)
      return andGate(label, builders);
    else if (op == Nary.OR)
      return orGate(label, builders);
    else
      throw new IllegalArgumentException("op != Nary.AND and op != Nary.OR");
  }

  /**
   * @param op
   * @param label
   * @param commons
   * @return
   */
  public AigBuilder gate(Nary op, int label, Collection<? extends AigBuilder> builders) {
    if (label > lastLabel)
      throw new IllegalArgumentException("label > lastLabel.");
    if (op == null)
      throw new NullPointerException("op == null.");
    if (op == Nary.AND)
      return andGate(label, builders);
    else if (op == Nary.OR)
      return orGate(label, builders);
    else
      throw new IllegalArgumentException("op != Nary.AND and op != Nary.OR");
  }

  public AigBuilder implicationGate(AigBuilder lhsBuilder, AigBuilder rhsBuilder) {
    if (lastLabel > Integer.MAX_VALUE)
      throw new ArithmeticException("Overflow.");
    return implicationGate(++lastLabel, lhsBuilder, rhsBuilder);
  }

  /**
   * @param label
   * @param lhsBuilder
   * @param rhsBuilder
   * @return
   */
  public AigBuilder implicationGate(int label, AigBuilder lhsBuilder, AigBuilder rhsBuilder) {
    if (label > lastLabel)
      throw new IllegalArgumentException("label > lastLabel.");
    if (lhsBuilder == null)
      throw new NullPointerException("lhsBuilder == null.");
    if (rhsBuilder == null)
      throw new NullPointerException("rhsBuilder == null.");

    return andGate(label, lhsBuilder, rhsBuilder.not()).not();
  }

  // /**
  // * @param builder
  // * @return
  // */
  // public AigBuilder notGate(AigBuilder builder) {
  // if (builder == null)
  // throw new NullPointerException("builder == null.");
  // final NotBuilder not = new NotBuilder(factory);
  // not.inputs(builder);
  // return not;
  // }

  public AigBuilder orGate(Collection<? extends AigBuilder> builders) {
    if (lastLabel > Integer.MAX_VALUE)
      throw new ArithmeticException("Overflow.");
    return orGate((builders.size() < 2 ? lastLabel : ++lastLabel), builders);
  }

  /**
   * @param label
   * @param exprs
   * @return
   */
  public AigBuilder orGate(int label, AigBuilder... builders) {
    if (label > lastLabel)
      throw new IllegalArgumentException("label > lastLabel.");
    if (builders == null)
      throw new NullPointerException("builders == null.");
    switch (builders.length) {
    case 0:
      return AigBuilder.FALSE;
    case 1:
      return builders[0];
    default:
      final OrBuilder or = new OrBuilder(factory, label);
      or.inputs(builders);
      return or;
    }
  }

  public AigBuilder orGate(AigBuilder... builders) {
    if (lastLabel > Integer.MAX_VALUE)
      throw new ArithmeticException("Overflow.");
    if (builders == null)
      throw new NullPointerException("builders == null.");
    switch (builders.length) {
    case 0:
      return AigBuilder.FALSE;
    case 1:
      return builders[0];
    default:
      final OrBuilder or = new OrBuilder(factory, ++lastLabel);
      or.inputs(builders);
      return or;
    }
  }

  /**
   * @param label
   * @param exprs
   * @return
   */
  public AigBuilder orGate(int label, Collection<? extends AigBuilder> builders) {
    if (label > lastLabel)
      throw new IllegalArgumentException("label > lastLabel.");
    if (builders == null)
      throw new NullPointerException("builders == null.");
    switch (builders.size()) {
    case 0:
      return AigBuilder.FALSE;
    case 1:
      return builders.iterator().next();
    default:
      final OrBuilder or = new OrBuilder(factory, label);
      or.inputs(builders);
      return or;
    }
  }

  /**
   * @param notGate
   * @return
   */
  public AigBuilder shortAndGate(AigBuilder builder) {
    if (lastLabel > Integer.MAX_VALUE)
      throw new ArithmeticException("Overflow.");
    if (builder == null)
      throw new NullPointerException("builders == null.");
    return andGate(++lastLabel, builder, AigBuilder.TRUE);
  }

  /**
   * If builders.isEmpty returns AigBuilder.TRUE, i.e., the result of
   * <code>f => t</code>. If builders.size() == 1 returns builders.get(0), i.e.,
   * the result of <code>f => builders.get(0)</code>. If builders.size() == n, n
   * > 1 returns
   * <code>!(builders.get(0) & ... & builders.get(n-2)) => builders.get(n-1)</code>
   * .
   * 
   * @param commons
   * @return
   */
  public AigBuilder implicationGate(Collection<? extends AigBuilder> builders) {
    final Iterator<? extends AigBuilder> iterator = builders.iterator();
    switch (builders.size()) {
    case 0:
      return AigBuilder.TRUE;
    case 1:
      return iterator.next();
    case 2: {
      AigBuilder lhs = iterator.next();
      assert iterator.hasNext();
      AigBuilder rhs = iterator.next();
      assert !iterator.hasNext();
      return implicationGate(lhs, rhs);
    }
    default:
      Collection<AigBuilder> lhs = new ArrayList<>(builders.size() - 1);
      AigBuilder rhs = null;
      while (iterator.hasNext()) {
        AigBuilder tmp = iterator.next();
        if (iterator.hasNext()) {
          lhs.add(tmp);
        } else {
          rhs = tmp;
          break;
        }
      }
      assert rhs != null;
      return implicationGate(andGate(lhs), rhs);
    }
  }

  /**
   * @param transition
   *          TODO
   * @return
   */
  public <T> LatchedActivator<T> createLatchedActivator(T activated) {
    return new LatchedActivator<T>(factory, this, activated);
  }

  /**
   * @param t
   * @return
   */
  public <T> InputActiviator<T> createInputActivator(T activated) {
    return new InputActiviator<T>(factory, ++lastLabel, activated);
  }
}
