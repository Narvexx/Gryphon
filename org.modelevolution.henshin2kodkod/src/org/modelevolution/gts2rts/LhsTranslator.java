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

import static org.modelevolution.gts2rts.util.NodeUtil.edgeMatch;
import static org.modelevolution.gts2rts.util.NodeUtil.isActionElement;
import static org.modelevolution.gts2rts.util.NodeUtil.isDeleted;
import static org.modelevolution.gts2rts.util.NodeUtil.isForbidden;
import static org.modelevolution.gts2rts.util.NodeUtil.isPreserved;
import static org.modelevolution.gts2rts.util.NodeUtil.loadActionNode;
import static org.modelevolution.gts2rts.util.NodeUtil.tuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import kodkod.ast.BinaryFormula;
import kodkod.ast.Decl;
import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.LeafExpression;
import kodkod.ast.Relation;
import kodkod.ast.Variable;
import kodkod.ast.operator.FormulaOperator;
import kodkod.engine.bool.BooleanVariable;
import kodkod.instance.Bounds;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.henshin.model.Action.Type;
import org.eclipse.emf.henshin.model.Annotation;
import org.eclipse.emf.henshin.model.Attribute;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.MappingList;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.modelevolution.emf2rel.Enums;
import org.modelevolution.emf2rel.Signature;
import org.modelevolution.emf2rel.StateRelation;
import org.modelevolution.gts2rts.attrexpr.AttrExprParser;
import org.modelevolution.gts2rts.util.NodeUtil;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class LhsTranslator {

  // static LhsHandler create(final Signature sig, NodeCache nodeCache,
  // final ParamDataset params) {
  // return new LhsHandler(sig, nodeCache, params);
  // }

  private final Signature sig;
  private final ParamDataset params;
  private final EffectCollector effectCollector;
  private final NodeCache<VarNodeInfo> nodeCache;
  private final Enums enums;
  private final PremiseCollector premiseCollector;
  private InjectivityConditionBuilder injBuilder;

  /**
   * @param rule
   * @param sig
   * @param nodeCache
   * @param params
   */
  LhsTranslator(Rule rule, final Signature sig, final NodeCache<VarNodeInfo> nodeCache,
      final ParamDataset params) {
    this.sig = sig;
    this.nodeCache = nodeCache;
    this.params = params;
    this.premiseCollector = new PremiseCollector();
    this.effectCollector = new EffectCollector(sig);
    this.enums = sig.enums();
    this.injBuilder = InjectivityConditionBuilder.create(rule);
  }

  /**
   * @param node
   * @return
   */
  private NodeInfo<Formula> cache(final Node node) {
    return nodeCache.cache(node);
  }

  /**
   * @param lhs
   * @param lhs2rhsMappings
   * @return
   * 
   */
  RuleTranslation translate(Graph lhs) {
    assert lhs.isLhs();
    final DanglingEdgeConditionBuilder decBuilder = new DanglingEdgeConditionBuilder(
                                                                                     sig,
                                                                                     nodeCache.readOnly());

    for (final Node node : lhs.getNodes()) {
      if (!isActionElement(node))
        continue;
      assert isPreserved(node) ^ isDeleted(node);
      final NodeInfo<Formula> info = translateNode(node);
      premiseCollector.addDeclaration(info);
      injBuilder.addInfo(info);
      // decBuilder.addInfo(info);
    }

    /*
     * NAC nodes and edges give rise to a different kind of matching condition,
     * namely, a negated, existentially quantified formula. Note that the
     * existentially quantified variables, which match a node, are local to this
     * condition and cannot be referenced from other parts of the formula;
     * hence, injectivity constraints are handled locally.
     */
    for (final NestedCondition nac : lhs.getNACs()) {
      final Graph nacGraph = nac.getConclusion();
      assert nac.getHost() == lhs;
      final NacTranslator nacTranslator = new NacTranslator(sig, nodeCache.readOnly(),
                                                            injBuilder.createNested(nac), params,
                                                            enums);
      final Formula nacCondition = nacTranslator.translate(nacGraph);
      premiseCollector.addPremise(nacCondition);

      // final List<Node> nacNodes = nacGraph.getNodes();
      // final List<NodeInfo> nacInfos = new ArrayList<>(nacNodes.size());
      //
      // for (final Node node : nacNodes) {
      // if (isActionElement(node)) {
      // final NodeInfo info = translateNode(node);
      // nacInfos.add(info);
      // }
      // }
      // infos.addAll(nacInfos);

      for (final Edge edge : nacGraph.getEdges()) {
        final Formula edgeCondition = translateForbidRequireEdge(edge);
        premiseCollector.addPremise(edgeCondition);
      }
    }

    /*
     * PAC nodes and edges yield the same conditions as those for the LHS's
     * nodes and edges
     */
    for (final NestedCondition pac : lhs.getPACs()) {
      final List<Node> pacNodes = pac.getConclusion().getNodes();
      // final List<NodeInfo> pacInfos = new ArrayList<>(pacNodes.size());

      for (final Node node : pacNodes) {
        if (isActionElement(node)) {
          final NodeInfo<Formula> info = translateNode(node);
          premiseCollector.addDeclaration(info);
          injBuilder.addInfo(info);
          // pacInfos.add(info);
        }
      }
      // infos.addAll(pacInfos);

      for (final Edge edge : pac.getConclusion().getEdges()) {
        final Formula edgeCondition = translateForbidRequireEdge(edge);
        premiseCollector.addPremise(edgeCondition);
      }
    }

    // premiseCollector.addAll(infos);

    return new RuleTranslation(premiseCollector, effectCollector, injBuilder, decBuilder);
  }

  // /**
  // * @param nacGraph
  // * @return
  // */
  // private Formula translateNac(final Graph nacGraph,
  // final InjectivityConditionBuilder nestedInjBuilder) {
  //
  // final Collection<Formula> nacCondition = new ArrayList<>();
  // final NacNodeCache nacCache = new NacNodeCache(sig);
  // for (final Node node : nacGraph.getNodes()) {
  // if (!NodeUtil.isForbidden(node))
  // continue;
  // final NacNodeInfo info = nacCache.cache(node);
  // nestedInjBuilder.addInfo(info);
  // for (final Edge edge : node.getOutgoing()) {
  // final Node srcNode = edge.getSource();
  // final Node tgtNode = edge.getTarget();
  // final NodeInfo<?> srcInfo = nacCache.cache(srcNode);
  // // final NodeInfo<?> tgtInfo =
  // }
  // for (final Edge edge : node.getIncoming()) {
  // final Node src = edge.getSource();
  // if (NodeUtil.isForbidden(src))
  // continue;
  // }
  // for (final Attribute attr : node.getAttributes()) {
  //
  // }
  // // final Variable var = Variable.
  // }
  // return Formula.and(nacCondition);
  // }

  /**
   * @param edge
   * @return
   */
  private Formula translateForbidRequireEdge(final Edge edge) {
    final Formula edgeCondition;
    /* We are only interested in forbidden or required edges */
    if (isActionElement(edge, Type.FORBID) || isActionElement(edge, Type.REQUIRE)) {
      final Node srcNode = edge.getSource();
      final Node tgtNode = edge.getTarget();
      /*
       * If either the source or the target node is an action node, then skip
       * this edge because a condition has already been generated by the
       * translateNode(srcNode|tgtNode) method
       */
      if (isActionElement(srcNode) || isActionElement(tgtNode)) {
        edgeCondition = Formula.TRUE;
      } else {
        final NodeInfo<?> srcInfo = cache(loadActionNode(srcNode));
        final NodeInfo<?> tgtInfo = cache(loadActionNode(tgtNode));
        final LeafExpression src = srcInfo.variable();
        final LeafExpression tgt = tgtInfo.variable();
        final Relation src2tgt = sig.pre(srcInfo.type(), edge.getType());
        
        if (isActionElement(edge, Type.FORBID))
          edgeCondition = edgeMatch(src, tgt, src2tgt).not();
        else
          edgeCondition = edgeMatch(src, tgt, src2tgt);
        
      }
    } else {
      edgeCondition = Formula.TRUE;
    }
    return edgeCondition;
  }

/**
   * @param node
   * @return
   * @return
   */
  private NodeInfo<Formula> translateNode(final Node node) {
    assert !isForbidden(node);
    /*
     * A node is unconstrained if it has neither incoming nor outgoing edges and
     * is not restricted by attribute values
     */
    final NodeInfo<Formula> nodeInfo = cache(node);
    final LeafExpression var = nodeInfo.variable();
    final Expression binding = nodeInfo.binding();
    assert binding == sig.state(node.getType()).preState();

    /* Add condition: node_var \in NodeTypeRelation */
    premiseCollector.addPremise(var.in(binding));

    if (isDeleted(node)) {
      effectCollector.addDeleteMod(sig.state(node.getType()), var);//TODO: replace call to sig.state(node.getType()) with nodeInfo.state()
    }
    translateAttributes(nodeInfo);
    translateOutgoingEdges(nodeInfo);
    /*
     * Multi/Pac nodes are not reachable through outgoing edges from the nodes
     * of the root rule's LHS graph; thus, we need to generate condition on
     * their incoming edges, too.
     */
    final Graph rootLhs = node.getGraph().getRule().getRootRule().getLhs();
    if (node.getGraph() != rootLhs) {
      translateIncomingEdges(nodeInfo);
    }

    return nodeInfo;
  }

  /**
   * @param info
   * @param lhs2rhsMappings
   * @param attributes
   * @param src
   * @param attrMatches
   */
  private void translateAttributes(final NodeInfo<?> info) {
    final List<Attribute> attributes = info.node().getAttributes();
    final AttributeTranslator attrTranslator = new AttributeTranslator(sig, nodeCache.readOnly(),
                                                                       params, enums);
    final LeafExpression src = info.variable();
    for (final Attribute attr : attributes) {
      final Expression preAttrExpr = attrTranslator.translateToExpr(attr);

      // /*
      // * Parse the string and generate a formula that the value of the
      // attribute
      // * is required to satisfied, ie, to match.
      // */
      // final EDataType attributeType = attr.getType().getEAttributeType();
      // final AttrExprParser parser = new
      // AttrExprParser(info.node().getGraph().getRule(),
      // attributeType, params, enums);
      // final Expression preAttrExpr = parser.parse(attr.getValue());
      // final Formula attrValueMatch =
      // src.join(src2attr.preState()).eq(preAttrExpr);

      premiseCollector.addPremise(attrTranslator.translateToFormula(attr, preAttrExpr));

      /*
       * In case the node that hosts the attribute is preserved we need to look
       * at the node's image in the RHS and check if the attribute's value is
       * changed. This is necessary because Henshin (supposedly) only maps nodes
       * and edges but not attributes between the LHS and the RHS. This has the
       * effect that attributes of preserved nodes, whose value is changed, are
       * only accessible through the LHS graph. Thus, we extract the next/post
       * state value that the RHS assigns to the attribute at this point and
       * pass the resulting formulas to this#handleRhs(List<Formula>).
       */
      if (info.isPreservedInfo()) {
        final Graph lhs = info.node().getGraph();
        assert lhs.isLhs();
        final Graph rhs = lhs.getRule().getRhs();
        MappingList lhs2rhsMappings = lhs.getRule().getMappings();
        final Node rhsNode = lhs2rhsMappings.getImage(info.node(), rhs);
        assert rhsNode != null;
        final Attribute postAttr = rhsNode.getAttribute(attr.getType());
        if (postAttr != null && !postAttr.getValue().equals(attr.getValue())) {
          final StateRelation src2attr = attrTranslator.attrState(attr);
          effectCollector.addDeleteMod(src2attr, tuple(src, preAttrExpr));
          final Expression postAttrExpr = attrTranslator.translateToExpr(postAttr);
          effectCollector.addCreateMod(src2attr, tuple(src, postAttrExpr));
        }
      } else if (info.isDeletedInfo()) {
        assert info.isDeletedInfo();
        final StateRelation src2attr = attrTranslator.attrState(attr);
        effectCollector.addDeleteMod(src2attr, tuple(src, preAttrExpr));
      }
    }
  }

  /**
   * @param node
   * @param edgeMatches
   * @param src
   */
  private void translateOutgoingEdges(final NodeInfo<?> info) {
    final List<Edge> outEdges = info.node().getOutgoing();
    final LeafExpression src = info.variable();

    for (final Edge outEdge : outEdges) {
      final NodeInfo<?> tgtInfo = cache(loadActionNode(outEdge.getTarget()));
      final LeafExpression tgt = tgtInfo.variable();
      final StateRelation srcState = sig.state(info.type(), outEdge.getType());
      final Relation src2tgt = srcState.preState();
        
      final Formula pc = getPresenceCondition(outEdge.getAnnotations());
      
      assert info instanceof VarNodeInfo;

      final Formula match = edgeMatch(src, tgt, src2tgt);
      
      if (pc != null) {
    	  premiseCollector.addPremise(pc.implies(match));
      } else {
    	  premiseCollector.addPremise(match);
      }
      
      

      if (isDeleted(outEdge))
        effectCollector.addDeleteMod(srcState, tuple(src, tgt));
    }
  }

  private void translateIncomingEdges(final NodeInfo<?> tgtInfo) {
    final List<Edge> inEdges = tgtInfo.node().getIncoming();
    final LeafExpression tgt = tgtInfo.variable();

    for (final Edge inEdge : inEdges) {
      final Node srcNode = loadActionNode(inEdge.getSource());
      /*
       * if the src and the tgt node belong to the same graph, we translated the
       * edge as an outgoing edge already.
       */
      if (srcNode.getGraph() == tgtInfo.node().getGraph())
        continue;
      final LeafExpression src = cache(srcNode).variable();
      final StateRelation srcState = sig.state(srcNode.getType(), inEdge.getType());
      final Relation src2tgt = srcState.preState();
      
      final Formula pc = getPresenceCondition(inEdge.getAnnotations());
      
      
      
    
      assert tgtInfo instanceof VarNodeInfo;
      
      final Formula match = edgeMatch(src, tgt, src2tgt);
      premiseCollector.addPremise(match);
      if (pc != null) {
    	  premiseCollector.addPremise(pc);
      }
      if (isDeleted(inEdge))
        effectCollector.addDeleteMod(srcState, tuple(src, tgt));
    }
  }
  
//  private Formula boundAbove(final EClass eClass, final EStructuralFeature feature) {
//  final Relation domain = registry.postState(eClass);
//  final Variable d = Variable.unary(domain.name());
//  final Relation r = registry.postState(eClass, feature);
//  final IntConstant upperBound = IntConstant.constant(feature.getUpperBound());
//  return d.join(r).count().lte(upperBound).forAll(d.oneOf(domain));
//}
  
  private Formula getPresenceCondition(List<Annotation> annotations) {
	  final String pc_val = (!annotations.isEmpty()) ? annotations.get(0).getValue() : "";
	  
	  
//	  if (!pc_val.isEmpty()) {
//	      final StateRelation test = sig.state(EcorePackage.Literals.EBOOLEAN);
//	      final Relation boolBound = test.preState();
//	      
//	      return Variable.nary(pc_val, 2).join(boolBound);  
//	  }
//	  return null;
	  

	  // get Relational Variable from VariabilityRuleTranslator	  
	  //final Variable prescond = (!annotations.isEmpty() && !pc_val.isEmpty()) ?  : null;
	  
	  return pc_val.contentEquals("") ? null : VariabilityRuleTranslator.getRelationByAtom(pc_val, sig);
	  //return prescond != null ? prescond.one() : null;
  }

  // private List<Formula> translateNACs(final List<NestedCondition> nacs) {
  // return translateNestedCondition(nacs, Type.FORBID);
  // }
  //
  // private List<Formula> translatePACs(final List<NestedCondition> pacs) {
  // return translateNestedCondition(pacs, Type.REQUIRE);
  // }

  // /**
  // * @param nacs
  // * @param forbid
  // * @return
  // */
  // private List<Formula> translateNestedCondition(
  // List<NestedCondition> conditions, Type action) {
  // assert action == Type.FORBID || action == Type.REQUIRE;
  // assert conditions != null;
  // final List<Formula> patterns = new ArrayList<>(conditions.size());
  // for (final NestedCondition condition : conditions) {
  // final NodeCache nestedCache = NodeCache.createNested();
  // final List<Formula> nested = new ArrayList<>();
  // for (final Node n : condition.getConclusion().getNodes()) {
  // // TODO: we can safely skip any multi-required nodes because they are
  // // satisfied if matched ZERO or more times
  // if (isMulti(n) && isRequired(n)) continue;
  // Variable src = null;
  // boolean isUnconstrained = true;
  // if (isActionNode(n, action)) {
  // src = nestedCache.cache(n).var();
  // }
  // for (final Edge edge : n.getOutgoing()) {
  // if (!isActionNode(edge, action)) continue;
  // isUnconstrained = false;
  // if (src == null) src = cache(n.getActionNode()).var();
  // Variable tgt = isActionNode(edge.getTarget(), action) ? nestedCache
  // .cache(edge.getTarget()).var() : cache(
  // edge.getTarget().getActionNode()).var();
  // Formula match = edgeMatch(src, tgt, sig.pre(edge.getType()));
  // nested.add(match);
  // }
  // for (final Edge edge : n.getIncoming()) {
  // if (!isActionNode(edge, action)) continue;
  // isUnconstrained = false; // we handle this edge as an outgoing edge
  // // when iterating over edge.getSource()
  // if (isActionNode(edge.getSource(), action)) {
  // // since !is(edge.getSource(), Type.FORBID) || !is(edge.getSource(),
  // // Type.REQUIRE) the
  // // node is preserved and cached in the nodeInfos
  // continue;
  // }
  // Variable realSrc = cache(edge.getSource().getActionNode()).var();
  // Formula match = edgeMatch(realSrc, src, sig.pre(edge.getType()));
  // nested.add(match);
  // }
  // for (final Attribute attr : n.getAttributes()) {
  // if (!isActionNode(attr, action)) continue;
  // isUnconstrained = false;
  // final PrePostRelation src2attr = sig.prePost(attr.getType());
  // AttributeAssignmentParser parser = new AttributeAssignmentParser(
  // src.join(src2attr.pre()), attr.getType().getEAttributeType(),
  // params);
  // final Formula attrValueMatch = parser.parse(attr.getValue());
  // nested.add(attrValueMatch);
  // }
  // if (isUnconstrained && isActionNode(n, action)) {
  // assert src != null;
  // // the node is not further constrained through edge or attribute
  // // relations; thus, we simply test for the existence of such a node
  // nested.add(src.some());
  // }
  // }
  // // TODO: handle isMulti-case!
  // // TODO: correct this!
  // // Formula pattern = Formula.and(nested).forSome(decls(nestedInfos));
  // // if (action == Type.FORBID) pattern = pattern.not();
  // // patterns.add(pattern);
  // }
  // return patterns;
  // }
  //
  // /**
  // * @param node
  // * @param src
  // */
  // private void translateMultiNode(final Node node) {
  // assert isMulti(node);
  // Variable src = cache(node).var();
  // assert nodeCache.get(node) instanceof MultiNodeInfo;
  // MultiNodeInfo info = (MultiNodeInfo) nodeCache.get(node);
  // Relation relation = sig.pre(node.getType());
  // info.addRequired(relation);
  //
  // if (isDeleted(node)) {
  // mods.addDeleteMod(sig.prePost(node.getType()), src);
  // }
  //
  // for (Attribute attr : node.getAttributes()) {
  // final Expression attrExpr = ExprParser.parse(attr.getValue(), attr
  // .getType().getEAttributeType(), params);
  // info.addRequired(attrExpr);
  // }
  // /*
  // * If the node is a multi-node, references coming from the "base" graph are
  // * part of the multi-graph (=the graph that hosts all multi-nodes), that is,
  // * all incoming edges are part of the multi-graph. Thus, we need to generate
  // * conditions for these edges while analyzing the multi-nodes.
  // */
  // for (final Edge edge : node.getIncoming()) {
  // /*
  // * If the source node of the incoming edge is not an action node and they
  // * belong to different graphs, i.e., edge.getSource().getGraph !=
  // * node.getGraph, load the variable of the corresponding action node
  // */
  // final Variable realSrcVar;
  // final Node realSrcNode = edge.getSource();
  // if (isActionElement(realSrcNode)
  // /* && edge.getSource().getGraph() == node.getGraph() */) {
  // assert isMulti(realSrcNode);
  // realSrcVar = cache(realSrcNode).var();
  // } else {
  // realSrcVar = cache(realSrcNode.getActionNode()).var();
  // }
  //
  // final Expression characteristic = realSrcVar
  // .join(sig.pre(edge.getType()));
  // if (isForbidden(realSrcNode)) info.addForbidden(characteristic);
  //
  // /*
  // * then, add an edge-match condition to the lhsMatches. If isDeleted(edge)
  // * returns true, add a delete modification, i.e.,
  // * mods.addDeleteMod(sig.pair(edge.getType()), realSrc.product(src))
  // */
  // // final Formula match = edgeMatch(realSrc, sig.pre(edge.getType()), src);
  // if (isDeleted(edge))
  // mods.addDeleteMod(sig.prePost(edge.getType()), tuple(realSrcVar, src));
  // }
  //
  // for (final Edge edge : node.getOutgoing()) {
  // final Variable tgt;
  //
  // }
  // }

}
