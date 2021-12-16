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
package org.modelevolution.aig;

import java.util.Collection;
import java.util.Iterator;

final class AndAssembler extends GateAssembler {
  AndAssembler(AigFactory factory, final int size) {
    super(factory, size);
  }

  AndAssembler(final AigFactory factory, final Collection<AigExpr> components) {
    super(factory, components);
  }

  AndAssembler(final AigFactory factory, final AigExpr... components) {
    super(factory, components);
  }

  /**
   * @return
   */
  @Override
  protected AigExpr identity() {
    return AigExpr.TRUE;
  }
  
  @Override
  protected AigExpr assemble() {
    /* no element was added --or-- all added elements where equiv to TRUE */
    if (components.isEmpty()) return identity();
    /* FALSE was added, so the entire expression becomes FALSE */
    if (isShortCircuit()) return AigExpr.FALSE;
    // since components.isEmpty()==false, there are elements in
    // this.components
    if (components.size() < 2)
      return components.peek();
    
    final Iterator<AigExpr> it = components.iterator();
    assert it.hasNext();
    AigExpr andExpr = it.next();
    while (it.hasNext()) {
      AigExpr expr = it.next();
      andExpr = factory.andGate(andExpr, expr);
    }
    assert andExpr != null;
    return andExpr;
  }

  /* (non-Javadoc)
   * @see org.modelevolution.aig.GateAssembler#isShortCircuit()
   */
  @Override
  boolean isShortCircuit() {
    return (/* components.peek() != null && */
        components.peek() == AbstractAigExpr.FALSE ? true : false);
  }
}