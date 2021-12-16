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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.modelevolution.aig.AigExpr;
import org.modelevolution.aig.AigFactory;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class BadBuilder extends AbstractAigBuilder {
  private AigBuilder input;

  /**
   * 
   */
  BadBuilder(final AigFactory factory) {
    super(factory);
    assert factory != null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.aig.builders.AigBuilder#toAig()
   */
  @Override
  public AigExpr toAig() {
    if (input == null)
      throw new IllegalStateException("this.input == null.");
    if (factory.isCached(this))
      return factory.cached(this);
    else
      return factory.cache(this, callToAigOnInput());
  }

  /**
   * @return
   */
  protected AigExpr callToAigOnInput() {
    return factory.aigBad(input.toAig());
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
      throw new NullPointerException("builders == null.");
    if (builders.length < 1)
      throw new IllegalArgumentException("builders.length < 1.");
    input = builders[0];
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
      throw new NullPointerException("builders == null.");
    final Iterator<? extends AigBuilder> it = builders.iterator();
    if (!it.hasNext())
      throw new IllegalArgumentException("builders.size < 1.");

    input = it.next();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.aig.builders.AigBuilder#label()
   */
  @Override
  public int label() {
    if (input != null)
      return input.label();
    else
      return UNKNOWN_LABEL;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.aig.builders.AigBuilder#toString()
   */
  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("b");
    sb.append(label());
    sb.append("=");
    if (input == null)
      sb.append("??").toString();
    if (input instanceof LatchBuilder)
      sb.append(input.label());
    else
      sb.append(input.toString());
    return sb.toString();
  }

  /**
   * Negate the input if any and return a new {@link BadBuilder} instance.
   * 
   * @see org.modelevolution.aig.builders.AigBuilder#not()
   */
  @Override
  public AigBuilder not() {
    final BadBuilder negBad = new BadBuilder(factory);
    negBad.input = input == null ? null : input.not();
    return negBad;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.aig.builders.AigBuilder#accept(org.modelevolution.aig
   * .builders.AigBuilderVisitor)
   */
  @Override
  public void accept(AigBuilderVisitor visitor) {
    visitor.visit(this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.aig.builders.AigBuilder#inputs()
   */
  @Override
  public Collection<AigBuilder> inputs() {
    if (input == null)
      return Collections.emptyList();
    else
      return Collections.singletonList(input);
  }

  // /*
  // * (non-Javadoc)
  // *
  // * @see org.modelevolution.aig.builders.AigBuilder#inputs()
  // */
  // @Override
  // public Collection<AigBuilder> inputs() {
  // if (input == null)
  // return Collections.emptyList();
  // return Collections.singleton(input);
  // }
}
