/**
 * 
 */
package org.modelevolution.fol2aig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import kodkod.ast.Formula;
import kodkod.engine.config.ExtOptions;
import kodkod.engine.fol2sat.BoolTranslation;

import org.modelevolution.aig.AigExpr;
import org.modelevolution.aig.AigFactory;
import org.modelevolution.aig.AigFile;
import org.modelevolution.aig.builders.AbstractAigBuilder;
import org.modelevolution.aig.builders.Activator;
import org.modelevolution.aig.builders.LatchedActivator;
import org.modelevolution.aig.builders.AigBuilder;
import org.modelevolution.aig.builders.AigBuilderFactory;
import org.modelevolution.aig.builders.AigFileBuilder;
import org.modelevolution.aig.builders.BadBuilder;
import org.modelevolution.emf2rel.Signature;
import org.modelevolution.rts.Invariant;
import org.modelevolution.rts.Loop;
import org.modelevolution.rts.Property;
import org.modelevolution.rts.StateChanger;
import org.modelevolution.rts.Transition;
import org.modelevolution.rts.TransitionRelation;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class Bool2AigTranslator {
  private final ExtOptions options;
  private AigFactory factory;
  private final Signature sig;
  private AigBuilderFactory builderFactory;
  private AigFileBuilder fileBuilder;
  // private LatchCache latchCache;
  private AigBuilderCache cache;
  private Map<Transition, AigBuilder> transitionPremiseMap;
  private final Map<Loop, AigBuilder> loopPremiseMap;

  // private final StateVector states = StateVector.create();
  // private AigBuilderFactory factory;
  // private final IntSet preLabels;
  // private final IntSet postLabels;

  // /**
  // *
  // * @param circuit
  // * @param transitions
  // * @param invariant
  // * @return
  // */
  // public static AigTranslation translate(final BoolTranslation circuit,
  // TransitionRelation transitions, Invariant invariant,
  // final ExtOptions options) {
  // return new Bool2AIGTranslator(options).translate(circuit, transitions,
  // invariant);
  // }

  /**
   * @param sig
   * @param options
   */
  Bool2AigTranslator(final Signature sig, final ExtOptions options) {
    this.options = options;
    this.sig = sig;
    this.transitionPremiseMap = new IdentityHashMap<>();
    loopPremiseMap = new IdentityHashMap<>();
  }

  AigFile translate(final BoolTranslation circuit, final TransitionRelation transitions,
      final List<Property> properties) {
    factory = new AigFactory();
    builderFactory = new AigBuilderFactory(circuit.maxFormula(), factory);
    fileBuilder = new AigFileBuilder(factory);
    cache = AigBuilderCache.create(sig, transitions, properties, circuit, builderFactory);

    fileBuilder.addInputs(cache.inputs());
    fileBuilder.addLatches(cache.latches());

    final Translator<AigBuilder> premiseTranslator = new PremiseTranslator(builderFactory, cache,
                                                                           circuit);
    final Translator<LatchBindings> consequentTranslator = new ConsequentTranslator(builderFactory,
                                                                                    cache, circuit);

    final Collection<AigBuilder> noOpPremises = translateTransitions(transitions,
                                                                     premiseTranslator,
                                                                     consequentTranslator);

    final Collection<Formula> noOpConsequents = transitions.noOps();
    translateNoOp(consequentTranslator, noOpPremises, noOpConsequents);

    // for (LatchBuilder builder : latchCache.latches()) {
    // System.out.println(builder);
    // }

    translateProperties(premiseTranslator, properties);

    return fileBuilder.toFile();
  }

  /**
   * @param transitions
   * @param premiseTranslator
   * @param consequentTranslator
   * @param standstillPremises
   * @return
   */
  private Collection<AigBuilder> translateTransitions(final TransitionRelation transitions,
      final Translator<AigBuilder> premiseTranslator,
      final Translator<LatchBindings> consequentTranslator) {
    final Collection<AigBuilder> noOpPremises = new ArrayList<>(transitions.size());
    /*
     * We require two passes over the transition relation: in the first pass, we
     * translate ALL premises (including loop premises); in the second pass, we
     * assemble the premises with their latch bindings.
     */
    for (final Transition t : transitions) {
      @SuppressWarnings("unchecked")
      final AigBuilder basePremise = premiseTranslator.translateAll(t.constraints(), t.premises());
      final AigBuilder activator = cache.activator(t);
      final AigBuilder deactivator = cache.deactivator(t); // buildDeactivator(t);
      final AigBuilder premise = builderFactory.andGate(basePremise, activator, deactivator);
      transitionPremiseMap.put(t, premise);
      noOpPremises.add(premise.not());

      for (final Loop l : t.loops()) {
        // FIXME: add activators and deactivators to loops
        @SuppressWarnings("unchecked")
        final AigBuilder loopPremise = premiseTranslator.translateAll(l.constraints(), l.premises());
        loopPremiseMap.put(l, loopPremise);
      }
    }

    for (final Transition t : transitions) {
      final LatchBindings latches = consequentTranslator.translate(t.consequents());

      final AigBuilder premise = transitionPremiseMap.get(t);
      final AigBuilder memoryActivator = buildMemoryActivator(t);

      for (final Loop l : t.loops()) {
        /*
         * FIXME: Loop Translation
         * 
         * final AigBuilder loopBasePremise = loopPremiseMap.get(l); //
         * premiseTranslator.translateAll(l.constraints(), l.premises()); final
         * LatchBindings loopLatches =
         * consequentTranslator.translate(l.consequents()); final AigBuilder
         * loopDeactivator = buildLoopDeactivator(l, t, loopPremiseMap); final
         * AigBuilder loopActivitor = cache.activator(l); assert loopActivitor
         * != null; loopActivitor.inputs(basePremise, loopBasePremise,
         * loopDeactivator, deactivator); //
         * loopLatches.setActivator(loopActivitor); final AigBuilder loopPremise
         * = builderFactory.andGate(builderFactory.orGate(loopActivitor,
         * basePremise), loopBasePremise, loopDeactivator, deactivator);
         * loopLatches.setPremise(loopPremise); latches.addLoop(loopLatches);
         */
      }
      latches.setPremise(premise, memoryActivator).exportTo(cache);
      // fileBuilder.add((AndBuilder) ifGate);
    }
    return noOpPremises;
  }

  /**
   * @param translator
   * @param standstillPremises
   * @param standstillConsequents
   */
  private void translateNoOp(final Translator<LatchBindings> translator,
      final Collection<AigBuilder> standstillPremises,
      final Collection<Formula> standstillConsequents) {
    final LatchBindings latches = translator.translate(standstillConsequents);
    latches.setPremise(builderFactory.andGate(standstillPremises)).exportTo(cache);
  }

  /**
   * @param translator
   * @param properties
   */
  private void translateProperties(final Translator<AigBuilder> translator,
      final List<Property> properties) {
    final AigBuilder deactivator = cache.allDeactivator();

    if (options.isBenchmarking()) {
      final List<AigBuilder> badInputs = new ArrayList<>(properties.size());
      for (Property p : properties) {
        if (p instanceof Invariant)
          continue; // p = p.negate();
        final AigBuilder constraints = translator.translate(p.constraints());
        final AigBuilder conditions = translator.translate(p.conditions());
        final AigBuilder badInput = builderFactory.andGate(conditions, constraints/*
                                                                                   * ,
                                                                                   * deactivator
                                                                                   */);
        badInputs.add(badInput);
      }
      final BadBuilder badBuilder = builderFactory.createBadBuilder(builderFactory.orGate(badInputs));
      fileBuilder.add(badBuilder);
    } else {
      for (Property p : properties) {
        if (p instanceof Invariant)
          continue; // p = p.negate();
        final AigBuilder constraints = translator.translate(p.constraints());
        final AigBuilder conditions = translator.translate(p.conditions());
        final AigBuilder badInput = builderFactory.andGate(conditions, constraints/*
                                                                                   * ,
                                                                                   * deactivator
                                                                                   */);
        final BadBuilder badBuilder = builderFactory.createBadBuilder(badInput);
        fileBuilder.add(badBuilder);
      }
    }
  }

  /**
   * @param t
   * @return
   */
  private AigBuilder buildMemoryActivator(final StateChanger t) {
    final Collection<AigBuilder> loopActivators = new ArrayList<>(t.loops().size());
    for (final StateChanger l : t.loops()) {
      loopActivators.add(cache.activator(l));
    }
    return builderFactory.orGate(loopActivators);
  }

  /**
   * @param parent
   * @param loopPremiseMap
   * @return
   */
  private AigBuilder buildLoopDeactivator(final StateChanger loop, final StateChanger parent,
      Map<Loop, AigBuilder> loopPremiseMap) {
    final int size = loopPremiseMap.size() - 1 + parent.loops().size() - 1;
    final Collection<AigBuilder> deactivators = new ArrayList<>(size);

    for (final StateChanger l : loopPremiseMap.keySet()) {
      if (l == loop)
        continue;
      deactivators.add(loopPremiseMap.get(l).not());
    }

    for (final StateChanger l : parent.loops()) {
      if (l == loop)
        continue;
      deactivators.add(cache.activator(l).not());
    }

    return builderFactory.andGate(deactivators);
  }

  // /**
  // * @param transition
  // * @param deactivators
  // * @return
  // */
  // private AigBuilder buildDeactivator(Transition transition) {
  // final Collection<AigBuilder> deactivators = new ArrayList<>();
  //
  // /*
  // * Note: the deactivator returned by cache.deactivator(transition) includes
  // * all necessary deactivation literals including those allocated for loops.
  // */
  // final AigBuilder deactivator = cache.deactivator(transition);
  //
  // /* FIXME: other loop premises */
  // for (final StateChanger t : loopPremiseMap.keySet()) {
  // if (transition.loops().contains(t))
  // continue;
  // deactivators.add(loopPremiseMap.get(t).not());
  // }
  //
  // final AigBuilder deactivatorConditions =
  // builderFactory.andGate(deactivators);
  // return deactivator;
  // }

  // private Collection<Formula>
  // buildStandstillConsequent(Collection<StateRelation> states) {
  // Collection<Formula> consequents = new ArrayList<>(states.size());
  // for (StateRelation r : states) {
  // if (r.isStatic())
  // continue;
  // consequents.add(r.postState().eq(r.preState()));
  // }
  // return consequents;
  // }

  AigExpr getAig(int folLabel) {
    final AigBuilder builder = cache.get(folLabel);
    if (builder != null)
      return factory.cached(builder);
    else
      return null;
  }

  /**
   * @param boolLabel
   * @return
   */
  AigBuilder getBuilder(int boolLabel) {
    return cache.get(boolLabel);
  }
}