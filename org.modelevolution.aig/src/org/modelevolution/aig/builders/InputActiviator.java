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

import java.util.Collection;
import java.util.Collections;

import org.modelevolution.aig.AigExpr;
import org.modelevolution.aig.AigFactory;

/**
 * @author Sebastian Gabmeyer
 *
 */
public class InputActiviator<T> extends AbstractAigBuilder implements Activator<T> {

  private final T activated;
  private final int label;

  /**
   * @param factory
   */
  InputActiviator(final AigFactory factory, final int label, final T activated) {
    super(factory);
    this.label = label;
    this.activated = activated;
  }

  /* (non-Javadoc)
   * @see org.modelevolution.aig.builders.AigBuilder#inputs(org.modelevolution.aig.builders.AigBuilder[])
   */
  @Override
  public void inputs(final AigBuilder... builders) {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see org.modelevolution.aig.builders.AigBuilder#inputs(java.util.Collection)
   */
  @Override
  public void inputs(final Collection<? extends AigBuilder> builders) {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see org.modelevolution.aig.builders.AigBuilder#label()
   */
  @Override
  public int label() {
    return this.label;
  }

  /* (non-Javadoc)
   * @see org.modelevolution.aig.builders.AigBuilder#accept(org.modelevolution.aig.builders.AigBuilderVisitor)
   */
  @Override
  public void accept(final AigBuilderVisitor visitor) {
    visitor.visit(this);
  }

  /* (non-Javadoc)
   * @see org.modelevolution.aig.builders.AigBuilder#inputs()
   */
  @Override
  public Collection<AigBuilder> inputs() {
    return Collections.emptyList();
  }

  /* (non-Javadoc)
   * @see org.modelevolution.aig.builders.Activatable#getActivated()
   */
  @Override
  public T getActivated() {
    return this.activated;
  }

  /* (non-Javadoc)
   * @see org.modelevolution.aig.builders.AbstractAigBuilder#callToAigOnInput()
   */
  @Override
  protected AigExpr callToAigOnInput() {
    return factory.aigInput();
  }
}
