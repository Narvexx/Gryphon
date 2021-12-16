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
package org.modelevolution.emf2rel;

import kodkod.ast.Relation;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class StateRelation {
  private final Relation pre;
  private final Relation post;
  private boolean isModified;

  /**
   * 
   */
  private StateRelation(final Relation pre, final Relation post) {
    assert pre.arity() == post.arity();
    this.pre = pre;
    this.post = post;
    setModified(false);
  }

  public Relation preState() {
    return this.pre;
  }

  public Relation postState() {
    return this.post;
  }

  /**
   * This function is equivalent to {@link #preState()} but should be used for
   * {@link StateRelation pre/post relations} that are static, i.e., pre and
   * post are equivalent.
   * 
   * @return
   */
  public Relation relation() {
    return preState();
  }

  /**
   * @param name
   * @return
   */
  public static StateRelation create(final String name, final int arity) {
    final Relation pre = Relation.nary(name, arity);
    final Relation post = Relation.nary(name + "'", arity);
    return new StateRelation(pre, post);
  }

  static StateRelation createStatic(final String name, final int arity) {
    final Relation r = Relation.nary(name, arity);
    return new StateRelation(r, r);
  }

  /**
   * @return
   */
  public String name() {
    return pre.name();
  }

  /**
   * @return
   */
  public int arity() {
    return pre.arity();
  }

  public boolean isStatic() {
    return pre == post;
  }

  /**
   * @return the isModified
   */
  public boolean isModified() {
    return isModified;
  }

  /**
   * @param isModified the isModified to set
   */
  public void setModified(boolean isModified) {
    this.isModified = isModified;
  }

  /**
   * @return
   */
  public boolean isNode() {
    return arity() == 1;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer(pre.name());
    sb.append("/");
    sb.append(pre.arity());
    return sb.toString();
  }
}