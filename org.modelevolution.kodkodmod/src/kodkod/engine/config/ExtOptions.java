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
package kodkod.engine.config;

import kodkod.engine.config.Options.IntEncoding;
import kodkod.engine.fol2sat.TranslationLoggerMod;
import kodkod.engine.satlab.SATFactory;
import kodkod.util.ints.IntRange;
import kodkodmod.verification.VerificationCondition;

/**
 * @author Sebastian Gabmeyer
 * 
 */
/**
 * @author Sebastian Gabmeyer
 * 
 */
/**
 * @author Sebastian Gabmeyer
 * 
 */
public final class ExtOptions implements Cloneable {

  private final Options options;
  private TranslationLoggerMod logger = null;
  private boolean vccheck = false;
  private boolean deterministic = false;
  private boolean isBenchmarking = false;

  public ExtOptions() {
    options = new Options();
  }

  /**
   * @param c
   *          a clone of this.options obtained by {@link Options#clone()
   *          options.clone()}.
   */
  public ExtOptions(Options c) {
    options = c;
  }

  /**
   * @return the encapsulated {@link Options options} object.
   */
  public Options baseOptions() {
    return this.options;
  }

  /**
   * Returns the value of the solver options. The default is
   * SATSolver.DefaultSAT4J.
   * 
   * @return this.solver
   */
  public SATFactory solver() {
    return options.solver();
  }

  /**
   * Sets the solver option to the given value.
   * 
   * @ensures this.solver' = solver
   * @throws NullPointerException
   *           solver = null
   */
  public void setSolver(SATFactory solver) {
    options.setSolver(solver);
  }

  /**
   * Returns this.reporter.
   * 
   * @return this.reporter
   */
  public Reporter reporter() {
    return options.reporter();
  }

  /**
   * Sets this.reporter to the given reporter.
   * 
   * @requires reporter != null
   * @ensures this.reporter' = reporter
   * @throws NullPointerException
   *           reporter = null
   */
  public void setReporter(Reporter reporter) {
    options.setReporter(reporter);
  }

  /**
   * Returns the integer encoding that will be used for translating
   * {@link kodkod.ast.IntExpression int nodes}. The default is BINARY
   * representation, which allows negative numbers. UNARY representation is best
   * suited to problems with small scopes, in which cardinalities are only
   * compared (and possibly added to each other or non-negative numbers).
   * 
   * @return this.intEncoding
   */
  public IntEncoding intEncoding() {
    return options.intEncoding();
  }

  /**
   * Sets the intEncoding option to the given value.
   * 
   * @ensures this.intEncoding' = encoding
   * @throws NullPointerException
   *           encoding = null
   * @throws IllegalArgumentException
   *           this.bitwidth is not a valid bitwidth for the specified encoding
   */
  public void setIntEncoding(IntEncoding encoding) {
    options.setIntEncoding(encoding);
  }

  /**
   * Returns the size of the integer representation. For example, if
   * this.intEncoding is BINARY and this.bitwidth = 5 (the default), then all
   * operations will yield one of the five-bit numbers in the range [-16..15].
   * If this.intEncoding is UNARY and this.bitwidth = 5, then all operations
   * will yield one of the numbers in the range [0..5].
   * 
   * @return this.bitwidth
   */
  public int bitwidth() {
    return options.bitwidth();
  }

  /**
   * Sets this.bitwidth to the given value.
   * 
   * @ensures this.bitwidth' = bitwidth
   * @throws IllegalArgumentException
   *           bitwidth < 1
   * @throws IllegalArgumentException
   *           this.intEncoding==BINARY && bitwidth > 32
   */
  public void setBitwidth(int bitwidth) {
    options.setBitwidth(bitwidth);
  }

  /**
   * Returns the range of integers that can be encoded using this.intEncoding
   * and this.bitwidth.
   * 
   * @return range of integers that can be encoded using this.intEncoding and
   *         this.bitwidth.
   */
  public IntRange integers() {
    return options.integers();
  }

  /**
   * Returns the 'amount' of symmetry breaking to perform. If a non-symmetric
   * solver is chosen for this.solver, this value controls the maximum length of
   * the generated lex-leader symmetry breaking predicate. In general, the
   * higher this value, the more symmetries will be broken. But setting the
   * value too high may have the opposite effect and slow down the solving. The
   * default value for this property is 20.
   * 
   * @return this.symmetryBreaking
   */
  public int symmetryBreaking() {
    return options.symmetryBreaking();
  }

  /**
   * Sets the symmetryBreaking option to the given value.
   * 
   * @ensures this.symmetryBreaking' = symmetryBreaking
   * @throws IllegalArgumentException
   *           symmetryBreaking !in [0..Integer.MAX_VALUE]
   */
  public void setSymmetryBreaking(int symmetryBreaking) {
    options.setSymmetryBreaking(symmetryBreaking);
  }

  /**
   * Returns the depth to which circuits are checked for equivalence during
   * translation. The default depth is 3, and the minimum allowed depth is 1.
   * Increasing the sharing may result in a smaller CNF, but at the cost of
   * slower translation times.
   * 
   * @return this.sharing
   */
  public int sharing() {
    return options.sharing();
  }

  /**
   * Sets the sharing option to the given value.
   * 
   * @ensures this.sharing' = sharing
   * @throws IllegalArgumentException
   *           sharing !in [1..Integer.MAX_VALUE]
   */
  public void setSharing(int sharing) {
    options.setSharing(sharing);
  }

  /**
   * Returns the depth to which existential quantifiers are skolemized. A
   * negative depth means that no skolemization is performed. The default depth
   * of 0 means that only existentials that are not nested within a universal
   * quantifiers are skolemized. A depth of 1 means that existentials nested
   * within a single universal are also skolemized, etc.
   * 
   * @return this.skolemDepth
   */
  public int skolemDepth() {
    return options.skolemDepth();
  }

  /**
   * Sets the skolemDepth to the given value.
   * 
   * @ensures this.skolemDepth' = skolemDepth
   */
  public void setSkolemDepth(int skolemDepth) {
    options.setSkolemDepth(skolemDepth);
  }

  /**
   * Returns the translation logging level (0, 1, or 2), where 0 means logging
   * is not performed, 1 means only the translations of top level formulas are
   * logged, and 2 means all formula translations are logged. This is necessary
   * for determining which formulas occur in the unsat core of an unsatisfiable
   * formula. Logging is off by default, since it incurs a non-trivial time
   * overhead.
   * 
   * @return this.logTranslation
   */
  public int logTranslation() {
    return options.logTranslation();
  }

  /**
   * Sets the translation logging level.
   * 
   * @requires logTranslation in [0..2]
   * @ensures this.logTranslation' = logTranslation
   * @throws IllegalArgumentException
   *           logTranslation !in [0..2]
   */
  public void setLogTranslation(int logTranslation) {
    options.setLogTranslation(logTranslation);
  }

  /**
   * Returns the core granularity level. The default is 0, which means that
   * top-level conjuncts of the input formula are used as "roots" for the
   * purposes of core minimization and extraction. Granularity of 1 means that
   * the top-level conjuncts of the input formula's negation normal form (NNF)
   * are used as roots; granularity of 2 means that the top-level conjuncts of
   * the formula's skolemized NNF (SNNF) are used as roots; and, finally, a
   * granularity of 3 means that the universal quantifiers of the formula's SNNF
   * are broken up and that the resulting top-level conjuncts are then used as
   * roots for core minimization and extraction.
   * 
   * <p>
   * Note that the finer granularity (that is, a larger value of
   * this.coreGranularity) will provide better information at the cost of slower
   * core extraction and, in particular, minimization.
   * </p>
   * 
   * @return this.coreGranularity
   */
  public int coreGranularity() {
    return options.coreGranularity();
  }

  /**
   * Sets the core granularity level.
   * 
   * @requires coreGranularity in [0..3]
   * @ensures this.coreGranularity' = coreGranularity
   * @throws IllegalArgumentException
   *           coreGranularity !in [0..3]
   */
  public void setCoreGranularity(int coreGranularity) {
    options.setCoreGranularity(coreGranularity);
  }

  public TranslationLoggerMod logger() {
    return this.logger;
  }

  public void setLogger(TranslationLoggerMod logger) {
    this.logger = logger;
  }

  /**
   * Returns a shallow copy of this Options object. In particular, the returned
   * options shares the same {@linkplain #reporter()} and {@linkplain #solver()}
   * factory objects as this Options.
   * 
   * @return a shallow copy of this Options object.
   */
  public ExtOptions clone() {
    final Options c = options.clone();
    final ExtOptions o = new ExtOptions(c);
    return o;
  }

  /**
   * Returns a string representation of this Options object.
   * 
   * @return a string representation of this Options object.
   */
  public String toString() {
    StringBuilder b = new StringBuilder(options.toString());
    b.append("\n Logger: ");
    b.append((logger == null ? "null" : logger.getClass().getName()));
    return b.toString();
  }

  /**
   * Indicates whether an integrity check of the {@link VerificationCondition}
   * should be performed or not. Default is <code>false</code>.
   */
  public boolean doVCCheck() {
    return vccheck;
  }

  public void doVCCheck(boolean check) {
    vccheck = check;
  }

  /**
   * Indicates whether the transition relation should be forced to be
   * deterministic, i.e., additional (input) variables are introduced that
   * activate exactly one transition at each step.
   */
  public boolean forceDeterminisitc() {
    return deterministic;
  }

  public void forceDeterminisitc(boolean deterministic) {
    this.deterministic = deterministic;
  }

  public boolean isBenchmarking() {
    return isBenchmarking;
  }
  
  /**
   * @param value
   */
  public void setIsBenchmarking(boolean value) {
    this.isBenchmarking = value;    
  }

}
