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
import java.util.List;

import kodkod.ast.Formula;

public final class RuleTranslation {
  // private final List<NodeInfo> infos;
  private final EffectCollector effectCollector;
  private final PremiseCollector premiseCollector;
  private final InjectivityConditionBuilder injBuilder;
  private final DanglingEdgeConditionBuilder decBuilder;
  private final List<RuleTranslation> multis;

  /**
   * @param premiseCollector
   * @param effectCollector
   * @param injectivity
   * @param decBuilder
   */
  RuleTranslation(final PremiseCollector premiseCollector, final EffectCollector effectCollector,
      final InjectivityConditionBuilder injBuilder, final DanglingEdgeConditionBuilder decBuilder) {
    // throw new
    // UnsupportedOperationException("Add handling of injectivity constraint.");
    this.premiseCollector = premiseCollector;
    this.effectCollector = effectCollector;
    this.injBuilder = injBuilder;
    this.decBuilder = decBuilder;
    this.multis = new ArrayList<>();
    // infos = new ArrayList<>(premiseCollector.infos().size() +
    // effectCollector.infos().size());
    // infos.addAll(premiseCollector.infos());
    // infos.addAll(effectCollector.infos());
  }

  // @Deprecated
  // final List<NodeInfo> infos() {
  // return infos;
  // }

  // final PremiseCollector precondition() {
  // return premiseCollection;
  // }

  final EffectCollector effectCollector() {
    return effectCollector;
  }

  // public final Formula declarations() {
  // return premiseCollection.declaration();
  // }

  public final Collection<Formula> declarations() {
    return Collections.unmodifiableCollection(premiseCollector.declarations());
  }

  // public final Formula premise() {
  // return premiseCollection.toFormula();
  // }

  // public final Formula consequent() {
  // return effectCollection.toFormula();
  // }

  // /**
  // * @return
  // */
  // List<NodeInfo> premiseInfos() {
  // return premiseCollection.infos();
  // }

  public Collection<Formula> premises() {
    return premiseCollector.conditions();
  }

  public Collection<Formula> effects() {
    return effectCollector.conditions();
  }

  /**
   * @param and
   */
  void addPremise(Formula premise) {
    this.premiseCollector.addPremise(premise);
  }

  InjectivityConditionBuilder injectivityBuilder() {
    return injBuilder;
  }

  DanglingEdgeConditionBuilder decBuilder() {
    return decBuilder;
  }

  /**
   * @return
   */
  public Collection<Formula> injectivityConditions() {
    return Collections.unmodifiableCollection(injBuilder.conditions());
  }

  public Collection<Formula> danglingEdgeConditions() {
    return Collections.unmodifiableCollection(decBuilder.conditions());
  }

  void addMulti(RuleTranslation multiTranslation) {
    multis.add(multiTranslation);
  }
  // /**
  // * @return
  // */
  // public Set<StateRelation> affectedStates() {
  // return this.effectCollection.affected();
  // }
}