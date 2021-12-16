package kodkodmod.examples;

import java.util.Iterator;
import java.util.Map.Entry;

import org.junit.Test;

import kodkod.ast.Formula;
import kodkod.ast.Relation;
import kodkod.engine.Solution;
import kodkod.engine.Solver;
import kodkod.engine.config.Options;
import kodkod.engine.fol2sat.Translation;
import kodkod.engine.fol2sat.TranslationRecord;
import kodkod.engine.fol2sat.Translator;
import kodkod.engine.satlab.SATFactory;
import kodkod.engine.ucore.AdaptiveRCEStrategy;
import kodkod.instance.Instance;
import kodkod.instance.TupleSet;
import kodkod.util.ints.IntIterator;

public final class PacmanRunner {

  private static final int BITWIDTH = 1;

  /**
   * Usage: java examples.PacmanRunner
   */
  public static void main(String[] args) {
    // runProver();
//    testDeclarationProver();
  }

  @Test
  public void testDeclarationProver() {
    final PacmanModel model = new PacmanModel(
        PacmanGameFactory._2x2_PacmanAt1_2GhostsAt0_GhostAt2_TreasureAt3(), BITWIDTH);

    final Options options = new Options();
    options.setBitwidth(1);
    options.setSolver(SATFactory.MiniSatProver);
    options.setCoreGranularity(2);
    options.setLogTranslation(2);
    // FIXME: Enable
    // options.setSymmetryBreaking(20);

    final Formula satFormula = model.testDeclaration();
    final Translation.Whole translation = Translator.translate(satFormula,
        model.verificationBounds(), options);
    printTranslationResult(translation);
    
    runProver(model, satFormula, options);
  }
  
  @Test
  public void testRemoveAllQuantifiedGhosts() {
    final PacmanModel model = new PacmanModel(
        PacmanGameFactory._2x2_PacmanAt1_2GhostsAt0_GhostAt2_TreasureAt3(), 1);

    final Options options = new Options();
    options.setBitwidth(1);
    options.setSolver(SATFactory.MiniSatProver);
    options.setCoreGranularity(2);
    options.setLogTranslation(2);
    // FIXME: Enable
    // options.setSymmetryBreaking(20);

    final Formula satFormula = model.removeActiveGhosts2();
    final Translation.Whole translation = Translator.translate(satFormula,
        model.verificationBounds(), options);
    printTranslationResult(translation);
    
    runProver(model, satFormula, options);
  }

  /**
   * 
   */
  @Test
  public void testSatUnsatProver() {
    final PacmanModel model = new PacmanModel(
        PacmanGameFactory.satSuperSimple(), BITWIDTH);

    final Options options = new Options();
    options.setSolver(SATFactory.MiniSatProver);
    options.setCoreGranularity(2);
    options.setLogTranslation(2);
    // FIXME: Enable
    // options.setSymmetryBreaking(20);
//    final Formula satFormula = model.moveAndDeactivate();
    final Formula satFormula = model.movePacmanOnce();
    final Formula unsatFormula = model.movePacmanOnce().and(
        model.pacmanOnTreasureField());
    final Translation.Whole translation = Translator.translate(satFormula,
        model.verificationBounds(), options);

    printTranslationResult(translation);

    runProver(model, satFormula, options);
  }

  /**
   * @param model
   * @param formula
   * @param options
   */
  public static void runProver(
      final PacmanModel model, final Formula formula, final Options options) {
    final Solver solver = new Solver(options);
    // final Solution solution = solver.solve(satFormula, model.bounds());
    final Iterator<Solution> solutionIt = solver.solveAll(formula,
        model.bounds());
    while (solutionIt.hasNext()) {
      System.out.println("Solution:");
      final Solution solution = solutionIt.next();
      // System.out.println(solution);
      if (solution.sat()) {
        System.out.println("\n---Instance is SAT---");
        Instance instance = solution.instance();
        for (Entry<Relation, TupleSet> e : instance.relationTuples().entrySet()) {
          Relation r = e.getKey();
          TupleSet ts = e.getValue();
          System.out.print(r.name() + ": ");
          System.out.println(ts.toString());
        }
      } else {
        System.out.println("\n---Instance is UNSAT---\n");
        System.out.println("** Minimizing the UNSAT-core...");
        solution.proof().minimize(
            new AdaptiveRCEStrategy(solution.proof().log()));
        System.out.println("\n** Done minimizing");

        System.out
            .println("\nThe UNSAT-core comprises the following (relational) constraints:\n");
        for (Iterator<TranslationRecord> recordIt = solution.proof().core(); recordIt
            .hasNext();) {
          TranslationRecord r = (TranslationRecord) recordIt.next();
          System.out.println(r);
        }
      }
      System.out.println();
    }
  }

  /**
   * @param translation
   */
  public static
      void printTranslationResult(final Translation.Whole translation) {
    System.out.println(translation.bounds());
    System.out.println();
    System.out
        .println("Relations and the primary variables associated with them:");
    for (Relation r : translation.bounds().relations()) {
      System.out.print(r.name() + ": ");
      IntIterator it = translation.primaryVariables(r).iterator();
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
    System.out.println("Replaying the translation in detail...");
    for (Iterator<TranslationRecord> it = translation.log().replay(); it
        .hasNext();) {
      TranslationRecord tr = it.next();
      System.out.println(tr);
    }
    System.out.println();
  }
}

// else {
// System.out.println("\n---Instance is UNSAT---\n");
// System.out.println("** Trying to shorten the UNSAT-proof...");
// // reduce clauses in the refutation proof
// solver.reduce(new AdaptiveRCEStrategy(translation.log()));
// System.out.println("\n** Done shortening!\n");
//
// // get a proof object that contains the unsat core
// Proof proof = ProofFactory.createResolutionBasedProof(solver,
// translation.log());
//
// System.out.println("Clauses in the refutation proof:");
// // iterate through the set of clauses in the refutation
// for(Clause c : solver.proof()) {
// System.out.println(c);
// }
// System.out.println("\n");
//
// System.out.println("Translation Records of the UNSAT core:");
//
// Iterator<TranslationRecord> itr = proof.core();
// while(itr.hasNext()) {
// TranslationRecord tr = itr.next();
// System.out.println(tr);
// }
// }
//
// System.out.println(solver.proof());
//
// solver.free();
// }

/*
 * final Solver solver = new Solver(); // final int p =
 * Integer.parseInt(args[0]); // final int h = Integer.parseInt(args[1]);
 * solver.options().setSolver(SATFactory.MiniSatProver);
 * solver.options().setSymmetryBreaking(20);
 * solver.options().setLogTranslation(2); // final Solution sol =
 * solver.solve(model.verificationCondition(), // model.satBounds(numFields,
 * numGhosts, numTreasures)); final Iterator<Solution> sit = solver.solveAll(
 * model.verificationCondition(), model.bounds()); for (int cnt = 1;
 * sit.hasNext(); cnt++) { Solution sol = (Solution) sit.next();
 * System.out.println("-------INSTANCE " + cnt + "-------");
 * System.out.println(sol); if (sol.unsat()) { Proof unsatProof = sol.proof();
 * if (cnt == 1) { // minimization doesn't seem to work if the solver //
 * previously returned SAT instances unsatProof .minimize(new
 * DynamicRCEStrategy(unsatProof.log())); } int coreCount = 0; for
 * (Iterator<TranslationRecord> trit = sol.proof().core(); trit .hasNext();) {
 * TranslationRecord r = (TranslationRecord) trit.next(); System.out.println(r);
 * coreCount += 1; } System.out.println("Number of cores: " + coreCount); } else
 * { System.out.println(sol.instance()); Map<Relation, TupleSet> assignments =
 * sol.instance() .relationTuples();
 * System.out.print("Obtain changed relation "); Relation nextOn = null; for
 * (Relation r : assignments.keySet()) { String name = r.name(); if
 * (name.equals("$on'")) nextOn = r; } TupleSet playerOnNext =
 * assignments.get(nextOn); System.out.println("on' = " + playerOnNext);
 * 
 * } System.out.println("-------END INSTANCE " + cnt + "-------\n\n"); } }
 * 
 * // private static void usage() { //
 * System.out.println("This should display the usage information..."); // }
 */
