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
package org.modelevolution.gts2rts.attrexpr;

import kodkod.ast.IntExpression;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.henshin.model.Node;
import org.modelevolution.gts2rts.ParamData;
import org.modelevolution.gts2rts.ParamDataset;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class Typechecker {
  private final ParamDataset params;

  /**
   * 
   */
  public Typechecker(final ParamDataset params) {
    this.params = params;
  }

  /**
   * Checks if the <code>node</code> has the specified <code>type</code> -OR- if
   * the node's type is unspecified, the method sets the node's type to the
   * specified type.
   * 
   * @param node
   * @param type
   * @throws TypeMatchException
   *           if the node's type differs from the specified type:
   *           {@link TypedNode#type() <code>node.type()</code>}
   *           <code>!= type</code>
   */
  protected TypedNode typematch(final TypedNode node, final EClassifier type) {
    if (node.type() == null) { // only parameters may have an unspecified type
      assert node.value() instanceof IntExpression;
      final ParamData data = params.paramData((IntExpression) node.value());
      data.type(type);
      return new TypedNode(node.value(), type);
    }
    if (type != node.type())
      throw new TypeMatchException(type + " != " + node.type());
    return node;
  }

  /**
   * Check if the lhs's type and the rhs's type match and, if they do not match,
   * throw a TypeMatchException, or initialize either the lhs's type to the
   * rhs's type or vice versa if either the lhs's type or the rhs's type is
   * unspecified. If both the lhs's and the rhs's type are unspecified, a
   * TypeInferenceException is thrown.
   * 
   * @param lhs
   * @param rhs
   */
  protected void typecheck(final TypedNode lhs, final TypedNode rhs) {
    if (lhs.type() == null && rhs.type() == null)
      throw new TypeInferenceException();
    if (lhs.type() == null) {
      typematch(lhs, rhs.type());
    } else if (rhs.type() == null) {
      typematch(rhs, lhs.type());
    } else {
      if (lhs.type() != rhs.type()) throw new TypeMatchException();
    }
  }
}
