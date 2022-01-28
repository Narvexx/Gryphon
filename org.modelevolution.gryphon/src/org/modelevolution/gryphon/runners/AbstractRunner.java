/* 
 * org.modelevolution.gryphon -- Copyright (c) 2015-present, Sebastian Gabmeyer
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
package org.modelevolution.gryphon.runners;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kodkod.ast.Formula;
import kodkod.ast.Relation;
import kodkod.ast.Variable;
import kodkod.engine.config.ExtOptions;
import kodkod.engine.config.Options;
import kodkod.engine.fol2sat.BoolTranslation;
import kodkod.engine.fol2sat.TranslationRecord;
import kodkod.instance.TupleFactory;
import kodkod.instance.TupleSet;
import kodkod.util.ints.IntIterator;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.henshin.model.Annotation;
import org.eclipse.emf.henshin.model.Rule;
import org.modelevolution.aig.AigFile;
import org.modelevolution.emf2rel.FeatureMerger;
import org.modelevolution.emf2rel.FeatureOmitter;
import org.modelevolution.emf2rel.Signature;
import org.modelevolution.fol2aig.AigTranslator;
import org.modelevolution.fol2aig.Fol2AigProof;
import org.modelevolution.gryphon.input.AbstractInputParams;
import org.modelevolution.gryphon.input.EmfModelResolver;
import org.modelevolution.gryphon.input.InputLoader;
import org.modelevolution.gryphon.output.Stats;
import org.modelevolution.gryphon.solver.VerificationResult;
import org.modelevolution.gts2rts.PropertyTranslator;
import org.modelevolution.gts2rts.RuleTranslator;
import org.modelevolution.gts2rts.VariabilityRuleTranslator;
import org.modelevolution.rts.Property;
import org.modelevolution.rts.StateChanger;
import org.modelevolution.rts.TransitionRelation;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public abstract class AbstractRunner implements Runner {
	protected static final int STATS_IDX = 1;
	protected static final int AIG_IDX = 0;

	protected Map<EClass, Integer> upperBounds;
	protected int bitwidth;
	protected EPackage metamodel;
	protected Resource[] initialStates;
	// protected Options options2;
	protected ExtOptions options;
	protected FeatureOmitter omitter;
	protected FeatureMerger merger;
	protected Collection<Rule> transitionRules;
	protected Collection<Rule> propertyRules;
	protected String[][] outputFilepaths;
	protected Stats stats;
	protected List<VerificationResult> results;
	protected boolean verbose;

	/**
	 * 
	 */
	protected AbstractRunner(AbstractInputParams params, boolean verbose) {
		metamodel = params.getMetamodel();
		initialStates = params.getInitialStates();
		upperBounds = params.getUpperBounds();
		merger = params.getMerger();
		omitter = params.getOmitter();
		bitwidth = params.getBitwidth();
		transitionRules = params.getTransitionRules();
		propertyRules = params.getSpecification();
		outputFilepaths = params.getOutputFilepaths();
		this.verbose = verbose;
		results = new ArrayList<>(propertyRules.size());
		options = new ExtOptions();
	}

	public List<VerificationResult> results() {
		return this.results;
	}

	public Stats statistics() {
		return stats;
	}

	/**
	 * @param sig
	 * @param transitions
	 * @param properties
	 * @param translator
	 */
	protected void printDebug(Signature sig, final TransitionRelation transitions,
			final Collection<Property> properties, final AigTranslator translator) {
		System.out.println();
		System.out.println("[Gryphon] Transitions:");
		for (StateChanger t : transitions) {
			System.out.println(t);
			System.out.println();
		}

		System.out.println();
		System.out.println("[Gryphon] Properties:");
		for (Property property : properties) {
			System.out.println(property);
			System.out.println();
		}

		System.out.println();
		System.out.println("[Gryphon] Proof of Translation:");
		final Fol2AigProof proof = translator.proof(transitions, properties);
		System.out.println(proof);

		final BoolTranslation circuit = translator.circuit();

		for (StateChanger t : transitions) {
			System.out.println(t.name());
			for (Formula p : t.premises()) {
				System.out.print(p);
				System.out.print(" & ");
			}
			System.out.println();
			for (Formula p : t.premises()) {
				System.out.print(circuit.mapping(p));
				System.out.print("&");
			}
			System.out.println();
			for (Formula c : t.consequents()) {
				System.out.print(c);
				System.out.print(" & ");
			}
			System.out.println();
			for (Formula c : t.consequents()) {
				System.out.print(circuit.mapping(c));
				System.out.print("&");
			}
			System.out.println();
		}
		System.out.println();

		System.out.println();
		System.out.println("[Gryphon] Relations and their primary variables:");
		for (Relation r : circuit.bounds().relations()) {
			System.out.print(r.name() + ": ");
			IntIterator it = circuit.labels(r).iterator();
			if (it.hasNext()) {
				int literal = (int) it.next();
				System.out.print(literal);
				while (it.hasNext()) {
					literal = (int) it.next();
					System.out.print(", " + literal);
				}
			}
			System.out.println();
		}

		System.out.println();
		System.out.println("[Gryphon] Bounds:");
		System.out.println(sig.bounds());

		System.out.println();
		System.out.println("[Gryphon] Replaying the translation in detail:");
		final Iterator<TranslationRecord> it = circuit.log().replay();
		while (it.hasNext()) {
			TranslationRecord tr = it.next();
			System.out.println(tr);
		}
	}

	/**
	 * @param aigFilepath
	 * @param specification
	 * @param stats
	 */
	protected abstract void runVerifier(final String aigFilepath, final List<Property> specification, Stats stats);

	/**
	 * @param sig
	 * @param transitions
	 * @param specification
	 * @param outputFilepath
	 * @param stats
	 * @return
	 * @throws IOException
	 */
	protected AigTranslator translateToAig(Signature sig, final TransitionRelation transitions,
			final List<Property> specification, final String outputFilepath, Stats stats) throws IOException {
		final long startAig = System.currentTimeMillis(); /* System.nanoTime() */
		options.setBitwidth(sig.bitwidth());
		options.setSymmetryBreaking(0);

		final AigTranslator translator = new AigTranslator(sig, options);
		assert translator != null;
		final AigFile aig = translator.translate(transitions, specification);
		stats.record(System.currentTimeMillis() - startAig /* System.nanoTime() */, "AIG (in-memory)");
		// final long aigDone = System.currentTimeMillis();

		/* Write the AIG representation to a file adhering to the AIGER format */
		final long startFile = System.currentTimeMillis(); /* System.nanoTime() */
		aig.writeAsciiTo(outputFilepath, true);
		stats.record(System.currentTimeMillis() - startFile /* System.nanoTime() */, "AIG File");
		return translator;
	}

	/**
	 * @param sig
	 * @param stats
	 * @return
	 */
	protected List<Property> createSpecification(Signature sig, Stats stats) {
		final long start = System.currentTimeMillis(); /* System.nanoTime() */
		final List<Property> specification = new ArrayList<>(propertyRules.size());
		final PropertyTranslator propertyTranslator = new PropertyTranslator(sig);
		for (final Rule propertyRule : propertyRules) {
			final Property property = propertyTranslator.translate(propertyRule);
			specification.add(property);
		}
		stats.record(System.currentTimeMillis() - start /* System.nanoTime() */, "Property Translation");
		return specification;
	}

	/**
	 * @param sig
	 * @param stats
	 * @return
	 */
	protected TransitionRelation createTransitions(Signature sig, Stats stats) {
		final long start = System.currentTimeMillis(); /* System.nanoTime() */
		final TransitionRelation transitions = RuleTranslator.translateAll(sig, transitionRules);
		stats.record(System.currentTimeMillis() - start /* System.nanoTime() */, "Rule Translation");
		// final long rulesDone = System.currentTimeMillis();

		assert transitions != null;
		assert transitions.size() == transitionRules.size();
		return transitions;
	}

	/**
	 * @param initial
	 * @param stats
	 * @return
	 */

	protected Signature createSignature(final Resource initial, Stats stats) {
		final long start = System.currentTimeMillis(); /* System.nanoTime() */
		// subStats.startTime(System.nanoTime());
		Signature sig = Signature.init(metamodel, VariabilityRuleTranslator.getFeatures(transitionRules), initial, upperBounds, merger, omitter, bitwidth);
		stats.record(System.currentTimeMillis() - start /* System.nanoTime() */, "Signature Creation");
		// final long sigDone = System.currentTimeMillis();
		return sig;
	}

	/**
	 * @param initialState
	 * @param aigFile
	 * @param stats
	 */
	protected void doRun(final Resource initialState, final String aigFile, final Stats stats) {
		
		/* Create the signature */
		final Signature sig = createSignature(initialState, stats);
		
		if (VariabilityRuleTranslator.isVariabilityBased()) {
			VariabilityRuleTranslator.annotateFeatureBounds(sig, transitionRules);
		}
		
		/* Translate the graph transformation to (symbolic) transitions */
		final TransitionRelation transitions = createTransitions(sig, stats);

		/*
		 * Translate graph constraints, i.e., property rules, to (symbolic) properties
		 */
		final List<Property> specification = createSpecification(sig, stats);

		/*
		 * Create the in-memory AIG representation of the (symbolic) transition system
		 * and write the AIG representation to a file whose content adheres to the AIGER
		 * format
		 */
		final AigTranslator translator;
		try {
			translator = translateToAig(sig, transitions, specification, aigFile, stats);
		} catch (IOException e) {
			System.err.println("An error occured while writing the AIG file. "
					+ "No AIG output has been generated. Stacktrace follows:");
			e.printStackTrace();
			return;
		}

		/* Run the model checker on the AIG file for each property */
		runVerifier(aigFile, specification, stats);

		if (verbose) {
			// System.out.println("[Gryphon] Results:");
			// for (VerificationResult r : results) {
			// for (final String line : r.output())
			// System.out.println(line);
			// System.out.println();
			// }
			System.out.println();
			printDebug(sig, transitions, specification, translator);
		}
	}
}
