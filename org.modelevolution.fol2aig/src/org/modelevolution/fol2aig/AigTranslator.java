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

import static kodkod.util.nodes.AnnotatedNode.annotateRoots;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.text.StyledEditorKit.BoldAction;

import kodkod.ast.Formula;
import kodkod.ast.LeafExpression;
import kodkod.ast.Relation;
import kodkod.engine.bool.BooleanConstant;
import kodkod.engine.bool.BooleanValue;
import kodkod.engine.config.ExtOptions;
import kodkod.engine.fol2sat.BoolTranslation;
import kodkod.engine.fol2sat.BoolTranslator;
import kodkod.engine.fol2sat.CustomMemoryLogger;
import kodkod.util.ints.IntIterator;
import kodkod.util.ints.IntSet;
import kodkod.util.nodes.AnnotatedNode;
import kodkod.util.nodes.Nodes;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.henshin.model.Annotation;
import org.eclipse.emf.henshin.model.Rule;
import org.modelevolution.aig.AigExpr;
import org.modelevolution.aig.AigFile;
import org.modelevolution.aig.AigLatch;
import org.modelevolution.aig.builders.AbstractAigBuilder;
import org.modelevolution.aig.builders.AigBuilder;
import org.modelevolution.aig.builders.InputBuilder;
import org.modelevolution.emf2rel.Signature;
import org.modelevolution.emf2rel.StateRelation;
import org.modelevolution.gts2rts.VariabilityRuleTranslator;
import org.modelevolution.rts.Invariant;
import org.modelevolution.rts.Property;
import org.modelevolution.rts.StateChanger;
import org.modelevolution.rts.Transition;
import org.modelevolution.rts.TransitionRelation;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public final class AigTranslator {
  // private VerificationCondition vc;
  // private Formula transitionRelation;
  // private Formula badProperty;
  // private Bounds originalBounds;
  // private AnnotatedNode<Formula> annotated;
  private BoolTranslation circuit;
  private final ExtOptions options;
  private Bool2AigTranslator bool2aigTranslator;
  private Signature sig;

  public AigTranslator(final Signature sig, ExtOptions options) {
    if (sig == null || options == null)
      throw new NullPointerException("sig == null || options == null.");
    if (sig.bitwidth() != options.bitwidth())
      throw new IllegalArgumentException("sig.bitwidth() != options.bitwidth().");
    this.options = options;
    this.sig = sig;
  }

  public AigFile translate(final TransitionRelation transitions, final List<Property> properties) {
    /*
     * For the purpose of translating the transition relation into a
     * combinational circuit we conjoin its components (if's, then's, else's)
     * together.
     */
    final Set<Formula> all = allConditions(transitions, properties);
    final AnnotatedNode<Formula> annotated = annotateRoots(Formula.and(all), all);
    assert Nodes.roots(annotated.node()).containsAll(all);
    assert all.containsAll(Nodes.roots(annotated.node()));
    // final Set<Formula> logged = collectLogFormulas(all);
    options.setLogger(new CustomMemoryLogger(annotated, sig.bounds(), all));

    circuit = new BoolTranslator(sig.bounds(), options).translate(annotated);

    /*
     * REMOVE UNSAT PROPERTIES
     * 
     * for (final Iterator<Property> propIterator = properties.iterator();
     * propIterator.hasNext();) { final Property p = propIterator.next(); for
     * (final Formula f : p.conditions()) { if (circuit.mapping(f) ==
     * BooleanConstant.FALSE) { propIterator.remove(); } } for (final Formula f
     * : p.constraints()) { if (circuit.mapping(f) == BooleanConstant.FALSE) {
     * propIterator.remove(); } } }
     */

    /*
     * FOR DEBUGGING
     * 
     * System.out.println(
     * "Relations and the primary variables associated with them:"); for
     * (Relation r : circuit.bounds().relations()) { System.out.print(r.name() +
     * ": "); IntIterator it = circuit.labels(r).iterator(); if (it.hasNext()) {
     * int literal = (int) it.next(); System.out.print(literal); while
     * (it.hasNext()) { literal = (int) it.next(); System.out.print(", " +
     * literal); } } System.out.println(); }
     */

    bool2aigTranslator = new Bool2AigTranslator(sig, options);
    AigFile translation = bool2aigTranslator.translate(circuit, transitions, properties);

    return translation;
  }
 

  /**
   * @param transitions
   * @param properties
   * @return
   */
  private Set<Formula> allConditions(TransitionRelation transitions, Collection<Property> properties) {
    Set<Formula> allConditions = new HashSet<>(transitions.conditions());
    
    // Add Presence Conditions explicitly
    allConditions.addAll(VariabilityRuleTranslator.getPresenceConditions());
    
    for (Property p : properties) {
      if (p instanceof Invariant)
        allConditions.addAll(p.negate().conditions());
      else
        allConditions.addAll(p.conditions());

      allConditions.addAll(p.decls());
      allConditions.addAll(p.injectivities());
    }
    return allConditions;
  }

  /**
   * @return
   */
  public BoolTranslation circuit() {
    return circuit;
  }

  /**
   * @param transitions
   * @param properties
   * @return
   */
  public Fol2AigProof proof(final TransitionRelation transitions,
      final Collection<Property> properties) {

    Map<LeafExpression, Map<Integer, AigExpr>> var2inputMap = new LinkedHashMap<>();
    for (final StateChanger t : transitions) {
      for (final LeafExpression var : sig.bounds().relations()) {
        final IntSet varLabels = circuit.labels(var);

        if (varLabels == null)
          throw new AssertionError("Unable to construct proof object: "
              + "No labels have been allocated for variable " + var + " in transition " + t.name()
              + ".");

        final Map<Integer, AigExpr> label2aigMap = new LinkedHashMap<>(varLabels.size());
        final IntIterator labelIterator = varLabels.iterator();

        while (labelIterator.hasNext()) {
          Integer label = labelIterator.next();
          AigExpr input = bool2aigTranslator.getAig(label);

          // if (input == null)
          // throw new AssertionError("Unable to construct proof object: "
          // + "No Aig has been allocated for var " + var + " (label: " + label
          // + ") in transition " + t.name() + ".");

          label2aigMap.put(label, input);
        }
        var2inputMap.put(var, label2aigMap);
      }
    }

    final Collection<StateRelation> states = sig.states();
    final Map<StateRelation, Map<Integer, AigExpr>> state2latchMap = new LinkedHashMap<>(
                                                                                         states.size());
    final Map<Relation, IntSet> postStateLabels = new HashMap<>(states.size());

    for (final StateRelation s : states) {
      final IntSet preLabels = circuit.labels(s.preState());
      final IntSet postLabels = circuit.labels(s.postState());
      postStateLabels.put(s.postState(), postLabels);

      if (preLabels == null || postLabels == null || preLabels.size() != postLabels.size())
        throw new AssertionError();

      Map<Integer, AigExpr> label2latchMap = new LinkedHashMap<>(preLabels.size());
      final IntIterator preIterator = preLabels.iterator();
      while (preIterator.hasNext()) {
        final int label = preIterator.next();
        AigExpr latch = bool2aigTranslator.getAig(label);

        if (latch == null)
          throw new AssertionError("Unable to construct proof object: "
              + "No Aig has been allocated for state " + s.name() + " (label: " + label + ").");
        if (!(latch instanceof AigLatch || latch == AigExpr.FALSE || latch == AigExpr.TRUE))
          throw new AssertionError("Unable to construct proof object: " + "Aig returned for state "
              + s.name() + " is not a latch expression or a constant expression.");

        label2latchMap.put(label, latch);
      }
      state2latchMap.put(s, label2latchMap);
    }

    Map<Transition, Map<Formula, BooleanValue>> transitionMap = new LinkedHashMap<>(
                                                                                    transitions.size());

    final Map<Integer, AigBuilder> bool2builderMap = new HashMap<>();
    final Map<Integer, AigExpr> bool2aigMap = new HashMap<>();

    for (final Transition t : transitions) {
      final Collection<Formula> premises = t.premises();
      final Collection<Formula> constraints = t.constraints();
      final Collection<Formula> consequents = t.consequents();
      final int size = premises.size() + constraints.size() + consequents.size();
      final Map<Formula, BooleanValue> fol2boolMap = new LinkedHashMap<>(size);

      for (final Formula premise : premises) {
        final BooleanValue bool = circuit.mapping(premise);

        if (bool == null)
          throw new AssertionError();

        fol2boolMap.put(premise, bool);
        final AigBuilder builder = bool2aigTranslator.getBuilder(bool.label());
        bool2builderMap.put(bool.label(), builder);
        final AigExpr aig = bool2aigTranslator.getAig(bool.label());
        bool2aigMap.put(bool.label(), aig);
      }

      for (final Formula constraint : constraints) {
        final BooleanValue bool = circuit.mapping(constraint);

        if (bool == null)
          throw new AssertionError();

        fol2boolMap.put(constraint, bool);
        final AigBuilder builder = bool2aigTranslator.getBuilder(bool.label());
        bool2builderMap.put(bool.label(), builder);
        final AigExpr aig = bool2aigTranslator.getAig(bool.label());
        bool2aigMap.put(bool.label(), aig);
      }

      for (final Formula consequent : consequents) {
        final BooleanValue bool = circuit.mapping(consequent);

        if (bool == null)
          throw new AssertionError();

        fol2boolMap.put(consequent, bool);
      }
      transitionMap.put(t, fol2boolMap);
    }

    return Fol2AigProof.createProof(var2inputMap, state2latchMap, postStateLabels, transitionMap,
                                    bool2builderMap, bool2aigMap);
  }
  // /**
  // * @param transitions
  // * @param properties
  // * @param property
  // * @return
  // */
  // private Set<Formula> collectLogFormulas(TransitionRelation transitions,
  // Collection<BadProperty> properties) {
  // final int mincapacity = ((transitions.size() * 2 + properties.size() + 1) /
  // 3) * 4;
  // Set<Formula> log = new HashSet<>(mincapacity);
  // boolean success = true;
  // for (Transition t : transitions) {
  // success &= log.add(t.premise());
  // success &= log.add(t.consequent());
  // }
  // success &= log.add(transitions.standstillPremise());
  // success &= log.add(transitions.standstillConsequent());
  // for (Property property : properties)
  // success &= log.add(property.formula());
  // assert success;
  // return log;
  // }

  // /**
  // * @throws IllegalArgumentException if transitionRelation == null ||
  // badProperty == null || initialState == null || initialState is unbounded
  // * @param transitionRelation
  // * @param badProperty
  // * @param initialState
  // * @param bounds
  // */
  // private static void preCheck(Formula transitionRelation, Formula
  // badProperty, Bounds initialState,
  // Bounds bounds) {
  // if(transitionRelation == null)
  // throw new IllegalArgumentException("transitionRelation must not be null.");
  // if(badProperty == null)
  // throw new IllegalArgumentException("badProperty must not be null.");
  // if(initialState == null || initialState.relations().size() < 1)
  // throw new IllegalArgumentException("initialState must be defined.");
  // }

  // /**
  // * @param initialBounds
  // * @param extendedBounds
  // */
  // private static void postCheck(Bounds initialBounds, Bounds extendedBounds)
  // {
  // Set<Relation> relations = extendedBounds.relations();
  // HashSet<String> relNames = new HashSet<>(relations.size());
  // // collect names
  // for (Relation r : extendedBounds.relations()) {
  // relNames.add(r.name());
  // }
  // // check if the extendedBounds contain a next-state relation foreach
  // // current-state relation in the initial bounds
  // for (Relation r : initialBounds.relations()) {
  // if (!(relNames.contains(r.name() + "'")
  // || relNames.contains("$" + r.name() + "'"))) {
  // throw new IllegalStateException(
  // "There exists no next-state relation for " + r.name());
  // }
  // }
  // }
}
