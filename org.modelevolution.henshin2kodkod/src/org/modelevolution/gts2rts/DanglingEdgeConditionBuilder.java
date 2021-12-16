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

import static org.modelevolution.gts2rts.util.NodeUtil.loadActionNode;

import java.util.ArrayList;
import java.util.Collection;

import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.Relation;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.henshin.model.Edge;
import org.modelevolution.emf2rel.Signature;

class DanglingEdgeConditionBuilder {

  /**
   * @author Sebastian Gabmeyer
   * @deprecated Use the "normal" {@link DanglingEdgeConditionBuilder} instead
   */
  private static class ExtendedDanglingEdgeConditionBuilder extends DanglingEdgeConditionBuilder {

    private final DanglingEdgeConditionBuilder base;

    // private final Collection<NodeInfo> infos;

    /**
     * @param sig
     * @param nodeCache
     */
    ExtendedDanglingEdgeConditionBuilder(Signature sig, NodeCache<?> nodeCache,
        DanglingEdgeConditionBuilder base) {
      super(sig, nodeCache);
      this.base = base;
      // this.infos = new ArrayList<>(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.modelevolution.henshin2kodkod.DanglingEdgeConditionBuilder#condition
     * ()
     */
    @Override
    Formula toFormula() {
      return base.toFormula().and(super.toFormula());
    }
  }

  private final Signature sig;
  private final NodeCache<?> nodeCache;
  private final Collection<NodeInfo<?>> infos;
  private Formula formula;
  private boolean modified;
  private Collection<Formula> conditions;

  /**
   * @param sig
   * @param nodeCache
   */
  DanglingEdgeConditionBuilder(final Signature sig, final NodeCache<?> nodeCache) {
    this.sig = sig;
    this.infos = new ArrayList<>(0);
    this.nodeCache = nodeCache;
    modified = false;
  }

  // DanglingEdgeConditionBuilder addAll(Collection<NodeInfo<?>> infos) {
  // modified = this.infos.addAll(infos);
  // return this;
  // }

  /**
   * @return the dangling edge condition.
   */
  Formula toFormula() {
//    if (formula == null || modified)
      formula = Formula.and(conditions());

    return formula;
  }

  /**
   * @return
   * @deprecated Use a "fresh" {@link DanglingEdgeConditionBuilder} instead!
   */
  DanglingEdgeConditionBuilder createLoopExtension() {
    assert !(this instanceof ExtendedDanglingEdgeConditionBuilder);
    return new ExtendedDanglingEdgeConditionBuilder(sig, nodeCache, this);
  }

  Collection<Formula> conditions() {
    // if (conditions == null || modified)
    conditions = buildDanglingEdgeConditions();

    // modified = false;
    return conditions;
  }

  /**
   * Builds the Dangling Edge Condition (DEC).
   * 
   * @param infos
   * @return
   */
  private Collection<Formula> buildDanglingEdgeConditions() {
    Collection<Formula> conditions = new ArrayList<>();
    for (NodeInfo<?> info : nodeCache) {
      if (!info.isDeletedInfo())
        continue;
      /*
       * First, collect all known opposite nodes; here, "known" means they are
       * mentioned in the rule. Since they are known, edges leading from or to
       * them are deleted (if they are not deleted themselves by the rule).
       */
      Expression nonDangling = Expression.NONE;
      for (Edge in : info.node().getIncoming()) {
        final NodeInfo<?> srcInfo = nodeCache.cache(loadActionNode(in.getSource()));
        nonDangling = nonDangling.union(srcInfo.variable());
      }
      for (Edge out : info.node().getOutgoing()) {
        nonDangling = nonDangling.union(nodeCache.cache(out.getTarget()).variable());
      }
      /*
       * Next, collect all actual opposite nodes that have edges leading from
       * and to this node.
       */
      Expression maybeDangling = Expression.NONE;
      for (EReference inref : sig.inRefs(info.type())) {
        final Relation in = sig.pre(info.type(), inref);
        maybeDangling = maybeDangling.union(in.join(info.variable()));
      }
      for (EReference outref : sig.outRefs(info.type())) {
        final Relation out = sig.pre(info.type(), outref);
        maybeDangling = maybeDangling.union(info.variable().join(out));
      }
      /*
       * If the set of nodes that are known to be connected by non-dangling
       * edges to this node is equal to the set of nodes that actually have an
       * edge leading from or to this node, the dangling edge condition is
       * satisfied; otherwise, not.
       */
      conditions.add(nonDangling.eq(maybeDangling));
    }
    return conditions;
  }

  // /**
  // * @param info
  // */
  // void addInfo(NodeInfo<?> info) {
  // modified |= infos.add(info);
  // }
}

// static DanglingEdgeConditionBuilder init(
// final Signature sig, final ParamDataset params, NodeCache nodeCache) {
// return new DanglingEdgeConditionBuilder(sig, params, nodeCache);
// }
//
// /**
// * Get the {@link Danglers} for the given <code>node</code>.
// *
// * @param node
// * @return the {@link Danglers} for the given <code>node</code>
// */
// Danglers danglers(Node node) {
// final Danglers danglers;
// if (danglerMap.containsKey(node))
// danglers = danglerMap.get(node);
// else {
// danglers = new Danglers(isMulti(node) ? sig.pre(node.getType())
// : cache(node));
// danglerMap.put(node, danglers);
// }
// return danglers;
// }
//
// /**
// * Construct the dangling-edge condition.
// *
// * @return the dangling-edge condition
// */
// Formula danglingEdgeCondition() {
// Formula f = Formula.TRUE;
// for (Danglers d : danglerMap.values()) {
// f = f.and(d.danglingCondition());
// }
// return f;
// }
//
// /**
// * @param node
// * @return
// */
// Danglers collectDanglingEdges(final Node node) {
// final Danglers dangls = danglers(node);
//
// // These are the incoming edges which are present in the rule, that is,
// // for these edges a match must exist and they are thus deleted by the
// // graph rule; hence, we store these edges as _impossibly_ dangling
// for (final Edge in : node.getIncoming()) {
// assert isDeleted(in);
// final LeafExpression incomingSrc = cache(loadActionNode(in.getSource()));
// dangls.addImpossible(incomingSrc);
// }
// // Likewise, for all outgoing edges: the targets of these edges are
// // deleted for sure; hence, it is _impossible_ that these remain
// // dangling after the deletion of the node.
// for (Edge out : node.getOutgoing()) {
// assert isDeleted(out);
// final LeafExpression tgt = cache(out.getTarget());
// dangls.addImpossible(tgt);
// }
// // TODO: do not work correctly on multi-nodes! See task for details!
// // Now, collect all possible incoming and outgoing references of the
// // node; this is the set of references that are possibly left dangling
// // if it does not coincide with the set of actually deleted edges.
// for (EReference in : sig.inRefs(node.getType())) {
// final Relation incoming = sig.pre(in);
// dangls.addPossible(incoming, EdgeDirection.IN);
// }
// for (EReference out : sig.outRefs(node.getType())) {
// final Relation outgoing = sig.pre(out);
// dangls.addPossible(outgoing, EdgeDirection.OUT);
// }
// // TODO: if parsing is expensive, maybe store attr->attrExpr in a map
// for (Attribute attr : node.getAttributes()) {
// final StateRelation src2attr = sig.prePost(attr.getType());
// final Expression attrExpr = ExprParser.parse(attr.getValue(), attr
// .getType().getEAttributeType(), params, sig.enums());
// dangls.addAttributeRestriction(src2attr.preState(), attrExpr);
// }
// return dangls;
// }
//
// private LeafExpression cache(Node node) {
// return nodeCache.cache(node).var();
// }