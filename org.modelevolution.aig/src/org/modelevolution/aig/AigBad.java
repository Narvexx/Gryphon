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
 * Currently, merely a wrapper class of an {@link AigLit} that represents an
 * literal for a bad property in an AIGER definition.
 * 
 * @author Sebastian Gabmeyer
 * 
 */
public final class AigBad extends AbstractAigExpr {

  private final AigExpr expr;

  AigBad(AigExpr expr) {
    this.expr = expr;
  }

  /*
   * (non-Javadoc)
   * 
   * @see kodkodmod.engine.fol2aig.AigLit#var()
   */
  public int var() {
    return expr.var();
  }

  /*
   * (non-Javadoc)
   * 
   * @see kodkodmod.engine.fol2aig.AigLit#toString()
   */
  @Override
  public String toString() {
    return String.valueOf(expr.literal());
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.aig.AigExpr#isNegated()
   */
  @Override
  public boolean isNegated() {
    return expr.isNegated();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.aig.AigExpr#input(int)
   */
  @Override
  public AigExpr input(int i) {
    return expr;
  }

  /* (non-Javadoc)
   * @see org.modelevolution.aig.AigExpr#accept(org.modelevolution.aig.AigVisitor, java.lang.Object)
   */
  @Override
  public <T, A> T accept(AigVisitor<T, A> visitor, A arg) {
    return visitor.visit(this, arg);
  }
}
