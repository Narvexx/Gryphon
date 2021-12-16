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

import org.modelevolution.aig.AigExpr;

/**
 * @author Sebastian Gabmeyer
 *
 */
public interface AigBuilder {

  public static final AigBuilder TRUE = new AbstractLiteralBuilder() {
      @Override
      public AigExpr toAig() {
        return AigExpr.TRUE;
      }
  
      @Override
      public int label() {
        return Integer.MAX_VALUE;
      }
  
      @Override
      public AigBuilder not() {
        return FALSE;
      }
  
      @Override
      public String toString() {
        return "T";
      }
      // @Override
      // public Collection<AigBuilder> inputs() {
      // throw new UnsupportedOperationException(); // TODO: Implement this!
      // }
    };
  public static final AigBuilder FALSE = new AbstractLiteralBuilder() {
      @Override
      public AigExpr toAig() {
        return AigExpr.FALSE;
      }
  
      @Override
      public int label() {
        return -Integer.MAX_VALUE;
      }
  
      @Override
      public AigBuilder not() {
        return TRUE;
      }
  
      @Override
      public String toString() {
        return "F";
      }
    };

  public abstract AigExpr toAig();

  public abstract void inputs(AigBuilder... builders);

  public abstract void inputs(Collection<? extends AigBuilder> builders);

  public abstract int label();

  /**
   * @return
   */
  public abstract AigBuilder not();

  /**
   * @param aigBuilderVisitor
   */
  public abstract void accept(AigBuilderVisitor visitor);

  public abstract Collection<AigBuilder> inputs();

}