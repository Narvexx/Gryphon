/*
 * henshin2kodkod -- Copyright (c) 2014-present, Sebastian Gabmeyer
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
package org.modelevolution.gts2rts;

import kodkod.ast.Formula;
import kodkod.ast.Relation;

import org.eclipse.emf.henshin.model.Node;

final class VarNodeInfo extends NodeInfo<Formula> {
  // private final Collection<Formula> matches;
  // private final Collection<Formula> effects;

  /**
   * @param name
   * @param binding
   * 
   */
  VarNodeInfo(String name, final Node node, Relation binding) {
    super(node, Relation.unary(name), binding);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.henshin2kodkod.AbstractNodeInfo#decl()
   */
  @Override
  protected Formula decl() {
    assert !isForbiddenInfo();
    return variable().one();
  }

  // /*
  // * (non-Javadoc)
  // *
  // * @see org.modelevolution.henshin2kodkod.NodeInfo#self()
  // */
  // @Override
  // protected Expression bindings() {
  // return variable();
  // }

  // boolean addMatchCondition(Formula match) {
  // return matches.add(match);
  // }

  // /*
  // * (non-Javadoc)
  // *
  // * @see
  // * org.modelevolution.henshin2kodkod.AbstractNodeInfo#add(kodkod.ast.Node)
  // */
  // @Override
  // protected boolean addMatch(kodkod.ast.Node condition) {
  // assert condition instanceof Formula;
  // return matches.add((Formula) condition);
  // }

  // /*
  // * (non-Javadoc)
  // *
  // * @see org.modelevolution.henshin2kodkod.AbstractNodeInfo#matchCondition()
  // */
  // @Override
  // protected Formula matchCondition() {
  // // final Formula condition;
  // // // if (isUnconstrainedInfo())
  // // // condition = var().some();
  // // // else
  // // condition = Formula.and(matches);
  // //
  // // return (isForbiddenMultiInfo() ?
  // // multiNodeMatchExists().implies(condition)
  // // : condition);
  // return Formula.and(matches);
  // }

  // /**
  // * @return
  // */
  // private Formula multiNodeMatchExists() {
  // final NodeCache nodeCache = NodeCache.instance();
  // return nodeCache.containsMultiNode() ? nodeCache.multiNode().expression()
  // .eq(Expression.NONE).not()
  // : Formula.FALSE;
  // }

  // /*
  // * (non-Javadoc)
  // *
  // * @see
  // * org.modelevolution.henshin2kodkod.AbstractNodeInfo#addEffect(kodkod.ast
  // * .Node)
  // */
  // @Override
  // protected boolean addEffect(Formula condition) {
  // assert !isForbiddenInfo();
  // return effects.add(condition);
  // }

  // /*
  // * (non-Javadoc)
  // *
  // * @see org.modelevolution.henshin2kodkod.AbstractNodeInfo#effectCondition()
  // */
  // @Override
  // protected Formula effectCondition() {
  // // assert !isForbidden();
  // return Formula.and(effects);
  // }
}