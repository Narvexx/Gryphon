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
import org.modelevolution.aig.AigNot;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class NotBuilder extends AbstractAigBuilder {
  private AigBuilder input;

  /**
   * @param factory
   */
  NotBuilder(AigFactory factory) {
    super(factory);
    assert factory != null;
  }

  /**
   * @param factory2
   * @param andBuilder
   */
  NotBuilder(AigFactory factory, AigBuilder input) {
    super(factory);
    assert factory != null;
    assert input != null;
    this.input = input;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.aig.GateBuilder#toGate()
   */
  @Override
  public AigExpr toAig() {
    if (input == null)
      throw new NullPointerException("input == null.");
    return super.toAig();
  }

  /**
   * @return
   */
  protected AigExpr callToAigOnInput() {
    final AigExpr expr;
    if (input instanceof NotBuilder) {
      AigBuilder nestedInput = ((NotBuilder) input).input;
      expr = nestedInput.toAig();
    } else {
      AigExpr inputGate = input.toAig();
      expr = factory.notGate(inputGate);
    }
    return expr;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.aig.GateBuilder#inputs(org.modelevolution.aig.GateBuilder
   * [])
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
    return -input.label();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.aig.builders.AigBuilder#toString()
   */
  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("!");
    // sb.append(input.label());
    if (input == null)
      return sb.append("(??)").toString();
    if (input instanceof LatchBuilder)
      sb.append(input.label());
    else
      sb.append(input.toString());
    return sb.toString();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.aig.builders.AigBuilder#not()
   */
  @Override
  public AigBuilder not() {
    return input;
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
  // return Collections.singletonList(input);
  // }
}
