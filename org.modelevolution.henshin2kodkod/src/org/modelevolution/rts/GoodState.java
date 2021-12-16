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
package org.modelevolution.rts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import kodkod.ast.Formula;
import kodkod.ast.LeafExpression;
import kodkod.ast.Relation;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class GoodState extends AbstractReachabilityProperty {

  public static final String PREFIX = "reach_";
  private String name;
  private Collection<Formula> coreConditions;
  private Collection<Formula> injectivities;
  private Formula formula;
  private boolean isNegated;
  private ArrayList<Formula> constraints;

  /**
   * @param variables
   * @param decls
   */
  protected GoodState(String name, Collection<LeafExpression> variables, Collection<Formula> decls,
      Collection<Formula> coreConditions, Collection<Formula> injectivity) {
    super(variables, decls);
    this.name = name;
    this.coreConditions = new ArrayList<>(coreConditions);
    this.injectivities = injectivity;
    this.formula = declarations().and(Formula.and(coreConditions)).and(Formula.and(injectivity));
    this.isNegated = false;
    this.constraints = new ArrayList<>(decls.size() + injectivities.size());
    constraints.addAll(decls);
    constraints.addAll(injectivities);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.henshin2kodkod.Property#formula()
   */
  @Override
  public final Formula formula() {
    return formula;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.verification.Property#name()
   */
  @Override
  public String name() {
    return name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.verification.AbstractProperty#toString()
   */
  @Override
  public String toString() {
    return new StringBuilder("REACHABLE ").append(name).append(": ").append(formula).append("\n")
                                          .toString();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.verification.Property#negate()
   */
  @Override
  public Property negate() {
    isNegated = !isNegated;
    Formula formula = Formula.and(coreConditions).not();
    coreConditions.clear();
    coreConditions.add(formula);
    return this;
  }

  /**
   * @return the isNegated
   */
  @Override
  public boolean isNegated() {
    return isNegated;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.verification.Property#conditions()
   */
  @Override
  public Collection<Formula> conditions() {
    return Collections.unmodifiableCollection(coreConditions);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.verification.Property#injectivities()
   */
  @Override
  public Collection<Formula> injectivities() {
    return injectivities;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.verification.Property#constraints()
   */
  @Override
  public Collection<Formula> constraints() {
    return Collections.unmodifiableCollection(constraints);
  }
}
