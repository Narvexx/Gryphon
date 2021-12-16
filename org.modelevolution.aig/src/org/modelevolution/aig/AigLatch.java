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

import java.util.Collection;

import org.modelevolution.aig.builders.AbstractLatchBuilder;
import org.modelevolution.aig.builders.AbstractAigBuilder;
import org.modelevolution.aig.builders.AigBuilder;
import org.modelevolution.aig.builders.AigBuilderVisitor;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public final class AigLatch extends AbstractAigExpr {
  private AigLit current;
  private AigExpr nextState;
  private AigExpr initial;

  public static final class AigLatchProxy implements AigExpr, AigBuilder {
    private final AigLatch proxiedLatch;
    private final AigBuilder nextStateBuilder;
    private final int builderLabel;
    
    /**
     * 
     */
    public AigLatchProxy(AigLit currentStateLiteral, AbstractLatchBuilder builder) {
      this.builderLabel = builder.label();
      proxiedLatch = new AigLatch();
      proxiedLatch.current = currentStateLiteral;
      proxiedLatch.initial = builder.initialState();
      this.nextStateBuilder = builder.nextState();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modelevolution.aig.AigExpr#var()
     */
    @Override
    public int var() {
      return proxiedLatch.var();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modelevolution.aig.AigExpr#literal()
     */
    @Override
    public int literal() {
      return proxiedLatch.literal();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modelevolution.aig.AigExpr#isNegated()
     */
    @Override
    public boolean isNegated() {
      return proxiedLatch.isNegated();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modelevolution.aig.AigExpr#input(int)
     */
    @Override
    public AigExpr input(int i) {
      if (proxiedLatch.nextState == null)
        return toAig().input(i);

      return proxiedLatch.input(i);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.modelevolution.aig.AigExpr#accept(org.modelevolution.aig.AigVisitor,
     * java.lang.Object)
     */
    @Override
    public <T, A> T accept(AigVisitor<T, A> visitor, A arg) {
      if (proxiedLatch.nextState == null)
        throw new IllegalStateException();
      return visitor.visit(proxiedLatch, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modelevolution.aig.builders.AigBuilder#toAig()
     */
    @Override
    public AigExpr toAig() {
      if (proxiedLatch.nextState != null) {
        /* note: we've already called toAig() */
        return proxiedLatch;
      }

      proxiedLatch.nextState = nextStateBuilder.toAig();
      return proxiedLatch;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.modelevolution.aig.builders.AigBuilder#inputs(org.modelevolution.
     * aig.builders.AigBuilder[])
     */
    @Override
    public void inputs(AigBuilder... builders) {
      throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.modelevolution.aig.builders.AigBuilder#inputs(java.util.Collection)
     */
    @Override
    public void inputs(Collection<? extends AigBuilder> builders) {
      throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modelevolution.aig.builders.AigBuilder#label()
     */
    @Override
    public int label() {
      return builderLabel;
    }

    public AigExpr latch() {
      return proxiedLatch;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modelevolution.aig.builders.AigBuilder#toString()
     */
    @Override
    public String toString() {
      final StringBuffer sb = new StringBuffer();
      sb.append(label());
      sb.append("->");
      sb.append(proxiedLatch.literal());
      return sb.toString();
    }

    /**
     * Not supported by this class.
     * 
     * @see org.modelevolution.aig.builders.AigBuilder#not()
     */
    @Override
    public AigBuilder not() {
      throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.modelevolution.aig.builders.AigBuilder#accept(org.modelevolution.
     * aig.builders.AigBuilderVisitor)
     */
    @Override
    public void accept(AigBuilderVisitor visitor) {
      return;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modelevolution.aig.builders.AigBuilder#inputs()
     */
    @Override
    public Collection<AigBuilder> inputs() {
      return nextStateBuilder.inputs();
    }
  }

  private AigLatch() {
  }

  AigLatch(AigLit current, AigExpr nextState, AigExpr initialState) {
    if (current == null || nextState == null || initialState == null)
      throw new NullPointerException();
    if (initialState != AbstractAigExpr.FALSE || initialState != AbstractAigExpr.TRUE)
      throw new IllegalArgumentException();
    this.current = current;
    this.nextState = nextState;
    this.initial = initialState;
  }

  /*
   * (non-Javadoc)
   * 
   * @see kodkodmod.engine.fol2aig.AigExpr#var()
   */
  @Override
  public int var() {
    return current.var();
  }

  /*
   * (non-Javadoc)
   * 
   * @see kodkodmod.engine.fol2aig.AigExpr#toString()
   */
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer(current.toString());
    sb.append(" ");
    sb.append(nextState == null ? "??" : nextState.literal());
    sb.append(" ");
    sb.append(initial.literal());
    return sb.toString();
  }

  /*
   * (non-Javadoc)
   * 
   * @see kodkodmod.engine.fol2aig.AigExpr#negated()
   */
  @Override
  public boolean isNegated() {
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see kodkodmod.engine.fol2aig.AigExpr#input(int)
   */
  @Override
  public AigExpr input(int i) {
    if (i == 0)
      return nextState;
    throw new IllegalArgumentException();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.aig.AigExpr#accept(org.modelevolution.aig.AigVisitor,
   * java.lang.Object)
   */
  @Override
  public <T, A> T accept(AigVisitor<T, A> visitor, A arg) {
    return visitor.visit(this, arg);
  }

}
