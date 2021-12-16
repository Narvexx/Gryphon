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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.modelevolution.aig;

/**
 * @author Sebastian Gabmeyer
 *
 */
public final class AigAnd extends AbstractAigExpr {
	private final AigLit lhs;
	private final AigExpr rhs1, rhs2;

	AigAnd(AigLit lhs, AigExpr expr1, AigExpr expr2) {
		this.lhs = lhs;
		this.rhs1 = expr1;
		this.rhs2 = expr2;
	}

	/* (non-Javadoc)
	 * @see kodkodmod.engine.fol2aig.AigExpr#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(lhs.toString());
		sb.append(" ");
		sb.append(rhs1.literal());
		sb.append(" ");
		sb.append(rhs2.literal());
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see kodkodmod.engine.fol2aig.AigExpr#var()
	 */
	@Override
	public int var() {
		return lhs.var();
	}

  /* (non-Javadoc)
   * @see kodkodmod.engine.fol2aig.AigExpr#negated()
   */
  @Override
  public boolean isNegated() {
    return false;
  }
  
  public AigExpr input(final int i) {
    if(i == 0) return rhs1;
    if(i == 1) return rhs2;
    throw new IllegalArgumentException();
  }

  /* (non-Javadoc)
   * @see org.modelevolution.aig.AigExpr#accept(org.modelevolution.aig.AigVisitor, java.lang.Object)
   */
  @Override
  public <T, A> T accept(AigVisitor<T, A> visitor, A arg) {
    return visitor.visit(this, arg);
  }
}
