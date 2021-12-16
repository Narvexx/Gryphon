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
import java.util.Iterator;

import org.modelevolution.aig.AigExpr;
import org.modelevolution.aig.AigFactory;
import org.modelevolution.aig.AigOutput;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class OutputBuilder extends AbstractAigBuilder {

  private AigBuilder input;

  /**
   * @param factory
   */
  OutputBuilder(final AigFactory factory) {
    super(factory);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.aig.builders.AigBuilder#toAig()
   */
  @Override
  public AigExpr toAig() {
    if (input == null)
      throw new NullPointerException("this.input == null.");
    return super.toAig();
  }

  /**
   * @return
   */
  @Override
  protected AigOutput callToAigOnInput() {
    return factory.aigOutput(input.toAig());
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
      throw new IllegalArgumentException("builders.size < 0.");
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
    final StringBuffer sb = new StringBuffer("o");
    sb.append(label());
    sb.append("=");
    if (input == null)
      sb.append("??");
    else
      sb.append(input.toString());
    return sb.toString();
  }

  /**
   * Negate the input if any and return a new {@link OutputBuilder} instance
   * with the negated input.
   * 
   * @see org.modelevolution.aig.builders.AigBuilder#not()
   */
  @Override
  public AigBuilder not() {
    final OutputBuilder negOutput = new OutputBuilder(factory);
    negOutput.input = input == null ? null : input.not();
    return negOutput;
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
    throw new UnsupportedOperationException(); // TODO: Implement this!
  }

  // /*
  // * (non-Javadoc)
  // *
  // * @see org.modelevolution.aig.builders.AigBuilder#inputs()
  // */
  // @Override
  // public Collection<AigBuilder> inputs() {
  // return Collections.singletonList(input);
  // }
}
