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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.modelevolution.gts2rts;

import java.util.Collection;

import kodkod.ast.Formula;
import kodkod.ast.LeafExpression;
import kodkod.ast.Relation;

import org.eclipse.emf.henshin.model.Rule;
import org.modelevolution.emf2rel.Signature;
import org.modelevolution.rts.Property;
import org.modelevolution.rts.PropertyFactory;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class PropertyTranslator {

  private final Signature sig;

  public Property translate(final Rule property) {
    final int expectedSize = property.getActionNodes(null).size();
    final NodeCache<VarNodeInfo> nodeCache = new VarNodeCache(sig, expectedSize);
    final ParamDataset params = ParamDataset.create(property.getParameters());

    final LhsTranslator lhsTranslator = new LhsTranslator(property, sig, nodeCache, params);
    final RuleTranslation propertyTranslation = lhsTranslator.translate(property.getLhs());

    final AttributeConditionTranslator acTranslator;
    acTranslator = new AttributeConditionTranslator(property, params, sig.enums());
    final Collection<Formula> attrConditions = acTranslator.translate(property.getAttributeConditions());
    propertyTranslation.addPremise(Formula.and(attrConditions));

    // final Collection<Formula> injectivity =
    // InjectivityConditionBuilder.create(property)
    // .addAll(propTranslation.infos())
    // .conditions();

    sig.bindParams(params);

    final Collection<LeafExpression> variables = nodeCache.variables();
    for (final ParamData param : params) {
      if (param.isUsedByRule(property))
        variables.add(param.var());
    }

    return PropertyFactory.create(property.getName(), variables, propertyTranslation);
  }

  public PropertyTranslator(final Signature sig) {
    this.sig = sig;
  }
}
