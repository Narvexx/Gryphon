/* 
 * org.modelevolution.aig -- Copyright (c) 2015-present, Sebastian Gabmeyer
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
package org.modelevolution.aig.builders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.modelevolution.aig.AigExpr;
import org.modelevolution.aig.AigFactory;

/**
 * The activator acts as a placeholder for an activation literal that guards
 * certain conditions. It is treated like latch and is thus stateful. Note that
 * it carries no label as the other builders do.
 * 
 * @author Sebastian Gabmeyer
 * 
 */
public class LatchedActivator<T> extends AbstractLatchBuilder implements Activator<T> {
  private Collection<AigBuilder> inputs;
  private final AigBuilderFactory builderFactory;
  private T activated;

  /**
   * @param builderFactory
   * @param activated
   *          TODO
   * 
   */
  LatchedActivator(final AigFactory factory, final AigBuilderFactory builderFactory, T activated) {
    super(factory);
    assert factory != null;
    this.builderFactory = builderFactory;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.aig.builders.AigBuilder#toAig()
   */
  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.aig.builders.Activatable#toAig()
   */
  @Override
  public AigExpr toAig() {
    if (inputs == null || inputs.isEmpty())
      throw new IllegalStateException("inputs == null || inputs.isEmpty().");

    return super.toAig();
  }

  /**
   * @return
   */
  protected AigExpr callToAigOnInput() {
    return factory.aigLatch(this);
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
      throw new NullPointerException();

    inputs = new ArrayList<>(builders.length);
    for (AigBuilder builder : builders) {
      if (builder != null)
        inputs.add(builder);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.aig.builders.AigBuilder#inputs(java.util.Collection)
   */
  @Override
  public void inputs(Collection<? extends AigBuilder> builders) {
    if (builders == null)
      throw new NullPointerException();

    inputs = new ArrayList<>(builders.size());
    for (AigBuilder builder : builders) {
      if (builder != null)
        inputs.add(builder);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.aig.builders.AigBuilder#label()
   */
  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.aig.builders.Activatable#label()
   */
  @Override
  public int label() {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.aig.builders.AigBuilder#toString()
   */
  @Override
  public String toString() {
    return "act";
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.aig.builders.AbstractLatchBuilder#nextState()
   */
  @Override
  public AigBuilder nextState() {
    if (inputs == null)
      throw new IllegalStateException("inputs == null.");
    return builderFactory.andGate(inputs);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.aig.builders.AbstractLatchBuilder#initialState()
   */
  @Override
  public AigExpr initialState() {
    return AigExpr.FALSE;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.aig.builders.AigBuilder#accept(org.modelevolution.aig
   * .builders.AigBuilderVisitor)
   */
  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.aig.builders.Activatable#accept(org.modelevolution.aig
   * .builders.AigBuilderVisitor)
   */
  @Override
  public void accept(final AigBuilderVisitor visitor) {
    visitor.visit((Activator<T>)this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.aig.builders.AigBuilder#inputs()
   */
  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.aig.builders.Activatable#inputs()
   */
  @Override
  public Collection<AigBuilder> inputs() {
    if (inputs == null)
      return Collections.emptyList();
    else
      return Collections.unmodifiableCollection(inputs);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.aig.builders.Activatable#getActivated()
   */
  @Override
  public T getActivated() {
    return this.activated;
  }

  /* (non-Javadoc)
   * @see org.modelevolution.aig.builders.AbstractLatchBuilder#hasFixedNextState()
   */
  public boolean hasFixedNextState() {
    return false;
  }

}
