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
import java.util.List;

import kodkod.ast.Formula;
import kodkod.ast.Relation;

import org.eclipse.emf.henshin.model.Annotation;
import org.eclipse.emf.henshin.model.Rule;
import org.modelevolution.emf2rel.Signature;
import org.modelevolution.emf2rel.StateRelation;
import org.modelevolution.rts.Transition;
import org.modelevolution.rts.TransitionRelation;

import com.google.inject.internal.Annotations;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * Restrictions:
 * 
 * <ul>
 * <li>Rule parameters may only be of type EInt or EBoolean or EEnum
 * <li>Rule parameters can not be passed values to from outside, but they can
 * and will be assigned values during the matching process
 * <li>Attribute conditions and attribute values may operate on rule parameters
 * only. The following arithmetic operations are supported ... and the following
 * logical operations are supported ... .
 * 
 * Further, the above restrictions are not enforced, that is, the translation
 * may diverge, but will most likely just crash.
 * 
 * @author Sebastian Gabmeyer
 * 
 */
public final class RuleTranslator {
  /**
   * @requires \forall r : rules | r.isActivated()==true
   * @param sig
   * @param rules
   * @return
   */
  public static TransitionRelation translateAll(final Signature sig, final Collection<Rule> rules) {
    if (sig == null || rules == null)
      throw new NullPointerException();
    if (rules.isEmpty())
      return null;
    
    /*
     * Assumptions: rule is activated, checkDangling, with only one multiGraph,
     * all nac/pac connections to/from multinodes are multinodes too, nested
     * condition have no nested conditions
     */
    final RuleTranslator rt = new RuleTranslator(sig);
    final TransitionRelation transitionRelation = new TransitionRelation(rules.size(), rt.noOps);
    for (final Rule rule : rules) {
      assert rule.isActivated();
      assert !rule.getName().toLowerCase().startsWith("bad");
      assert !rule.getName().toLowerCase().startsWith("inv");
      assert !rule.getName().toLowerCase().startsWith("reach");
      
      final Transition t = rt.translate(rule);
      if (t != null)
        transitionRelation.add(t);
    }

    return transitionRelation;
  }

  private final Signature sig;

  private final Collection<Formula> noOps;

  /**
   * @param sig
   * @param max
   */
  private RuleTranslator(final Signature sig) {
    this.sig = sig;
    this.noOps = buildNoOps(sig.states());
  }

  /**
   * Constructs a formula that ensure that no changes are performed, ie, for
   * every {@link StateRelation state relation} <code>S</code> it holds that
   * <code>S' = S</code>.
   * 
   * @return
   * 
   * @return a formula s.t. <code>\forall s : states | s.post() = s.pre()</code>
   */
  private Collection<Formula> buildNoOps(final Collection<StateRelation> states) {
    final Collection<Formula> noOps = new ArrayList<>(states.size());
    for (final StateRelation r : states) {
      if (r.isStatic())
        continue;
      noOps.add(r.postState().eq(r.preState()));
    }
    return noOps;
  }

  /**
   * @param rule
   *          TODO
   * @return
   */
  private Transition translate(final Rule rule) {
    final int expectedSize = rule.getActionNodes(null).size();
    final NodeCache<VarNodeInfo> nodeCache = new VarNodeCache(sig, expectedSize);
    final ParamDataset params = ParamDataset.create(rule.getParameters());
    //////////////////////// Get Features of Rule
    String[] pc = null;

    for (final Annotation annotation : rule.getAnnotations()) {
  	  if (annotation.getKey().contentEquals("features")) {
  		  pc = annotation.getValue().split(",");
  	  }
    }
    System.out.println("Rule features " + rule.getName() + ":");
    if (pc != null) {
	      for (final String p : pc) {
	    	  System.out.println("  " + p.trim());
	      }
    }
    ////////////////////////
    /*
     * Translate the LHS and PACs to relational declarations, premises, and
     * constraints (injectivity and dangling edge conditions). Moreover,
     * translate NACs to negated existentially quantified relational formulas
     * including injectivity constraints.
     */
    final LhsTranslator lhsTranslator = new LhsTranslator(rule, sig, nodeCache, params);
    final RuleTranslation ruleTranslation = lhsTranslator.translate(rule.getLhs());

    /* Translate attribute conditions to relational premises */
    final AttributeConditionTranslator attrCondHandler;
    attrCondHandler = new AttributeConditionTranslator(rule, params, sig.enums());
    final Collection<Formula> attrConstraints = attrCondHandler.translate(rule.getAttributeConditions());
    ruleTranslation.addPremise(Formula.and(attrConstraints));

    /* Translate the RHS to relational consequents */
    final RhsTranslator rhsTranslator = RhsTranslator.create(sig, nodeCache, params,
                                                             ruleTranslation);
    rhsTranslator.translate(rule.getRhs());

    final TransitionBuilder tBuilder = new TransitionBuilder(rule, sig, nodeCache, params);

    /* Assign upper bounds to rule parameters according to the parameter's type. */
    sig.bindParams(params);

    // FIXME: Make RuleTranslation class multi-rule aware
    for (final Rule multiRule : rule.getMultiRules()) {
      final RuleTranslation loopTranslation = lhsTranslator.translate(multiRule.getLhs());
      rhsTranslator.translate(multiRule.getRhs());
      tBuilder.addLoop(multiRule, loopTranslation);
    }

    return tBuilder.toTransition(ruleTranslation);
  }
}
