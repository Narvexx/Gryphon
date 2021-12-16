/*
 * henshin2kodkod -- Copyright (c) 2015-present, Sebastian Gabmeyer
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
package org.modelevolution.rts;

import java.util.Collection;
import java.util.Collections;

import kodkod.ast.Formula;
import kodkod.ast.LeafExpression;
import kodkod.ast.Relation;

/**
 * @author Sebastian Gabmeyer
 * 
 */
abstract class AbstractReachabilityProperty implements Property {

  protected final Collection<Formula> decls;

  public abstract Formula formula();

  protected final Collection<LeafExpression> vars;

  protected AbstractReachabilityProperty(Collection<LeafExpression> variables,
      Collection<Formula> decls) {
    vars = variables;
    this.decls = decls;
  }

  @Override
  public final Collection<LeafExpression> variables() {
    return Collections.unmodifiableCollection(vars);
  }

  @Override
  public Collection<Formula> decls() {
    return Collections.unmodifiableCollection(decls);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.verification.Property#declarations()
   */
  @Override
  public Formula declarations() {
    return Formula.and(decls);
  }

  public abstract String toString();

}