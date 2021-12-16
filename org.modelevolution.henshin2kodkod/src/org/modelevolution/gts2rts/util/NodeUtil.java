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
package org.modelevolution.gts2rts.util;

import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.LeafExpression;
import kodkod.ast.Relation;
import kodkod.ast.Variable;

import org.eclipse.emf.henshin.model.Action;
import org.eclipse.emf.henshin.model.Action.Type;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.GraphElement;
import org.eclipse.emf.henshin.model.Node;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public final class NodeUtil {
  /**
   * @param elem
   * @return true if the RHS graph element is preserved
   */
  public static boolean isPreserved(final GraphElement elem) {
    if (elem == null)
      throw new NullPointerException("elem == null.");
    // assert elem.getGraph().isLhs();
    if (!isActionElement(elem))
      return false;
    return elem.getAction().getType() == Type.PRESERVE;
    // if (elem.getGraph().isRhs()) throw new AssertionError();
    // // There are only CREATE-action nodes in the RHS
    // assert !isActioned(elem) || elem.getAction().getType() == Type.CREATE;
    // if (elem.getAction() != null) return false;
    // // this is implied by the first assertion: rhsElem.getGraph().isRhs()
    // assert elem.getGraph().eContainer() instanceof Rule;
    // // if (!(rhsElem.getGraph().eContainer() instanceof Rule)) return false;
    // Rule rule = (Rule) elem.getGraph().eContainer();
    // GraphElement origin = rule.getMappings().getOrigin(elem);
    // // this should never happen if Henshin "behaves" correctly
    // if (!isActioned(origin)) throw new AssertionError();
    // return origin.getAction().getType() == Type.PRESERVE;
  }

  /**
   * This function requires that the tested {@link GraphElement graph element}
   * is an action node or belongs to the RHS of the rule.
   * 
   * @param elem
   * @return
   * @throws IllegalArgumentException
   *           if
   *           <code>elem.getAction() == null && !elem.getGraph().isRhs()</code>
   *           , i.e., if the given graph element is not an action node and does
   *           not belong to the rule's RHS
   */
  public static boolean isCreated(final GraphElement elem) {
    if (elem == null)
      throw new NullPointerException("elem == null.");
    // if (!elem.getGraph().isRhs() && !isActionNode(elem))
    // throw new IllegalArgumentException();
    if (!isActionElement(elem))
      return false;
    return elem.getAction().getType() == Type.CREATE;
  }

  /**
   * @param elem
   * @return
   * @throws IllegalArgumentException
   *           if
   *           <code>elem.getAction() == null && !elem.getGraph().isLhs()</code>
   *           , i.e., if the given graph element is not an action node and does
   *           not belong to the rule's LHS
   */
  public static boolean isDeleted(final GraphElement elem) {
    if (elem == null)
      throw new NullPointerException("elem == null.");
    // if (!elem.getGraph().isLhs() && !isActionNode(elem))
    // return false;
    if (!isActionElement(elem))
      return false;
    return elem.getAction().getType() == Type.DELETE;
  }

  /**
   * @param elem
   * @return
   * @throws IllegalArgumentException
   *           if
   *           <code>elem.getAction() == null && !elem.getGraph().isNestedCondition()</code>
   *           , i.e., if the given graph element is not an action node and does
   *           not belong to the rule's NAC
   */
  public static boolean isForbidden(final GraphElement elem) {
    if (elem == null)
      throw new NullPointerException("elem == null.");
    // if (!elem.getGraph().isNestedCondition() && !isActionNode(elem))
    // throw new IllegalArgumentException();
    if (!isActionElement(elem))
      return false;
    return elem.getAction().getType() == Type.FORBID;
  }

  /**
   * @param elem
   * @return
   * @throws IllegalArgumentException
   *           if
   *           <code>elem.getAction() == null && !elem.getGraph().isNestedCondition()</code>
   *           , i.e., if the given graph element is not an action node and does
   *           not belong to the rule's PAC
   */
  public static boolean isRequired(final GraphElement elem) {
    if (elem == null)
      throw new NullPointerException("elem == null.");
    // if (!elem.getGraph().isNestedCondition() && !isActionNode(elem))
    // throw new IllegalArgumentException();
    if (!isActionElement(elem))
      return false;
    return elem.getAction().getType() == Type.REQUIRE;
  }

  /**
   * Tests whether the given {@link GraphElement graph element} is a
   * {@link Action#isMulti() multi-element}. This method reuqires that the
   * tested {@link GraphElement element} <code>elem</code> is an
   * {@link Node#getAction() action node}.
   * 
   * @param elem
   * @return <code>true</code> if the given graph element is a multi-element.
   * @throws IllegalArgumentException
   *           if elem.getAction() == null, i.e., if the given graph element is
   *           not an action node.
   */
  public static boolean isMulti(final GraphElement elem) {
    if (elem == null)
      throw new NullPointerException("elem == null.");
    if (!isActionElement(elem)) // throw new IllegalArgumentException();
      return false;
    return elem.getAction().isMulti();
  }

  /**
   * @param elem
   * @param action
   * @return <code>true</code> if the given {@link GraphElement graph element}
   *         is an action node of the given {@link Type action type}.
   */
  public static boolean isActionElement(final GraphElement elem, final Type action) {
    if (elem == null)
      throw new NullPointerException("elem == null.");
    if (!isActionElement(elem))
      return false;
    return elem.getAction().getType() == action;
  }

  /**
   * Tests whether the given {@link GraphElement element} is an {@link Action
   * action node}.
   * 
   * @param elem
   * @return <code>true</code> if the given element is an action node.
   */
  public static boolean isActionElement(final GraphElement elem) {
    if (elem == null)
      throw new NullPointerException("elem == null.");
    return elem.getAction() != null;
  }

  /**
   * Creates a condition that checks whether there exists an edge between source
   * <code>src</code> and target <code>tgt</code>. More specifically, the
   * returned condition checks asserts that there exists a tuple
   * <code>(src, tgt)</code> in the <code>src2tgt</code> {@link Relation
   * relation}.
   * 
   * @param src
   * @param tgt
   * @param src2tgt
   * @return
   */
  public static Formula edgeMatch(final LeafExpression src, final LeafExpression tgt,
      final Relation src2tgt) {
    if (src == null)
      throw new NullPointerException("src == null.");
    if (tgt == null)
      throw new NullPointerException("tgt == null.");
    if (src2tgt == null)
      throw new NullPointerException("src2tgt == null.");
    final Expression tuple = tuple(src, tgt);
    return tuple.in(src2tgt);
  }

  /**
   * @param src
   * @param tgt
   * @return
   */
  public static Expression tuple(final LeafExpression src, final Expression tgt) {
    if (src == null)
      throw new NullPointerException("src == null.");
    if (tgt == null)
      throw new NullPointerException("tgt == null.");
    return src.product(tgt);
  }

  /**
   * @param node
   * @return
   */
  public static Node loadActionNode(final Node node) {
    if (node == null)
      throw new NullPointerException("node == null.");
    return isActionElement(node) ? node : node.getActionNode();
  }
  
}
