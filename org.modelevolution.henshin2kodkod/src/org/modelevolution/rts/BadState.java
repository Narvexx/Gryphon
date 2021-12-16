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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
public final class BadState extends AbstractReachabilityProperty {
  public static final String PREFIX = "bad_";

  private final Formula formula;
  private final String name;
  private final Collection<Formula> coreConditions;
  private final Collection<Formula> injectivities;
  private boolean isNegated;
  private final Collection<Formula> constraints;

  BadState(String name, Collection<LeafExpression> variables, Collection<Formula> decls,
      Collection<Formula> coreConditions, Collection<Formula> injectivities) {
    super(variables, decls);
    this.name = name;
    this.coreConditions = new ArrayList<>(coreConditions);
    this.injectivities = injectivities;
    this.formula = buildFormula(decls, coreConditions, injectivities);
    this.isNegated = false;
    this.constraints = new ArrayList<>(decls.size() + injectivities.size());
    constraints.addAll(decls);
    constraints.addAll(injectivities);
  }

  /**
   * @param decls
   * @param coreConditions
   * @param injectivities
   * @return
   */
  private Formula buildFormula(Collection<Formula> decls, Collection<Formula> coreConditions,
      Collection<Formula> injectivities) {
    final Formula core = Formula.and(coreConditions);
    if (decls.isEmpty()) {
      if (injectivities.isEmpty())
        return core;
      else
        return core.and(Formula.and(injectivities));
    } else {
      final Formula declarations = Formula.and(decls);
      if (injectivities.isEmpty())
        return declarations.and(core);
      else
        return declarations.and(core).and(Formula.and(injectivities));
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.henshin2kodkod.Property#formula()
   */
  @Override
  public Formula formula() {
    return formula;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.verification.AbstractProperty#toString()
   */
  @Override
  public String toString() {
    return new StringBuilder("PROHIBIT ").append(name).append(": ").append(formula).append("\n")
                                         .toString();
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
   * @see org.modelevolution.verification.Property#negate()
   */
  @Override
  public Property negate() {
    this.isNegated = !isNegated;
    Formula formula = Formula.and(coreConditions).not();
    coreConditions.clear();
    coreConditions.add(formula);
    return this;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.verification.Property#isNegated()
   */
  @Override
  public boolean isNegated() {
    return this.isNegated;
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
    return Collections.unmodifiableCollection(injectivities);
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
