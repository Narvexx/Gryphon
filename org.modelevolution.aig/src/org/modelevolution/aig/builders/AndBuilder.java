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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.modelevolution.aig.AigExpr;
import org.modelevolution.aig.AigFactory;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public final class AndBuilder extends AbstractAigBuilder {
  private Collection<AigBuilder> inputs;
  private final int label;

  /**
   * @param factory
   * @param label
   */
  AndBuilder(AigFactory factory, int label) {
    super(factory);
    assert factory != null;
    this.label = label;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.aig.builders.AigBuilder#toAig()
   */
  @Override
  public AigExpr toAig() {
    if (inputs == null)
      throw new IllegalStateException("this.inputs == null.");

    return super.toAig();
  }

  /**
   * @return
   */
  protected AigExpr callToAigOnInput() {
    final Collection<AigExpr> inputGates = new ArrayList<>(inputs.size());
    for (AigBuilder input : inputs) {
      final AigExpr inputExpr = input.toAig();
      assert inputExpr != null;
      inputGates.add(inputExpr);
    }
    final AigExpr andGate = factory.andGate(inputGates);
    return andGate;
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

    addInputs(Arrays.asList(builders));
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

    addInputs(builders);
  }

  /**
   * @param builders
   */
  void addInputs(Collection<? extends AigBuilder> builders) {
    inputs = new ArrayList<>(builders.size());
    for (final AigBuilder builder : builders) {
      if (builder == AigBuilder.FALSE) {
        inputs = Collections.singleton(AigBuilder.FALSE);
        return;
      } else if (builder != null && builder != AigBuilder.TRUE)
        inputs.add(builder);
    }
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

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.aig.builders.AigBuilder#toString()
   */
  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer();
    // sb.append(label());
    if (inputs == null || inputs.isEmpty())
      return sb.append("(??&??)").toString();
    sb.append("(");
    appendAll(sb, inputs);
    sb.append(")");
    return sb.toString();
  }

  private void appendAll(final StringBuffer sb, Collection<AigBuilder> builders) {
    int i = 0;
    final Iterator<AigBuilder> it = builders.iterator();
    while (it.hasNext()) {
      final AigBuilder input = it.next();
      if (input instanceof LatchBuilder)
        sb.append(input.label());
      else
        sb.append(input);
      if (i++ < builders.size() - 1)
        sb.append("&");
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
    if (inputs == null)
      return Collections.emptyList();
    else
      return Collections.unmodifiableCollection(inputs);
  }

  // /*
  // * (non-Javadoc)
  // *
  // * @see org.modelevolution.aig.builders.AigBuilder#inputs()
  // */
  // @Override
  // public Collection<AigBuilder> inputs() {
  // if (inputs == null || inputs.isEmpty())
  // return Collections.emptyList();
  // return Collections.unmodifiableCollection(inputs);
  // }
}
