/*
 * org.modelevolution.fol2aig -- Copyright (c) 2015-present, Sebastian Gabmeyer
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

import static org.modelevolution.aig.AigExpr.FALSE;
import static org.modelevolution.aig.AigExpr.TRUE;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import kodkod.ast.LeafExpression;
import kodkod.ast.Relation;
import kodkod.engine.fol2sat.BoolTranslation;
import kodkod.util.ints.IntIterator;
import kodkod.util.ints.IntSet;

import org.modelevolution.aig.builders.AigBuilder;
import org.modelevolution.aig.builders.AigBuilderFactory;
import org.modelevolution.aig.builders.LatchBuilder;
import org.modelevolution.emf2rel.Signature;
import org.modelevolution.emf2rel.StateRelation;
import org.modelevolution.rts.StateChanger;
import org.modelevolution.rts.TransitionRelation;

/**
 * @author Sebastian Gabmeyer
 * 
 */
final class LatchCache {

  

  static LatchCache create(Signature sig, TransitionRelation transitions,
      BoolTranslation circuit, AigBuilderFactory builderFactory) {
    final LatchCache latchCache = new LatchCache(circuit.maxVariable());

    /* Create a latch for each var label associated with a StateRelation */
    for (StateRelation state : sig.states()) {
      final Relation preState = state.preState();
      final IntSet preStateLabels = circuit.labels(preState);

      /*
       * The StateRelation is static as no labels have been allocated for this
       * StateRelation; hence, we do not allocate any latches
       */
      if (preStateLabels.isEmpty())
        continue;

      /*
       * Clearly the preStateLabels and the postStateLabels
       * (=circuit.labels(state.postState())) [see second condition] need to be
       * of equal size, for they have identical upper bounds. Also the number of
       * atoms in the upper bound of a relation define the number of var labels
       * allocated for a relation in the circuit. Hence, also the number of
       * variables in the upper bound needs to be equal to the number of
       * preStateLabels. Moreover, a label in the preStateLabels at position j
       * corresponds to a variable in the upper bound at position j. In this
       * way, we deduce whether the label at position j is an initial state.
       */
      if (preStateLabels.size() != sig.bounds().upperBound(preState).indexView().size()
          || preStateLabels.size() != circuit.labels(state.postState()).size())
        throw new AssertionError();

      final IntIterator uBoundIndexIter = sig.bounds().upperBound(preState).indexView().iterator();
      final IntIterator preIter = preStateLabels.iterator();
      final IntIterator postIter = circuit.labels(state.postState()).iterator();

      while (preIter.hasNext()) {
        final Integer index = uBoundIndexIter.next();
        final Integer preLabel = preIter.next();
        final Integer postLabel = postIter.next();
        final boolean isInitial = sig.isInitial(preState, index);
        final LatchBuilder latch = builderFactory.createLatchBuilder(preLabel, isInitial ? TRUE : FALSE);
        if (!state.isModified()) {
          if (isInitial)
            latch.inputs(AigBuilder.TRUE);
          else
            latch.inputs(AigBuilder.FALSE);
          latch.setHasFixedNextState(true);
        }
        latchCache.cache(preLabel, postLabel, latch);
      }
    }

    /*
     * FIXME: Loop Handling: 1) register/create a latch for each input variable of a
     * transition that has loops
     */
    for (final StateChanger t : transitions) {
      if (t.hasLoops()) {
        for (final LeafExpression var : t.variables()) {
          final IntIterator it = circuit.labels(var).iterator();
          while (it.hasNext()) {
            final Integer label = it.next();
            final LatchBuilder inputMemoryLatch = builderFactory.createLatchBuilder(label, FALSE);
            latchCache.cache(label, label, inputMemoryLatch);
          }
        }

        /*
         * Note the commented code has been moved into the
         * AigBuilderCache.create(...) method
         */
        // for (final Loop loop : t.loops()) {
        // final LatchedActivator<Loop> activator =
        // builderFactory.createLatchedActivator(loop);
        // latchCache.loopActivators.put(loop, activator);
        // }
      }
    }

    return latchCache;
  }

  private int maxLabel = Integer.MIN_VALUE;
  private final Map<Integer, LatchBuilder> preCache;
  private final Map<Integer, LatchBuilder> postCache;


  /**
   * @param size
   * 
   */
  private LatchCache(int size) {
    preCache = new TreeMap<>();
    postCache = new HashMap<>(size);
  }

  private void cache(final int preLabel, final int postLabel, final LatchBuilder builder) {
    if (preLabel < 0 || postLabel < 0)
      throw new IllegalArgumentException();
    if (builder == null)
      throw new NullPointerException();
    preCache.put(preLabel, builder);
    postCache.put(postLabel, builder);
    assert preLabel <= postLabel;
    maxLabel = maxLabel < postLabel ? postLabel : maxLabel;
  }

  LatchBuilder get(int label) {
    if (preCache.containsKey(label))
      return preCache.get(label);
    if (postCache.containsKey(label))
      return postCache.get(label);
    throw new AssertionError("No label " + label + " found.");
  }

  /**
   * @param label
   * @return
   */
  boolean containsPreLabel(int label) {
    return preCache.containsKey(label);
  }

  /**
   * @return
   */
  Collection<LatchBuilder> latches() {
    return Collections.unmodifiableCollection(preCache.values());
  }

  /**
   * @param label
   * @return
   */
  public boolean contains(int label) {
    return preCache.containsKey(label) || postCache.containsKey(label);
  }

  /**
   * @param label
   * @return
   */
  public boolean containsPostLabel(int label) {
    return postCache.containsKey(label);
  }

  // /**
  // * @param s
  // * @return
  // */
  // public Collection<Activator<?>> otherActivors(final StateChanger s) {
  // if (s == null)
  // return Collections.<Activator<?>>
  // unmodifiableCollection(transitionActivators.values());
  // final Map<Transition, Activator<?>> otherActivators = new
  // HashMap<Transition, Activator<?>>(
  // transitionActivators);
  // for (final StateChanger l : s.loops()) {
  // otherActivators.remove(l);
  // }
  // return Collections.unmodifiableCollection(otherActivators.values());
  // }

  // /**
  // * @param loop
  // * @param parent
  // * @return
  // */
  // public Collection<AigBuilder> otherLoopActivators(final StateChanger loop,
  // final StateChanger parent) {
  // if (loop == null)
  // throw new NullPointerException();
  // if (parent == null)
  // throw new NullPointerException();
  //
  // final Collection<Loop> loops = parent.loops();
  // final Collection<AigBuilder> otherActivators = new ArrayList<>(loops.size()
  // - 1);
  // for (final StateChanger s : loops) {
  // if (s == loop)
  // continue;
  // final AigBuilder activator = activator(s);
  // if (activator != null)
  // otherActivators.add(activator);
  // }
  // return otherActivators;
  // }

  // /**
  // * @param t
  // * @return
  // */
  // public Collection<AigBuilder> activatorsOf(StateChanger t) {
  // if (t == null)
  // throw new NullPointerException();
  //
  // final Collection<Loop> loops = t.loops();
  // final Collection<AigBuilder> activators = new ArrayList<>(loops.size());
  // for (final StateChanger l : loops)
  // activators.add(transitionActivators.get(l));
  // return activators;
  // }
}
