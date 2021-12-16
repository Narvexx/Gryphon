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

import static org.modelevolution.gts2rts.util.NodeUtil.isCreated;
import static org.modelevolution.gts2rts.util.NodeUtil.loadActionNode;
import static org.modelevolution.gts2rts.util.NodeUtil.tuple;
import kodkod.ast.Expression;
import kodkod.ast.LeafExpression;

import org.eclipse.emf.henshin.model.Attribute;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.MappingList;
import org.eclipse.emf.henshin.model.Node;
import org.modelevolution.emf2rel.Enums;
import org.modelevolution.emf2rel.Signature;
import org.modelevolution.emf2rel.StateRelation;
import org.modelevolution.gts2rts.attrexpr.AttrExprParser;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class RhsTranslator {
  private final Signature sig;
  private final ParamDataset params;
  private final Enums enums;
  private final NodeCache<?> nodeCache;
  private final RuleTranslation ruleTranslation;

  // private ;

  private RhsTranslator(final Signature sig, final NodeCache<?> nodeCache,
      final ParamDataset params, final RuleTranslation ruleTranslation) {
    this.sig = sig;
    this.nodeCache = nodeCache;
    this.params = params;
    this.enums = sig.enums();
    this.ruleTranslation = ruleTranslation;
  }

  static RhsTranslator create(final Signature sig, final NodeCache<?> nodeCache,
      final ParamDataset params, final RuleTranslation ruleTranslation) {
    if (sig == null)
      throw new NullPointerException();
    if (nodeCache == null)
      throw new NullPointerException();
    if (params == null)
      throw new NullPointerException();
    if (ruleTranslation == null)
      throw new NullPointerException();
    return new RhsTranslator(sig, nodeCache, params, ruleTranslation);
  }

  // static final class RhsTranslation {
  // private final Collection<Formula> effects;
  //
  // /**
  // *
  // */
  // public RhsTranslation(Collection<Formula> effects) {
  // this.effects = effects;
  // }
  //
  // Collection<Formula> effects() {
  // return effects;
  // }
  // }

  /**
   * @param rhs
   *          TODO
   * @param effects
   *          TODO
   * 
   */
  void translate(final Graph rhs) {
    final EffectCollector effects = ruleTranslation.effectCollector();
    /*
     * In case that the RHS graph contains multiple nodes of the same type or
     * the RHS graph's rule contains multi-rules, we need to ensure that nodes
     * of the same type are not mapped to equivalent Boolean variables.
     */
    final InjectivityConditionBuilder injBuilder = ruleTranslation.injectivityBuilder();
    final MappingList mappings = rhs.getRule().getMappings();
    /*
     * Note: we are only interested in created graph elements, but iterating
     * through the RHS graph's nodes also includes those nodes that are
     * preserved, so we need to check whether a node is an action node.
     */
    for (final Node node : rhs.getNodes()) {
      final NodeInfo<?> info = cache(loadActionNode(node));
      final LeafExpression src = info.variable();
      if (isCreated(node)) {
        injBuilder.addInfo(info);
        effects.addCreateMod(sig.state(node.getType()), src);
      }
      for (final Edge edge : node.getOutgoing()) {
        /*
         * Note again: also the preserved edges are mapped to the RHS and thus
         * are part of the RHS graph; hence, we skip them.
         */
        if (!isCreated(edge))
          continue;
        final StateRelation edgeRelation = sig.state(node.getType(), edge.getType());
        final LeafExpression tgt = cache(loadActionNode(edge.getTarget())).variable();
        effects.addCreateMod(edgeRelation, tuple(src, tgt));
      }

      /*
       * If an attribute value is only set in the RHS of the graph production,
       * it is tagged as <<create>>; since we do not know the attribute's
       * current value we need to override it. If, however, the node is newly
       * created, we simple add the new (src, attrExpr)-tuple to the src2attr
       * relation.
       */
      if (!info.isPreservedInfo()) {
        for (final Attribute attr : node.getAttributes()) {
          final StateRelation src2attr = sig.state(node.getType(), attr.getType());
          if (mappings.getOrigin(attr) != null
              && mappings.getOrigin(attr).getValue().equals(attr.getValue()))
            continue;
          final AttrExprParser parser = new AttrExprParser(info.node().getGraph().getRule(),
                                                           attr.getType().getEAttributeType(),
                                                           params, enums);
          final Expression attrExpr = parser.parse(attr.getValue());

          if (info.isCreatedInfo())
            effects.addCreateMod(src2attr, tuple(src, attrExpr));
          else
            effects.addOverrideMod(src2attr, tuple(src, attrExpr));
        }
      }
    }
  }

  // /**
  // * @param actionNode
  // * @return
  // */
  // private LeafExpression cache(Node node) {
  // return nodeCache.cache(node).var();
  // }

  private NodeInfo<?> cache(Node node) {
    return nodeCache.cache(node);
  }

  // /**
  // * @param ruleFormula
  // */
  // void setRuleTranslation(final RuleTranslation translation) {
  // if (translation == null)
  // throw new NullPointerException();
  // this.ruleTranslation = translation;
  // }
}
