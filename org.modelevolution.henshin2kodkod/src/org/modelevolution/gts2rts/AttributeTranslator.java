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

import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.LeafExpression;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.henshin.model.Attribute;
import org.modelevolution.emf2rel.Enums;
import org.modelevolution.emf2rel.Signature;
import org.modelevolution.emf2rel.StateRelation;
import org.modelevolution.gts2rts.attrexpr.AttrExprParser;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public final class AttributeTranslator {
  private final Signature sig;
  private final NodeCache<?> nodeCache;
  private final ParamDataset params;
  private final Enums enums;

  /**
   * 
   */
  public AttributeTranslator(final Signature sig, final NodeCache<?> nodeCache,
      final ParamDataset params, final Enums enums) {
    this.sig = sig;
    this.nodeCache = nodeCache;
    this.params = params;
    this.enums = enums;
  }

  public StateRelation attrState(final Attribute attr) {
    return sig.state(attr.getNode().getType(), attr.getType());
  }

  /**
   * @param attr
   * @return
   */
  public Expression translateToExpr(final Attribute attr) {
    final EDataType attributeType = attr.getType().getEAttributeType();
    final AttrExprParser parser = new AttrExprParser(attr.getGraph().getRule(), attributeType,
                                                     params, enums);
    final Expression attrExpr = parser.parse(attr.getValue());
    return attrExpr;
  }

  public Formula translateToFormula(final Attribute attr) {
    final LeafExpression src = nodeCache.get(attr.getNode()).variable();
    final StateRelation attrState = attrState(attr);
    /*
     * Parse the string and generate a formula that the value of the attribute
     * is required to satisfied, ie, to match.
     */
    final Expression attrExpr = translateToExpr(attr);
    final Formula attrCondition = src.join(attrState.preState()).eq(attrExpr);
    return attrCondition;
  }

  public Formula translateToFormula(final Attribute attr, final Expression attrExpr) {
    final LeafExpression src = nodeCache.get(attr.getNode()).variable();
    final StateRelation attrState = attrState(attr);
    /*
     * Take the parsed string and generate a formula that the value of the
     * attribute is required to satisfied, ie, to match.
     */
    final Formula attrCondition = src.join(attrState.preState()).eq(attrExpr);
    return attrCondition;
  }
}
