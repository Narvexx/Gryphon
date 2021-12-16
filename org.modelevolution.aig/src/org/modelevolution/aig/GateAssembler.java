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

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public abstract class GateAssembler {

  protected final Deque<AigExpr> components;

  protected abstract AigExpr identity();
  
  protected abstract AigExpr assemble();

  protected final AigFactory factory;

  /**
   * @param size
   * @param factory
   * 
   */
  protected GateAssembler(AigFactory factory, int size) {
    this.factory = factory;
    components = new ArrayDeque<>(size);
  }

  /**
   * @param factory
   * @param components
   */
  protected GateAssembler(AigFactory factory, Collection<AigExpr> components) {
    this(factory, components.size());
    addAll(components);
  }

  /**
   * @param factory
   * @param components
   */
  protected GateAssembler(AigFactory factory, AigExpr... components) {
    this(factory, components.length);
    addAll(components);
  }

  abstract boolean isShortCircuit();

  boolean add(final AigExpr component) {
    /*
     * adding NULL is treated as identity element and TRUE is AND's identity;
     * thus, they are not added to the list of components
     */
    if (isIdentity(component) || isShortCircuit()) return false;
    return components.offerFirst(component);
  }

  protected boolean addAll(final AigExpr... components) {
    boolean changed = false;
    for (AigExpr c : components) {
      if (isShortCircuit()) break;
      if (isIdentity(c))
        changed |= false;
      else
        changed |= this.components.offerFirst(c);
    }
    return changed;
  }

  /**
   * @param expr
   * @return
   */
  boolean isIdentity(AigExpr expr) {
    return expr == null || expr == identity();
  }

  protected boolean addAll(final Collection<AigExpr> components) {
    boolean changed = false;
    for (AigExpr c : components) {
      if (isShortCircuit()) break;
      if (isIdentity(c))
        changed |= false;
      else
        changed |= this.components.offerFirst(c);
    }
    return changed;
  }
}