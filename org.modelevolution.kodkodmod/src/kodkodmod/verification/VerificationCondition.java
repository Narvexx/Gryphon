/*
 * KodkodMod -- Copyright (c) 2014-present, Sebastian Gabmeyer
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
package kodkodmod.verification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kodkod.ast.BinaryFormula;
import kodkod.ast.Formula;
import kodkod.ast.Relation;
import kodkod.engine.config.ExtOptions;
import kodkod.util.ints.IntSet;
import kodkod.util.nodes.Nodes;

import org.modelevolution.commons.modifications.Pair;

/**
 * The verification condition consists of the transition relation and a bad
 * property that should not be reachable. The verification condition is
 * conjunction T_1 && ... && T_n && badProperty where T_1,...,T_n are the
 * transitions. Each transition is an implication of the from
 * <code>pre(x_1,...,x_n) => post(x_1,...,x_n)</code> where
 * <code>pre(x_1,...,x_n)</code> defines the precondition of a transition
 * relation over the state variables <code>x_1,...,x_n</code> and
 * <code>post(x_1,...,x_n)</code> the postcondition or effect of the transition
 * on the state variables.
 * 
 * @author Sebastian Gabmeyer
 */
@Deprecated
public class VerificationCondition {

  private final Formula transitionRelation;
  private final List<BinaryFormula> transitions;
  private final Formula badProperty;
  private final Formula formula;
  private final List<Formula> antecedents;
  private final List<Formula> consequents;

  // private boolean debug = false;

  /**
   * Constructs a verification condition from the given
   * <code>transitionRelation</code> and the <code>badProperty</code>.
   * 
   * @throws NullPointerException
   *           if transitionRelation==null || badProperty==null
   * @throws IllegalArgumentException
   *           if a transition T_i is not of the form
   *           <code>pre(x_1,...,x_n) => post(x_1,...,x_n)</code>
   * @param transitionRelation
   *          a conjunction of implication of the form
   *          <code>pre(x_1,...,x_n) => post(x_1,...,x_n)</code>
   * @param badProperty
   *          a proof obligation defining an undesired state
   * @param options
   *          a set of options that parameterize the verification procedure
   */
  public VerificationCondition(Formula transitionRelation, Formula badProperty,
      ExtOptions options) {
    if (options.doVCCheck()) {
      VCChecker.checkVerificationCondition(transitionRelation, badProperty);
    }
    this.formula = Formula.and(transitionRelation, badProperty);
    this.transitionRelation = transitionRelation;
    this.transitions = extractTransitions(transitionRelation);
    Pair<List<Formula>, List<Formula>> rs = splitImplications(transitions);
    this.antecedents = rs.fst();
    this.consequents = rs.snd();
    this.badProperty = badProperty;
  }

  /**
   * Constructs a verification condition from the given <code>formula</code>
   * which has the form <code>transitionRelation && badProperty</code>
   * 
   * @throws NullPointerException
   *           if formula==null
   * @throw IllegalArgumentException if Nodes.conjuncts(formula).size() != 2
   * @throws IllegalArgumentException
   *           if a transition T_i is not of the form
   *           <code>pre(x_1,...,x_n) => post(x_1,...,x_n)</code>
   * @param formula
   *          represents the verification condition and has the form
   *          <code>transitionRelation && badProperty</code>
   * @see Nodes#conjuncts(Formula)
   */
  public VerificationCondition(Formula formula) {
    this.formula = formula;
    this.transitionRelation = extractTransitionRelation(formula);
    this.transitions = extractTransitions(transitionRelation);
    Pair<List<Formula>, List<Formula>> rs = splitImplications(transitions);
    this.antecedents = rs.fst();
    this.consequents = rs.snd();
    this.badProperty = extractBadProperty(formula);
  }

  public Formula formula() {
    return this.formula;
  }

  public Formula transitionRelation() {
    return transitionRelation;
  }

  public Formula badProperty() {
    return badProperty;
  }

  public List<BinaryFormula> transitions() {
    return transitions;
  }

  public List<Formula> antecedents() {
    return antecedents;
  }

  public List<Formula> consequents() {
    return consequents;
  }

  /**
   * Returns the set of conjuncts T_1 && ... && T_n && badProperty found in the
   * transition relation plus the bad property.
   * 
   * @return the set of conjuncts that define the verification condition
   */
  public Set<Formula> conjuncts() {
    Set<Formula> conjuncts = new HashSet<Formula>(transitions);
    conjuncts.add(badProperty);
    return conjuncts;
  }

  /**
   * @throws IllegalArgumentException
   *           if a transition T_i is not of the form
   *           <code>pre(x_1,...,x_n) => post(x_1,...,x_n)</code>
   * @param tr
   *          the transition relation of the form T_1 && ... && T_n
   * @return the set of T_1,...,T_n (in order)
   */
  private List<BinaryFormula> extractTransitions(Formula tr) {
    final Set<Formula> conjuncts = Nodes.conjuncts(tr);
    List<BinaryFormula> transitions = new ArrayList<>(conjuncts.size());
    for (Formula t : conjuncts) {
      // if (debug) {
      // t.accept(new VCChecker());
      // //&& !(t instanceof BinaryFormula && ((BinaryFormula) t).op() ==
      // FormulaOperator.IMPLIES)) {
      // throw new IllegalArgumentException("Transition t="
      // + t.toString() + " is not an implication.");
      // }
      transitions.add((BinaryFormula) t);
    }
    // if (debug) {
    // for (BinaryFormula bf : transitions) {
    // //bf.
    // }
    // }
    return transitions;
  }

  /**
   * @param formulabounds
   * @return
   */
  private Formula extractBadProperty(Formula formula) {
    if (formula == null)
      throw new NullPointerException("formula must not be null.");
    Set<Formula> cs = Nodes.conjuncts(formula);
    if (cs.size() != 2)
      throw new IllegalArgumentException(
          "formula must consist of two conjuncts: transitionRelation && badProperty.");
    return (Formula) cs.toArray()[1];
  }

  /**
   * @param formula
   * @return
   */
  private Formula extractTransitionRelation(Formula formula) {
    if (formula == null)
      throw new NullPointerException("formula must not be null.");
    Set<Formula> cs = Nodes.conjuncts(formula);
    return (Formula) cs.toArray()[0];
  }

  /**
   * @param formulas
   *          forall f in formulas | f instanceof BinaryFormula && f.op() ==
   *          FormulaOperator.IMPLIES
   * @return a pair of lists each containing the antecedents <i>ante</i> and
   *         consequents <i>cons</i> of an implication <i>ante => cons</i>.
   */
  private Pair<List<Formula>, List<Formula>> splitImplications(
      Collection<BinaryFormula> formulas) {
    List<Formula> as = new ArrayList<>(formulas.size());
    List<Formula> cs = new ArrayList<>(formulas.size());
    for (Formula f : formulas) {
      BinaryFormula bf = (BinaryFormula) f;
      as.add(bf.left());
      cs.add(bf.right());
    }
    return new Pair<List<Formula>, List<Formula>>(as, cs);
  }

  // /**
  // * @return
  // */
  // public StateVector stateVector() {
  // return this.stateVector;
  // }
  //
  // /**
  // * @param relVars
  // */
  // public void updateStateVector(Map<Relation, IntSet> relVars) {
  // stateVector.updateStateVars(relVars);
  // }

  /**
   * @param relVars
   */
  public void extractInputVars(Map<Relation, IntSet> relVars) {

  }
}
