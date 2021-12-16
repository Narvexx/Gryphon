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
package org.modelevolution.fol2aig;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import kodkod.engine.bool.Operator.Nary;
import kodkod.util.ints.IntSet;
import kodkodmod.verification.StateVector;

import org.modelevolution.aig.AigExpr;
import org.modelevolution.aig.AigFactory;
import org.modelevolution.aig.builders.AigBuilder;

/**
 * @author Sebastian Gabmeyer
 * 
 */
@Deprecated
class TranslationInfo {
  /**
   * @author Sebastian Gabmeyer
   * 
   */
  enum AigContainer {
    COMPOUND, NOT, VAR,
  }

  /**
   * The <code>activiationLiteral</code> <i>v_i</i> and the
   * <code>activationConstraint</code> <i>XOR(v_1,...,v_n)</i>are added to each
   * transition such that the formula reads
   * <code>v_i => (antecedent=>consequent) & XOR(v_1,...,v_n)</code>. Both
   * default to {@link AbstractAigExpr#TRUE}, the identity element of a conjunction.
   * 
   * @see Bool2AigTranslator#translate()
   * 
   *      TODO: Not implemented yet
   */
  private AigExpr activationLiteral = AigExpr.TRUE;
  private AigExpr activationConstraint = AigExpr.TRUE;
  private final AigExpr antecedent;
  private AigBuilder commonConsequent;
  // private final IntSet latchIndexes = new IntTreeSet();
  private Map<Integer, Set<AigExpr>> latchInfo;
  // private AigContainer immediateContainer;
  private final StateVector states;
  private AigFactory aig;

  /**
   * @param states
   *          TODO
   * @param antecedent
   */
  TranslationInfo(StateVector states, AigExpr antecedent) {
    this.states = states;
    this.antecedent = antecedent;
  }

  StateVector states() {
    return states;
  }

  AigExpr antecedent() {
    return antecedent;
  }

  // void antecedent(AigExpr antecedent) {
  // this.antecedent = antecedent;
  // }

  AigBuilder commonConsequent() {
    return commonConsequent;
  }

  /**
   * @param builder
   *          a common consequent
   */
  void commonConsequent(AigBuilder builder) {
    // if(consequent == AigExpr.TRUE || consequent == AigExpr.FALSE)
    // return false;
    commonConsequent = builder;
  }

  // IntSet latchIndexes() {
  // return latchIndexes;
  // }
  //
  // boolean addLatchIndex(Integer index) {
  // return latchIndexes.add(index);
  // }

  // /**
  // * @param not
  // */
  // void immediateContainer(AigContainer container) {
  // this.immediateContainer = container;
  // }
  //
  // AigContainer immediateContainer() {
  // return immediateContainer;
  // }

  // /**
  // * @param label
  // * @return
  // */
  // boolean isNextStateVar(int label) {
  // return states.isNextStateVar(label);
  // }

  int merge(int var, final Nary op, final Collection<TranslationInfo> subinfos) {
    if (var < 1) throw new IndexOutOfBoundsException("Reason: var < 1");
    if (op == null || subinfos == null)
      throw new NullPointerException("Reason: op == null || subinfos == null");
    if (subinfos.isEmpty()) return var;

    latchInfo = new HashMap<>(guessCapacity(subinfos));
    // for(final TranslationInfo subinfo : subinfos) {
    // if(subinfo.latchInfo == null || subinfo.latchInfo.isEmpty()) {
    // // build gate for cc's
    // AigExpr cc = aig.gate(op, subinfo.commonConsequent());
    // var = aig.nextVar();
    // // create a map, latchMap, from latchIndex to its partial nextState
    // function (AigExpr)
    // // foreach latch-index: (1) create container (list) for latch in latchMap
    // if latch not present in latchMap,
    // // (2) add ccExpr Op latchExpr to above container (list), where latchExpr
    // is either AigExpr.TRUE or AigExpr.FALS
    // for(final IntIterator it = subinfo.latchIndexes().iterator();
    // it.hasNext(); ) {
    // final int latch = it.next(); // next state latch index; latch > 0 ->
    // TRUE, latch <= 0 -> FALSE
    // AigExpr latchpart = aig.gate(op, (latch > 0 ? AigExpr.TRUE :
    // AigExpr.FALSE), cc);
    // if(latchInfo.containsKey(abs(latch))) {
    // latchInfo.get(abs(latch)).add(latchpart);
    // } else {
    // Set<AigExpr> parts = new HashSet<>();
    // parts.add(latchpart);
    // latchInfo.put(abs(latch), parts);
    // }
    // var = aig.nextVar();
    // }
    // }
    // }
    return var;
  }

  /**
   * @return
   */
  private IntSet latchIndexes() {
    throw new UnsupportedOperationException(); // TODO: Implement this!
  }

  /**
   * Guesses the capacity of the this.latchInfo map based on the number of
   * latchIndexes found in the first subinfo multiplied by the number of
   * subinfos divided by 2, i.e., the assumption is that half of the subinfos
   * contain the same latch indexes. We assume a default load factor for maps of
   * 0.75, hence we divide the multiplication result by 3 multiplied by 4 and
   * divided by 2 again to account for the above assumption:
   * 
   * <pre>
   * (subinfos.size() * first.latchIndexes.size()) / 3) * 4 / 2
   * </pre>
   * 
   * which simplifies to
   * 
   * <pre>
   * (subinfos.size() * first.latchIndexes.size()) / 3) * 2
   * </pre>
   * 
   * @param subinfos
   * @return
   * @requires subinfous != null && !subinfos.isEmpty()
   */
  private int guessCapacity(final Collection<TranslationInfo> subinfos) {
    final int DEFAULT_MAP_CAPACITY = 16;
    final TranslationInfo first = subinfos.iterator().next();
    final int initialCapacity =
        (first != null ? ((subinfos.size() /* * first.latchIndexes.size() */) / 3) * 2
                      : DEFAULT_MAP_CAPACITY);
    return initialCapacity;
  }

  /**
   * @param label
   * @return
   */
  public boolean isNextStateVar(int label) {
    throw new UnsupportedOperationException(); // TODO: Implement this!
  }

  /**
   * @param label
   */
  public void addLatchIndex(int label) {
    throw new UnsupportedOperationException(); // TODO: Implement this!
  }
}
