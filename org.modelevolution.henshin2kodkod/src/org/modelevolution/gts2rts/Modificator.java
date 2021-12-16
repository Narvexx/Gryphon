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
package org.modelevolution.gts2rts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import kodkod.ast.Expression;
import kodkod.ast.Relation;

import org.modelevolution.emf2rel.StateRelation;

/**
 * Stores the set of create and delete modifications, i.e., the effects,
 * performed by a rule on a {@link StateRelation stateful relation}.
 * 
 * @author Sebastian Gabmeyer
 * 
 */
final class Modificator {
  private final StateRelation prePost;
  private final Collection<Expression> createExprs;
  private final Collection<Expression> deleteExprs;
  private final Collection<Expression> overrideExprs;

  /**
   * @param prePost
   *          TODO
   */
  Modificator(final StateRelation prePost) {
    this.prePost = prePost;
    this.createExprs = new ArrayList<>();
    this.deleteExprs = new ArrayList<>();
    this.overrideExprs = new ArrayList<>();
  }

  StateRelation state() {
    return this.prePost;
  }

  Relation pre() {
    return this.prePost.preState();
  }

  Relation post() {
    return this.prePost.postState();
  }

  Collection<Expression> createExprs() {
    return this.createExprs;
  }

  Collection<Expression> deleteExprs() {
    return this.deleteExprs;
  }

  boolean addCreateExpr(Expression expr) {
    return createExprs.add(expr);
  }

  boolean addDeleteExpr(Expression expr) {
    return deleteExprs.add(expr);
  }

  /**
   * @param tuple
   */
  boolean addOverrideExpr(Expression expr) {
    return overrideExprs.add(expr);
  }

  /**
   * @return
   */
  public Collection<Expression> overrideExprs() {
    return overrideExprs;
  }
}