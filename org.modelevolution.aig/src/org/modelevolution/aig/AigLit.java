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
public final class AigLit extends AbstractAigExpr {
  private final int var;

  // AigLit(final int lit) {
  // this(lit >> 1, isOdd(lit));
  // }

  AigLit(final int var) {
    this.var = var;
  }

  // public static AigLit create(final int lit) {
  // return new AigLit(lit >> 1, isEven(lit));
  // }
  //
  // public static AigLit create(final int var, final boolean neg) {
  // return new AigLit(var, neg);
  // }

  // /**
  // * @param lit
  // * @return
  // */
  // private static boolean isOdd(int lit) {
  // return lit % 2 == 1 ? true : false;
  // }

  /*
   * (non-Javadoc)
   * 
   * @see kodkodmod.engine.fol2aig.AigExpr#var()
   */
  @Override
  public int var() {
    return var;
  }

  /*
   * (non-Javadoc)
   * 
   * @see kodkodmod.engine.fol2aig.AigExpr#toString()
   */
  @Override
  public String toString() {
    return String.valueOf(literal());
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

  @Override
  public AigExpr input(final int i) {
    return null;
  }

  /* (non-Javadoc)
   * @see org.modelevolution.aig.AigExpr#accept(org.modelevolution.aig.AigVisitor, java.lang.Object)
   */
  @Override
  public <T, A> T accept(AigVisitor<T, A> visitor, A arg) {
    return visitor.visit(this, arg);
  }
}
