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

import org.modelevolution.aig.AigExpr;
import org.modelevolution.aig.AigFactory;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public abstract class AbstractAigBuilder implements AigBuilder {
  public static final int UNKNOWN_LABEL = 0;
  protected final AigFactory factory;

  /**
   * @param factory2
   */
  protected AbstractAigBuilder(AigFactory factory) {
    this.factory = factory;
  }

  /* (non-Javadoc)
   * @see org.modelevolution.aig.builders.AigBuilder#toAig()
   */
  @Override
  public AigExpr toAig() {
    if (factory.isCached(this))
      return factory.cached(this);
    else
      return factory.cache(this, callToAigOnInput());
  }

  // public abstract Collection<AigBuilder> inputs();

  /**
   * @return
   */
  protected abstract AigExpr callToAigOnInput();

  /* (non-Javadoc)
   * @see org.modelevolution.aig.builders.AigBuilder#inputs(org.modelevolution.aig.builders.AbstractAigBuilder)
   */
  @Override
  public abstract void inputs(AigBuilder... builders);

  /* (non-Javadoc)
   * @see org.modelevolution.aig.builders.AigBuilder#inputs(java.util.Collection)
   */
  @Override
  public abstract void inputs(Collection<? extends AigBuilder> builders);

  /* (non-Javadoc)
   * @see org.modelevolution.aig.builders.AigBuilder#label()
   */
  @Override
  public abstract int label();

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return String.valueOf(label());
  }

  /* (non-Javadoc)
   * @see org.modelevolution.aig.builders.AigBuilder#accept(org.modelevolution.aig.builders.AigBuilderVisitor)
   */
  @Override
  public abstract void accept(final AigBuilderVisitor visitor);
  
  /* (non-Javadoc)
   * @see org.modelevolution.aig.builders.AigBuilder#inputs()
   */
  @Override
  public abstract Collection<AigBuilder> inputs();

  @Override
  public AigBuilder not() {
    return new NotBuilder(factory, this);
  }
}
