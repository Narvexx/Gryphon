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
package kodkod.engine.fol2sat;

import java.util.Collections;
import java.util.Map;

import kodkod.ast.Formula;
import kodkod.ast.LeafExpression;
import kodkod.ast.Relation;
import kodkod.ast.Variable;
import kodkod.engine.bool.BooleanValue;
import kodkod.instance.Bounds;
import kodkod.util.ints.IntSet;
import kodkod.util.ints.Ints;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public final class BoolTranslation {

  private final TranslationLog log;
  private final Bounds bounds;
  private Map<Formula, BooleanValue> fol2bool;
  private Map<Relation, IntSet> varLabelMap;
  private final int maxVariable;
  private final int maxFormula;

  /**
   * @param varLabelMap
   * @param maxVariable
   * @param maxFormula
   * @param maxPrimaryVar
   * @param maxLabel
   */
  public BoolTranslation(ExtTranslationLog log, Bounds bounds, Map<Relation, IntSet> varLabelMap,
      int maxVariable, int maxFormula) {
    this.log = log;
    this.bounds = bounds;
    this.fol2bool = log.translation();
    this.maxVariable = maxVariable;
    this.maxFormula = maxFormula;
    this.varLabelMap = varLabelMap;
  }

  public TranslationLog log() {
    return log;
  }

  /**
   * @return
   */
  public Bounds bounds() {
    return bounds;
  }

  // public Map<Formula, BooleanValue> mapping() {
  // return mapping;
  // }

  public BooleanValue mapping(final Formula fol) {
    if (fol2bool.containsKey(fol)) {
      BooleanValue gate = fol2bool.get(fol);
      return gate;
      // else
      // throw new IllegalArgumentException("Mapping found, "
      // + "but not a BooleanFormula.");
    } else
      throw new IllegalArgumentException("No mapping found for " + "'relationFormula'.");
  }

  public Map<Formula, BooleanValue> mappings() {
    return Collections.unmodifiableMap(fol2bool);
  }

  public int maxVariable() {
    return maxVariable;
  }

  // /**
  // * @return
  // */
  // public Map<Relation, IntSet> varLabelMap() {
  // return varLabelMap;
  // }

  /**
   * Get the variable labels that where allocated for the given relation.
   * 
   * @param lexpr
   * @return
   */
  public IntSet labels(LeafExpression lexpr) {
    final IntSet vars;
    Relation rel = null;
    if (lexpr instanceof Relation) {
      rel = (Relation) lexpr;
    } else if (lexpr instanceof Variable) {
      for (final Relation r : varLabelMap.keySet()) {
        if (r.name().equals(lexpr.name())) {
          rel = r;
          break;
        }
      }
    }
    if (rel == null)
      vars = Ints.EMPTY_SET;
    else
      vars = varLabelMap.containsKey(rel) ? varLabelMap.get(rel) : Ints.EMPTY_SET;
    return vars;
  }

  /**
   * @return
   */
  public int maxFormula() {
    return this.maxFormula;
  }

  // public Map<Relation, IntSet> varUsage()

}
