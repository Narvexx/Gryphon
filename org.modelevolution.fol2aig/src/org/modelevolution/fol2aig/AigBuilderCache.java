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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import kodkod.engine.bool.BooleanConstant;
import kodkod.engine.fol2sat.BoolTranslation;

import org.modelevolution.aig.builders.AbstractAigBuilder;
import org.modelevolution.aig.builders.Activator;
import org.modelevolution.aig.builders.AigBuilder;
import org.modelevolution.aig.builders.AigBuilderFactory;
import org.modelevolution.aig.builders.InputActiviator;
import org.modelevolution.aig.builders.InputBuilder;
import org.modelevolution.aig.builders.LatchBuilder;
import org.modelevolution.emf2rel.Signature;
import org.modelevolution.rts.Loop;
import org.modelevolution.rts.Property;
import org.modelevolution.rts.StateChanger;
import org.modelevolution.rts.Transition;
import org.modelevolution.rts.TransitionRelation;

/**
 * @author Sebastian Gabmeyer
 * 
 */
final class AigBuilderCache {

  private final Map<Integer, AigBuilder> internalCache;
  private final InputCache inputCache;
  private final LatchCache latchCache;
  private final Map<StateChanger, AigBuilder> activators;
  /*
   * Note: Currently it makes no sense to distinguish loopActivators from
   * transitionActivators.
   */
  // private final Map<Loop, InputActiviator<Loop>> loopActivators;
  /**
   * TODO: Loop latches remember whether a loop was previously active or not;
   * their inputs are the conjunction of: the loop's premise, the parent's
   * premise, and the loop's and the parent's activator+deactivators. It is
   * initially set to false.
   */
  private final Map<Loop, LatchBuilder> loopLatches;
  private final AigBuilderFactory builderFactory;

  /**
   * @param builderFactory
   * @param inputCache
   * @param latchCache
   * @param activators
   */
  private AigBuilderCache(AigBuilderFactory builderFactory, final InputCache inputCache,
      final LatchCache latchCache, Map<StateChanger, AigBuilder> activators) {
    this.builderFactory = builderFactory;
    this.internalCache = new HashMap<>();
    this.inputCache = inputCache;
    this.latchCache = latchCache;
    add(BooleanConstant.FALSE.label(), AigBuilder.FALSE);
    add(BooleanConstant.TRUE.label(), AigBuilder.TRUE);
    this.activators = activators;
    // this.loopActivators = new IdentityHashMap<>();
    this.loopLatches = new IdentityHashMap<>();
  }

  void add(int label, AigBuilder builder) {
    // if (label < 0) throw new IllegalArgumentException("label < 0.");
    if (builder == null)
      throw new NullPointerException("builder == null.");
    internalCache.put(label, builder);
  }

  AigBuilder get(int label) {
    if (inputCache.contains(label))
      return inputCache.get(label);
    if (latchCache.contains(label))
      return latchCache.get(label);
    if (internalCache.containsKey(label))
      return internalCache.get(label);
    return null;
  }

  /**
   * @param label
   * @return
   */
  boolean contains(int label) {
    return inputCache.contains(label) || latchCache.contains(label)
        || internalCache.containsKey(label);
  }

  /**
   * @param label
   * @return
   */
  boolean isPreLabel(int label) {
    return latchCache.containsPreLabel(label);
  }

  /**
   * @param label
   * @return
   */
  boolean isPostLabel(int label) {
    return latchCache.containsPostLabel(label);
  }

  /**
   * @param label
   * @return
   */
  boolean isLatch(int label) {
    return latchCache.contains(label);
  }

  /**
   * Checks if for the specified label there exists both an input builder and a
   * corresponding latch builder.
   * 
   * @param label
   * @return
   */
  boolean hasMemoryLatch(int label) {
    return inputCache.contains(label) && latchCache.contains(label);
  }

  // void addThenCondition(final int latchLabel, AigBuilder condition) {
  // assert latchCache.contains(latchLabel);
  // latchCache.addThenCondition(latchLabel, condition);
  // }

  LatchBuilder getLatch(int label) {
    return latchCache.get(label);
  }

  AigBuilder getInput(int label) {
    return inputCache.get(label);
  }

  /**
   * @param s
   * @return
   */
  public AigBuilder activator(StateChanger s) {
    if (s == null)
      throw new NullPointerException();
    // if (s instanceof Transition)
    return activators.get(s);
    // else if (s instanceof Loop)
    // return loopActivators.get(s);
    // else
    // throw new IllegalArgumentException();
  }

  public AigBuilder deactivator(final StateChanger stateChanger) {
    // final Activator<?> act = activator(s);
    final Collection<AigBuilder> deactivators = new ArrayList<>(activators.size());
    for (final StateChanger s : activators.keySet()) {
      if (s == stateChanger)
        continue;
      final AigBuilder activatorOfT = activators.get(s);
      assert activatorOfT != null;
      deactivators.add(activatorOfT.not());
    }
    // if (s instanceof Loop) {
    // for (final Loop l : loopActivators.keySet()) {
    // if (l == s)
    // continue;
    // final Activator<?> activatorOfL = loopActivators.get(l);
    // assert activatorOfL != null;
    // deactivators.add(activatorOfL.not());
    // }
    // }
    return builderFactory.andGate(deactivators);
  }

  /**
   * @param sig
   * @param transitions
   * @param properties
   * @param circuit
   * @param builderFactory
   * @return
   */
  public static AigBuilderCache create(Signature sig, TransitionRelation transitions,
      List<Property> properties, BoolTranslation circuit, AigBuilderFactory builderFactory) {
    final InputCache inputCache = InputCache.create(transitions, properties, circuit,
                                                    builderFactory);
    final LatchCache latchCache = LatchCache.create(sig, transitions, circuit, builderFactory);

    final Map<StateChanger, AigBuilder> activators = new IdentityHashMap<>(transitions.size());
    for (final Transition t : transitions) {
      activators.put(t, inputCache.cache(builderFactory.createInputActivator(t)));
      for (final Loop l : t.loops())
        activators.put(l, inputCache.cache(builderFactory.createInputActivator(l)));
    }

    final AigBuilderCache cache = new AigBuilderCache(builderFactory, inputCache, latchCache,
                                                      activators);
    return cache;
  }

  /**
   * @return
   */
  public Collection<LatchBuilder> latches() {
    return latchCache.latches();
  }

  /**
   * @return
   */
  public Collection<AigBuilder> inputs() {
    return inputCache.inputs();
  }

  /**
   * @return
   */
  public AigBuilder allDeactivator() {
    return deactivator(null);
  }
}
