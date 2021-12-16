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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.modelevolution.rts;

import java.util.Collection;

import kodkod.ast.Formula;
import kodkod.ast.LeafExpression;

import org.modelevolution.aig.builders.Activator;

/**
 * @author Sebastian Gabmeyer
 *
 */
public class Loop implements StateChanger {

  /* (non-Javadoc)
   * @see org.modelevolution.rts.StateChanger#variables()
   */
  @Override
  public Collection<LeafExpression> variables() {
    throw new UnsupportedOperationException(); // TODO: Implement this!
  }

  /* (non-Javadoc)
   * @see org.modelevolution.rts.StateChanger#loops()
   */
  @Override
  public Collection<Loop> loops() {
    throw new UnsupportedOperationException(); // TODO: Implement this!
  }

  /* (non-Javadoc)
   * @see org.modelevolution.rts.StateChanger#name()
   */
  @Override
  public String name() {
    throw new UnsupportedOperationException(); // TODO: Implement this!
  }

  /* (non-Javadoc)
   * @see org.modelevolution.rts.StateChanger#premises()
   */
  @Override
  public Collection<Formula> premises() {
    throw new UnsupportedOperationException(); // TODO: Implement this!
  }

  /* (non-Javadoc)
   * @see org.modelevolution.rts.StateChanger#consequents()
   */
  @Override
  public Collection<Formula> consequents() {
    throw new UnsupportedOperationException(); // TODO: Implement this!
  }

  /* (non-Javadoc)
   * @see org.modelevolution.rts.StateChanger#constraints()
   */
  @Override
  public Collection<Formula> constraints() {
    throw new UnsupportedOperationException(); // TODO: Implement this!
  }

  /* (non-Javadoc)
   * @see org.modelevolution.rts.StateChanger#declarations()
   */
  @Override
  public Collection<Formula> declarations() {
    throw new UnsupportedOperationException(); // TODO: Implement this!
  }

  /* (non-Javadoc)
   * @see org.modelevolution.rts.StateChanger#injectivities()
   */
  @Override
  public Collection<Formula> injectivities() {
    throw new UnsupportedOperationException(); // TODO: Implement this!
  }

  /* (non-Javadoc)
   * @see org.modelevolution.rts.StateChanger#danglingEdgeConditions()
   */
  @Override
  public Collection<Formula> danglingEdgeConditions() {
    throw new UnsupportedOperationException(); // TODO: Implement this!
  }

  /* (non-Javadoc)
   * @see org.modelevolution.rts.StateChanger#hasLoops()
   */
  @Override
  public boolean hasLoops() {
    throw new UnsupportedOperationException(); // TODO: Implement this!
  }

  /* (non-Javadoc)
   * @see org.modelevolution.rts.StateChanger#getActivator()
   */
  @Override
  public Activator<? extends StateChanger> getActivator() {
    throw new UnsupportedOperationException(); // TODO: Implement this!
  }

}
