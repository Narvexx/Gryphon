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

import static org.modelevolution.gts2rts.util.NodeUtil.edgeMatch;
import static org.modelevolution.gts2rts.util.NodeUtil.isForbidden;
import static org.modelevolution.gts2rts.util.NodeUtil.loadActionNode;

import java.util.ArrayList;
import java.util.List;

import kodkod.ast.Decls;
import kodkod.ast.Formula;
import kodkod.ast.LeafExpression;

import org.eclipse.emf.henshin.model.Attribute;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.Node;
import org.modelevolution.emf2rel.Enums;
import org.modelevolution.emf2rel.Signature;
import org.modelevolution.gts2rts.util.NodeUtil;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public final class NacTranslator {
  private final NacNodeCache nacCache;
  private Graph nacGraph;
  private final NodeCache<?> containerNodeCache;
  private final InjectivityConditionBuilder nestedInjBuilder;
  private final Enums enums;
  private final ParamDataset params;
  private final Signature sig;

  /**
   * @param sig
   * @param nodeCache
   * @param createNested
   * @param params
   * @param enums
   */
  public NacTranslator(Signature sig, NodeCache<?> containerNodeCache,
      InjectivityConditionBuilder nestedInjBuilder, ParamDataset params, Enums enums) {
    this.sig = sig;
    this.nacCache = new NacNodeCache(sig);
    this.containerNodeCache = containerNodeCache;
    this.nestedInjBuilder = nestedInjBuilder;
    this.params = params;
    this.enums = enums;
  }

  /**
   * @param nacGraph
   * @return
   */
  public Formula translate(final Graph nacGraph) {
    this.nacGraph = nacGraph;
    final List<Formula> conditions = new ArrayList<>();
    final AttributeTranslator attrTranslator = new AttributeTranslator(sig, nacCache.readOnly(),
                                                                       params, enums);
    for (final Node node : nacGraph.getNodes()) {
      if (!isForbidden(node))
        continue;

      final NodeInfo<?> info = nacCache.cache(node);
      nestedInjBuilder.addInfo(info);

      /* Translate outgoing edges to relational logic conditions */
      for (final Edge edge : node.getOutgoing()) {
        final LeafExpression src = info.variable();
        final LeafExpression tgt = cache(loadActionNode(edge.getTarget())).variable();
        conditions.add(edgeMatch(src, tgt, sig.pre(info.type(), edge.getType())));
      }
      /* Translate incoming edges to relational logic conditions */
      for (final Edge edge : node.getIncoming()) {
        final Node srcNode = edge.getSource();
        if (isForbidden(srcNode))
          continue;
        final LeafExpression src = cache(loadActionNode(srcNode)).variable();
        final LeafExpression tgt = info.variable();
        conditions.add(edgeMatch(src, tgt, sig.pre(srcNode.getType(), edge.getType())));
      }
      /* Translate attributes to relational logic conditions */
      for (final Attribute attr : node.getAttributes()) {
        conditions.add(attrTranslator.translateToFormula(attr));
      }

      /*
       * In case no conditions have been generated for the node, add the default
       * condition.
       */
      if (node.getOutgoing().size() == 0 && node.getIncoming().size() == 0
          && node.getAttributes().size() == 0)
        conditions.add(info.variable().in(info.binding()));
    }

    final Decls decls = nacCache.decls();
    if (decls == null)
      return Formula.TRUE;
    final Formula injCondition = Formula.and(nestedInjBuilder.conditions());
    if (injCondition == Formula.TRUE)
      return Formula.and(conditions).forSome(decls).not();
    else
      return Formula.and(conditions).and(injCondition).forSome(decls).not();
  }

  // /**
  // * @param attr
  // * @return
  // */
  // private Formula translateAttribute(final Attribute attr) {
  // final LeafExpression src = nacCache.get(attr.getNode()).variable();
  // final StateRelation src2attr = sig.state(attr.getNode().getType(),
  // attr.getType());
  // /*
  // * Parse the string and generate a formula that the value of the attribute
  // * is required to satisfied, ie, to match.
  // */
  // final EDataType attributeType = attr.getType().getEAttributeType();
  // final AttrExprParser parser = new AttrExprParser(attr.getGraph().getRule(),
  // attributeType,
  // params, enums);
  // final Expression attrExpr = parser.parse(attr.getValue());
  // final Formula attrCondition = src.join(src2attr.preState()).eq(attrExpr);
  // return attrCondition;
  // }

  private NodeInfo<?> cache(Node node) {
    final NodeInfo<?> cachedInfo;
    if (node.eContainer() == nacGraph)
      cachedInfo = nacCache.cache(node);
    else
      cachedInfo = containerNodeCache.cache(node);
    assert cachedInfo != null;
    return cachedInfo;
  }

}
