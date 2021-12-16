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

import kodkod.ast.LeafExpression;
import kodkod.ast.Relation;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.henshin.model.Node;
import org.modelevolution.gts2rts.util.NodeUtil;

/**
 * @author Sebastian Gabmeyer
 * @param <D>
 * 
 */
public abstract class NodeInfo<D> {

  private final Node node;
  private final LeafExpression var;
  private final Relation binding;

  // TODO: use StateRelation in NodeInfo and add method state(); derive variable
  // 'binding' from state().preState()
  /**
   * 
   */
  protected NodeInfo(final Node node, final LeafExpression var, final Relation binding) {
    super();
    this.node = node;
    this.var = var;
    this.binding = binding;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append(var);
    if (node.getType() != null) {
      builder.append(": ");
      builder.append(node.getType().getName());
    }
    return builder.toString();
  }

  /**
   * Get the set of objects that this node represents. Note that the set of
   * objects might very well be a singleton set.
   * 
   * @return
   */
  protected Relation binding() {
    return binding;
  }

  abstract protected D decl();

  /**
   * @return
   */
  protected boolean isCreatedInfo() {
    return NodeUtil.isCreated(node);
  }

  protected boolean isDeletedInfo() {
    return NodeUtil.isDeleted(node);
  }

  protected boolean isForbiddenInfo() {
    return NodeUtil.isForbidden(node);
  }

  /**
   * Checks whether {@link Node this.node} is a mutli-node.
   * 
   * @return <code>true</code> if <code>this.node</code> is a multi node,
   *         <code>false</code> otherwise.
   * @deprecated used by deprecated class and method => delete!
   */
  @Deprecated
  protected boolean isMultiInfo() {
    return node.getAction().isMulti();
  }

  /**
   * @return
   */
  protected boolean isPreservedInfo() {
    return NodeUtil.isPreserved(node);
  }

  /**
   * @return
   */
  protected Node node() {
    return node;
  }

  /**
   * Get the {@link Relation relation} that defines the bindings for this
   * {@link #node()}.
   * 
   * @return
   */
  protected LeafExpression variable() {
    return var;
  }

  /**
   * @return
   */
  protected EClass type() {
    return node.getType();
  }

  // protected final Signature sig = Signature.instance();

  // protected static NodeInfo create(String name, final Node node) {
  // assert node.getAction() != null;
  // if (isForbidden(node))
  // return new NacNodeInfo(name, node);
  // else
  // return new VarNodeInfo(name, node);
  // }
}