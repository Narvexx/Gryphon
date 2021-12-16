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
 * 
 * if p | m then (if p & mr then mr_eff & m else p_eff & !m) else else_eff
 * 
 * (p|m) => ((p&mr) => (mr_eff&m) & !(p&mr) => p_eff & !m) & !(p|m) => else_eff
 * 
 * !(p|m) | ((!(p&mr) | (mr_eff&m)) & ((p&mr) | p_eff & !m)) & ((p|m) |
 * else_eff)
 * 
 * (!(p|m) | !(p&mr) | (mr_eff&m)) & (!(p|m) | (p&mr) | (p_eff & !m)) & ((p|m) |
 * else_eff)
 * 
 * Note that multirule implies rule p, i.e., mr=>p because p \subseteq mr, and
 * mr_effects do not change elements in p's LHS.
 */
package org.modelevolution.rts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import kodkod.ast.Formula;
import kodkod.ast.LeafExpression;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public final class TransitionRelation implements Iterable<Transition> {

  // private static final class StandstillBuilder {
  //
  // private final String name = "standstill";
  // private final Collection<LeafExpression> variables = new ArrayList<>();
  // private final Collection<Formula> premises = new ArrayList<>();
  // private final Collection<Formula> consequents = new ArrayList<>();
  // private final Collection<Formula> decls = new ArrayList<>();
  // private final Collection<Formula> injectivities = new ArrayList<>();
  // private final Collection<Formula> decs = new ArrayList<>();
  // private final Collection<Formula> multiplicities = new ArrayList<>();
  //
  // private final Collection<Transition> loops = Collections.emptyList();
  //
  // private StateChanger toTransition() {
  // return new Transition(name, variables, premises, consequents, decls,
  // injectivities, decs,
  // multiplicities, loops);
  // }
  // }

  private final Collection<Transition> transitions;
  // private final Formula standstillConsequent;
  // private Formula standstillPremise;
  // private final StandstillBuilder builder;
  private final Collection<Formula> noOpConditions;
  // private Transition standstill;
  private boolean modified = false;
  private Set<Formula> conditions;
  // private boolean sealed;
  private final Map<Transition, Collection<LeafExpression>> variableMap;

  public TransitionRelation(final int numTransitions, Collection<Formula> noOpConditions) {
    transitions = new ArrayList<>(numTransitions);
    // builder = new StandstillBuilder();
    // builder.buildConsequent(states);
    this.noOpConditions = noOpConditions;
    conditions = new HashSet<>(noOpConditions);
    variableMap = new HashMap<>();

  }

  public void add(final Transition t) {
    modified = transitions.add(t);
    variableMap.put(t, t.variables());
    conditions.addAll(t.premises());
    conditions.addAll(t.constraints());
    conditions.addAll(t.consequents());
  }

  public Collection<Formula> conditions() {
    return Collections.unmodifiableCollection(conditions);
  }

  /**
   * Iterate over all transitions excluding the <i>NoOp</i> transition, i.e.,
   * the transition that causes no change on the transition system if none of
   * the transitions is applicable.
   */
  @Override
  public Iterator<Transition> iterator() {
    return Collections.unmodifiableCollection(transitions).iterator();
  }

  /**
   * @return
   */
  public int size() {
    return transitions.size();
  }

  /**
   * @return
   */
  public Collection<Formula> noOps() {
    return noOpConditions;
  }

  /**
   * Get the variables of the specified transition.
   * 
   * @param transition
   * @return
   */
  public Collection<LeafExpression> variables(final StateChanger transition) {
    if (variableMap.containsKey(transition))
      return Collections.unmodifiableCollection(variableMap.get(transition));
    else
      return Collections.emptyList();
  }

  /**
   * Get the variables of all transitions other than the specified one.
   * 
   * @param transition
   * @return
   */
  public Collection<LeafExpression> otherVariables(final StateChanger transition) {
    final Collection<LeafExpression> otherVars = new ArrayList<>();
    for (final StateChanger t : variableMap.keySet()) {
      if (t == transition)
        continue;
      otherVars.addAll(variableMap.get(t));
    }
    return otherVars;
  }

  // public Formula standstillPremise() {
  // // initStandstill();
  // return standstill.premise();
  // }

  // public Set<Formula> allConditions(Collection<Property> properties) {
  // Set<Formula> allConditions = new HashSet<>(conditions());
  // for (Property p : properties) {
  // if (p instanceof Invariant)
  // allConditions.addAll(p.negate().conditions());
  // else
  // allConditions.addAll(p.conditions());
  //
  // allConditions.addAll(p.decls());
  // allConditions.addAll(p.injectivities());
  // }
  // return Collections.unmodifiableSet(allConditions);
  // }

  // /**
  // * Returns a formula for the transition relation that can be used to check
  // * whether the given (undesired) properties are reachable.
  // *
  // * @param badProperties
  // *
  // * @return
  // */
  // public Formula reachability(Collection<BadProperty> badProperties) {
  // if (badProperties == null || badProperties.isEmpty())
  // return formula();
  // Formula[] ts = new Formula[transitions.size() + 2];
  // Formula[] standstillSubPremises = new Formula[transitions.size()];
  // int i = 0;
  // for (Transition t : transitions) {
  // ts[i] = t.formula();
  // standstillSubPremises[i] = t.premise();
  // i += 1;
  // }
  // assert i == transitions.size();
  // // standstillPremise = Formula.or(standstillSubPremises).not();
  // // ts[i] = standstillPremise.implies(standstillConsequent);
  // int j = 0;
  // Formula[] ps = new Formula[badProperties.size()];
  // for (Property property : badProperties) {
  // ps[j] = property.formula();
  // j += 1;
  // }
  // i += 1;
  // ts[i] = Formula.or(ps);
  // return Formula.and(ts);
  // }

  // /**
  // *
  // */
  // private void initStandstill() {
  // if (standstill == null) {
  // standstill = builder.toTransition();
  // add(standstill);
  // sealed = true;
  // }
  // assert sealed;
  // }

  // /**
  // * @return
  // */
  // private Formula buildStandstillPremise() {
  // Formula[] standstillSubPremises = new Formula[transitions.size()];
  // int i = 0;
  // for (Transition t : transitions) {
  // standstillSubPremises[i] = t.premise();
  // i += 1;
  // }
  // assert i == transitions.size();
  // return Formula.or(standstillSubPremises).not();
  // }
}
