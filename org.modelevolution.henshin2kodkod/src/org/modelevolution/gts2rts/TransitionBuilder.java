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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import kodkod.ast.Formula;
import kodkod.ast.LeafExpression;
import kodkod.ast.Relation;

import org.eclipse.emf.henshin.model.Rule;
import org.modelevolution.emf2rel.Signature;
import org.modelevolution.rts.Loop;
import org.modelevolution.rts.Transition;

/**
 * 
 * @author Sebastian Gabmeyer
 * 
 */
class TransitionBuilder {
  // private Signature sig;
  // private final InjectivityConditionBuilder injBuilder;
  // private final Formula featureMultiplicities;
  private final NodeCache<?> nodeCache;
  private final String name;
  private final Collection<TransitionBuilder> loops;
  // private final DanglingEdgeConditionBuilder decBuilder;
  private Collection<Formula> decls;
  private Collection<Formula> premises;
  private Collection<Formula> consequents;
  private final ParamDataset params;
  private final Rule rule;

  /**
   * @param rule
   * @param params
   *          TODO
   * 
   */
  TransitionBuilder(final Rule rule, final Signature sig, final NodeCache<?> nodeCache,
      final ParamDataset params) {
    this.rule = rule;
    this.name = rule.getName();
    // this.sig = sig;
    this.nodeCache = nodeCache;
    this.params = params;
    // featureMultiplicities = sig.multiplicityConstraint();
    // injBuilder = InjectivityConditionBuilder.create(rule);
    // decBuilder = new DanglingEdgeConditionBuilder(sig, nodeCache);
    loops = new ArrayList<>(rule.getMultiRules().size());
  }

  /**
   * @param base
   *          TODO
   * @param loopRule
   *          TODO
   * 
   */
  private TransitionBuilder(final TransitionBuilder base, Rule loopRule) {
    rule = loopRule;
    // sig = base.sig;
    name = base.name + "_loop";
    nodeCache = base.nodeCache;
    params = base.params;
    // featureMultiplicities = base.featureMultiplicities;
    // injBuilder = base.injBuilder.createLoopExtension(loopRule);
    // decBuilder = base.decBuilder.createLoopExtension();
    /* There mustn't be any subloops in a loop */
    loops = Collections.emptyList();
  }

  /**
   * @param loopRule
   * @deprecated Method functionality will be offered by
   *             {@link RuleTranslation#addMulti(RuleTranslation)}.
   */
  void addLoop(Rule loopRule, final RuleTranslation loopTranslation) {
    final TransitionBuilder loop = new TransitionBuilder(this, loopRule);
    loop.init(loopTranslation);
    loops.add(loop);
  }

  /**
   * @param translation
   * @deprecated Method functionality moved to
   *             {@link #toTransition(RuleTranslation)}
   */
  void init(final RuleTranslation translation) {
    decls = translation.declarations();
    premises = translation.premises();
    // final List<NodeInfo> lhsInfos = translation.premiseInfos();
    // decBuilder.addAll(lhsInfos);
    // final List<NodeInfo> allInfos = translation.infos();
    // injBuilder.addAll(allInfos);
    consequents = translation.effects();
  }

  /**
   * @param ruleTranslation
   *          TODO
   * @return
   */
  Transition toTransition(RuleTranslation ruleTranslation) {
    // FIXME: handle loop transitions
    final Collection<Loop> loopTransitions = new ArrayList<>(loops.size());
    for (final TransitionBuilder loop : loops) {
      throw new UnsupportedOperationException();
      // loopTransitions.add(loop.toTransition(ruleTranslation));
    }

    final Collection<LeafExpression> variables = nodeCache.variables();
    for (final ParamData param : params) {
      if (param.isUsedByRule(rule))
        variables.add(param.var());
    }
    // FIXME: FIX multiplicities!!
    final Collection<Formula> multiplicities = Collections.emptyList();
    final Collection<Formula> premises = ruleTranslation.premises();
    final Collection<Formula> consequents = ruleTranslation.effects();
    final Collection<Formula> declarations = ruleTranslation.declarations();
    final Transition t = new Transition(name, variables, premises, consequents, declarations,
                                        ruleTranslation.injectivityConditions(),
                                        ruleTranslation.danglingEdgeConditions(), multiplicities,
                                        loopTransitions);
    return t;
  }
}
