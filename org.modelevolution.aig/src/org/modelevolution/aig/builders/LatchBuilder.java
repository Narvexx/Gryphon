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
import java.util.Collections;
import java.util.Iterator;

import org.modelevolution.aig.AigExpr;
import org.modelevolution.aig.AigFactory;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public final class LatchBuilder extends AbstractLatchBuilder {
  private Collection<AigBuilder> inputs;
  private final Collection<AigBuilder> satInputs;
  private final Collection<AigBuilder> unsatInputs;
  private final AigExpr initialState;
  private final AigBuilderFactory builderFactory;
  private final int label;
  private boolean hasFixedNextState;

  /**
   * @param label
   * @param builderFactory
   * 
   */
  LatchBuilder(final AigFactory factory, int label, AigBuilderFactory builderFactory,
      final AigExpr initialState) {
    super(factory);
    assert factory != null;
    assert builderFactory != null;
    assert initialState != null;
    assert initialState == AigExpr.FALSE || initialState == AigExpr.TRUE;
    this.builderFactory = builderFactory;
    this.initialState = initialState;
    this.inputs = new ArrayList<>();
    this.satInputs = new ArrayList<>();
    this.unsatInputs = new ArrayList<>();
    this.label = label;
    hasFixedNextState = false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.aig.builders.AigBuilder#toAig()
   */
  @Override
  public AigExpr toAig() {
    if (inputs.isEmpty() && satInputs.isEmpty() && unsatInputs.isEmpty())
      throw new IllegalStateException("Inputs are emtpy.");
    if (!inputs.isEmpty() && (!satInputs.isEmpty() || !unsatInputs.isEmpty()))
      throw new IllegalStateException("Multiple ambiguous inputs set.");

    if (factory.isCached(this)) {
      return factory.cached(this);
    } else {
      if (hasFixedNextState)
        return factory.cache(this, initialState);
      else
        return factory.cache(this, factory.aigLatch(this));
    }
  }

  /**
   * @return
   */
  protected AigExpr callToAigOnInput() {
    /*
     * if (hasFixedNextState) return initialState; else return
     * factory.aigLatch(this);
     */
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.aig.builders.AigBuilder#inputs(org.modelevolution.aig
   * .builders.AigBuilder[])
   */
  @Override
  public void inputs(AigBuilder... builders) {
    if (builders == null)
      throw new NullPointerException("builders==null.");

    inputs = new ArrayList<>(builders.length);
    for (final AigBuilder builder : builders) {
      if (builder != null)
        inputs.add(builder);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.aig.builders.AigBuilder#inputs(org.modelevolution.aig
   * .builders.AigBuilder[])
   */
  @Override
  public void inputs(Collection<? extends AigBuilder> builders) {
    if (builders == null)
      throw new NullPointerException("builders==null.");

    inputs = new ArrayList<>(builders.size());
    for (final AigBuilder builder : builders) {
      if (builder != null)
        inputs.add(builder);
    }
  }

  public boolean addNegativeInput(final AigBuilder condition) {
    if (condition == null)
      throw new NullPointerException("condition == null.");
    return unsatInputs.add(condition);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.aig.builders.AigBuilder#label()
   */
  @Override
  public int label() {
    return this.label;
  }

  /**
   * @param andGate
   * @return
   */
  public boolean addPositiveInput(final AigBuilder condition) {
    if (condition == null)
      throw new NullPointerException("condition == null.");
    return satInputs.add(condition);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.aig.builders.AigBuilder#toString()
   */
  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("l");
    sb.append(label());
    sb.append("=[");
    if (inputs.isEmpty() && satInputs.isEmpty() && unsatInputs.isEmpty())
      return sb.append("??]").toString();

    appendAll(sb, inputs());
    sb.append("]");
    return sb.toString();
  }

  /**
   * @param sb
   * @param inputs
   * @return
   */
  private void appendAll(final StringBuffer sb, Collection<AigBuilder> inputs) {
    int i = 0;
    final Iterator<AigBuilder> it = inputs.iterator();
    while (it.hasNext()) {
      final AigBuilder input = it.next();
      if (input instanceof LatchBuilder)
        sb.append(input.label());
      else
        sb.append(input);
      if (i++ < inputs.size() - 1)
        sb.append(" & ");
    }
  }

  // /**
  // * @param sb
  // * @return
  // */
  // private int insertSeperator(StringBuffer sb) {
  // sb.append(" & ");
  // return 1;
  // }

  /**
   * @return
   */
  @Override
  public AigExpr initialState() {
    return initialState;
  }

  /**
   * Note the next state defaults to {@link AigBuilder#TRUE TRUE} if no input
   * has been set.
   * 
   * @return
   */
  @Override
  public AigBuilder nextState() {
    return builderFactory.andGate(inputs());
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.aig.builders.AigBuilder#accept(org.modelevolution.aig
   * .builders.AigBuilderVisitor)
   */
  @Override
  public void accept(final AigBuilderVisitor visitor) {
    visitor.visit(this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.aig.builders.AigBuilder#inputs()
   */
  @Override
  public Collection<AigBuilder> inputs() {
    if (inputs.isEmpty()) {
      if (unsatInputs.isEmpty()) {
        if (satInputs.isEmpty())
          return Collections.emptyList();
        else
          return Collections.unmodifiableCollection(satInputs);
      } else
        return Collections.unmodifiableCollection(unsatInputs);
    } else
      return Collections.unmodifiableCollection(inputs);
  }

  /**
   * @return the isUnmodifiable
   */
  @Override
  public boolean hasFixedNextState() {
    return hasFixedNextState;
  }

  /**
   * @param haseFixedNextState
   *          the isUnmodifiable to set
   */
  public void setHasFixedNextState(boolean haseFixedNextState) {
    this.hasFixedNextState = haseFixedNextState;
  }
}
