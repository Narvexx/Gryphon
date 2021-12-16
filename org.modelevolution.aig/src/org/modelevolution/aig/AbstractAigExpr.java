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

/**
 * @author Sebastian Gabmeyer
 * 
 */
abstract class AbstractAigExpr implements AigExpr {
  // public static final AigLit FALSE = new AigLit(0);
  // public static final AigNot TRUE = new AigNot(FALSE);

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.aig.AigExpr#var()
   */
  @Override
  public abstract int var();

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.aig.AigExpr#literal()
   */
  @Override
  public int literal() {
    return var() << 1;
  }

  @Override
  public abstract String toString();

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.aig.AigExpr#isNegated()
   */
  @Override
  public abstract boolean isNegated();

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.aig.AigExpr#input(int)
   */
  @Override
  public abstract AigExpr input(final int i);

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.aig.AigExpr#accept(org.modelevolution.aig.AigVisitor, A)
   */
  @Override
  public abstract <T, A> T accept(AigVisitor<T, A> visitor, A arg);
}
