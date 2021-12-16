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
package org.modelevolution.rts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.modelevolution.aig.builders.Activator;

import kodkod.ast.Formula;
import kodkod.ast.LeafExpression;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public final class Transition implements StateChanger {
  private final Formula premise;
  private final Formula consequent;
  private final Formula featureMultiplicities;
  private final Formula danglingEdgeCondition;
  private final Formula injectivity;
  private final Collection<LeafExpression> variables;
  private final Formula declarations;
  // private final Formula formula;
  private final String name;
  private final Collection<Loop> loops;
  private final Collection<Formula> premises;
  private final Collection<Formula> consequents;
  private final Collection<Formula> decls;
  private final Collection<Formula> injectivities;
  private final Collection<Formula> decs;
  private final Collection<Formula> multiplicities;
  private final Collection<Formula> constraints;
  private Activator<Transition> activator;

  /**
   * @param name
   * @param variables
   * @param premises
   * @param consequents
   * @param decls
   * @param injectivities
   * @param decs
   * @param multiplicities
   * @param loops
   */
  public Transition(String name, Collection<LeafExpression> variables,
      Collection<Formula> premises, Collection<Formula> consequents, Collection<Formula> decls,
      Collection<Formula> injectivities, Collection<Formula> decs,
      Collection<Formula> multiplicities, Collection<Loop> loops) {
    this.name = name;
    this.variables = variables;
    this.premises = premises;
    this.consequents = consequents;
    int size = decls.size() + injectivities.size() + decs.size() + multiplicities.size();
    this.constraints = new ArrayList<>(size);
    constraints.addAll(decls);
    constraints.addAll(injectivities);
    constraints.addAll(decs);
    constraints.addAll(multiplicities);
    this.decls = decls;
    this.injectivities = injectivities;
    this.decs = decs;
    this.multiplicities = multiplicities;

    this.premise = Formula.and(premises);
    this.consequent = Formula.and(consequents);
    this.declarations = Formula.and(decls);
    this.danglingEdgeCondition = Formula.and(decs);
    this.featureMultiplicities = Formula.and(multiplicities);
    this.injectivity = Formula.and(injectivities);
    // this.formula = Formula.and(constraints).and(premise).implies(consequent);
    this.loops = loops;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.verification.StateChanger#variables()
   */
  @Override
  public Collection<LeafExpression> variables() {
    return variables;
  }

  // /**
  // * @return the formula
  // */
  // @Override
  // public Formula formula() {
  // return formula;
  // }

  /**
   * @return the ifCondition
   */
  public Formula premise() {
    return premise;
  }

  /**
   * @return the thenConsequent
   */
  public Formula consequent() {
    return consequent;
  }

  /**
   * @return the featureMultiplicities
   */
  public Formula multiplicity() {
    return featureMultiplicities;
  }

  /**
   * @return the dec
   */
  public Formula danglingEdgeCondition() {
    return danglingEdgeCondition;
  }

  /**
   * @return the injectivity
   */
  public Formula injectivityCondition() {
    return injectivity;
  }

  /**
   * @return the variableMultiplicities
   */
  public Formula decl() {
    return declarations;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.verification.StateChanger#loops()
   */
  @Override
  public Collection<Loop> loops() {
    return Collections.unmodifiableCollection(loops);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.verification.StateChanger#name()
   */
  @Override
  public String name() {
    return name;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder(name);
    sb.append("(").append(removeBrackets(variables.toString())).append(")")
      .append(System.lineSeparator()).append("\t").append("MATCH ").append(declarations)
      .append(" SUCH THAT").append(System.lineSeparator()).append("\t").append("IF (")
      .append(premise);

    if (danglingEdgeCondition != Formula.TRUE)
      sb.append(" && ").append(System.lineSeparator()).append("\t    ")
        .append(danglingEdgeCondition).append("\t// Dangling Edge Condition ");
    if (injectivity != Formula.TRUE)
      sb.append(" && ").append(System.lineSeparator()).append("\t    ").append(injectivity)
        .append(")\t// Injectivity Condition");

    sb.append(System.lineSeparator()).append("\tTHEN ").append(consequent);
    return sb.toString();
  }

  /**
   * @param string
   * @return
   */
  private String removeBrackets(String string) {
    return string.substring(1, string.length() - 1);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.verification.StateChanger#premises()
   */
  @Override
  public Collection<Formula> premises() {
    return Collections.unmodifiableCollection(premises);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.verification.StateChanger#consequents()
   */
  @Override
  public Collection<Formula> consequents() {
    return Collections.unmodifiableCollection(consequents);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.verification.StateChanger#constraints()
   */
  @Override
  public Collection<Formula> constraints() {
    return Collections.unmodifiableCollection(constraints);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.verification.StateChanger#decls()
   */
  @Override
  public Collection<Formula> declarations() {
    return Collections.unmodifiableCollection(decls);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.verification.StateChanger#injectivities()
   */
  @Override
  public Collection<Formula> injectivities() {
    return Collections.unmodifiableCollection(injectivities);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.verification.StateChanger#decs()
   */
  @Override
  public Collection<Formula> danglingEdgeConditions() {
    return Collections.unmodifiableCollection(decs);
  }

  public Collection<Formula> multiplicities() {
    return Collections.unmodifiableCollection(multiplicities);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.verification.StateChanger#hasLoops()
   */
  @Override
  public boolean hasLoops() {
    return loops.size() > 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.rts.StateChanger#getActivator()
   */
  @Override
  public Activator<? extends StateChanger> getActivator() {
    return activator;
  }
}
