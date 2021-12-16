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
package org.modelevolution.gts2rts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import kodkod.ast.Decl;
import kodkod.ast.Formula;

/**
 * @author Sebastian Gabmeyer
 * 
 */
final class PremiseCollector {
  private final PremiseCollector base;
  // private final List<NodeInfo<Formula>> infos = new ArrayList<>();
  private final Collection<Formula> conditions;
  private boolean modified;
  private Formula premise;
  private Collection<Formula> declarations;

  // private boolean modifiedDecls;

  /**
   * @param infos
   * @param conditions
   */
  PremiseCollector() {
    this(null);
  }

  /**
   * Copy Constructor.
   */
  private PremiseCollector(final PremiseCollector base) {
    this.base = base;
    this.conditions = new ArrayList<>();
    this.modified = false;
    // this.modifiedDecls = false;
    this.premise = Formula.FALSE;
    this.declarations = new ArrayList<>();
  }

  void addPremise(final Formula premise) {
    if (premise != Formula.TRUE) {
      modified |= conditions.add(premise);
    }
  }

  Formula toFormula() {
    if (modified/* || modifiedDecls */) {
      // updateDecls();
      premise = Formula.and(conditions).and(Formula.and(declarations));
      if (base != null)
        premise = base.toFormula().and(premise);
      modified = false;
    }
    return premise;
  }

  Collection<Formula> declarations() {
    return Collections.unmodifiableCollection(declarations);
  }

  void addDeclaration(final NodeInfo<Formula> info) {
    declarations.add(info.decl());
    
//    for (Decl d : VariabilityRuleTranslator.variableDecls) {
//    	declarations.add(d.expression().one());
//    }
    // modifiedDecls = infos.add(info);
  }

  Collection<Formula> conditions() {
    return Collections.unmodifiableCollection(conditions);
  }

  // /**
  // * Calls {@link #declarations()} for its side effect of updating the set of
  // * {@link #conditions}.
  // */
  // private void updateDecls() {
  // declarations();
  // }

  // Formula declaration() {
  // if (modifiedDecls) {
  // declsFormula = Formula.and(declFormulas());
  // }
  // return declsFormula;
  // }

  // @Deprecated
  // PremiseCollector loopCollector() {
  // return new PremiseCollector(this);
  // }

  // /**
  // * @return
  // */
  // @Deprecated
  // List<NodeInfo> infos() {
  // return Collections.unmodifiableList(infos);
  // }

  // /**
  // * @param infos
  // */
  // @Deprecated
  // void addAll(final List<NodeInfo> infos) {
  // this.infos = (infos);
  // modifiedDecls = true;
  // }

}
