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

import static org.modelevolution.commons.logic.OpConverter.convert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;

import kodkod.ast.Formula;
import kodkod.engine.bool.BooleanAccumulator;
import kodkod.engine.bool.BooleanConstant;
import kodkod.engine.bool.BooleanFormula;
import kodkod.engine.bool.BooleanValue;
import kodkod.engine.bool.BooleanVariable;
import kodkod.engine.bool.BooleanVisitor;
import kodkod.engine.bool.ITEGate;
import kodkod.engine.bool.MultiGate;
import kodkod.engine.bool.NotGate;
import kodkod.engine.fol2sat.BoolTranslation;

import org.modelevolution.aig.builders.AbstractAigBuilder;
import org.modelevolution.aig.builders.AigBuilder;
import org.modelevolution.aig.builders.AigBuilderFactory;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class PremiseTranslator implements Translator<AigBuilder> {
  private static final int TOP = 0;

  private final AigBuilderFactory builderFactory;
  private final AigBuilderCache cache;
  private final BoolTranslation circuit;

  /**
   * @param builderFactory
   * @param cache
   * @param circuit
   *          TODO
   */
  PremiseTranslator(final AigBuilderFactory builderFactory, final AigBuilderCache cache,
      BoolTranslation circuit) {
    this.builderFactory = builderFactory;
    this.cache = cache;
    this.circuit = circuit;
  }

  private AigBuilder translate(final BooleanValue gate) {
    if (gate instanceof BooleanAccumulator) {
      throw new MalformedGateException("gate instanceof BooleanAccumulator.");
    } else if (gate instanceof BooleanVariable) {
      assert cache.contains(gate.label()) : "Label :" + gate.label();
      final AigBuilder builder = cache.get(gate.label());
      assert builder != null;
      return builder;
    } else if (gate instanceof BooleanConstant) {
      if (gate == BooleanConstant.TRUE)
        return AigBuilder.TRUE;
      else
        return AigBuilder.FALSE;
    } else {
      assert gate instanceof BooleanFormula;
      return ((BooleanFormula) gate).accept(new PremiseVisitor(), TOP);
    }
  }

  /**
   * @param booleanValue
   * @param builderFactory
   * @return
   */
  private AigBuilder cache(Integer label, AigBuilder builder) {
    cache.add(label, builder);
    return builder;
  }

  /**
   * @param label
   * @return
   */
  private boolean isCached(Integer label) {
    return cache.contains(label);
  }

  private class PremiseVisitor implements BooleanVisitor<AigBuilder, Integer> {

    // private final AigBuilderFactory factory;
    // public PremiseVisitor(final AigBuilderFactory factory,
    // Map<Integer, AigBuilder> cache) {
    // this.factory = factory;
    // }

    /*
     * (non-Javadoc)
     * 
     * @see
     * kodkod.engine.bool.BooleanVisitor#visit(kodkod.engine.bool.MultiGate,
     * java.lang.Object)
     */
    @Override
    public AigBuilder visit(MultiGate multigate, Integer level) {
      if (isCached(multigate.label()))
        return cache.get(multigate.label());
      level += 1;
      final AigBuilder[] builders = new AigBuilder[multigate.size()];
      int i = 0;
      for (BooleanFormula f : multigate) {
        builders[i] = (f.accept(this, level));
        i += 1;
      }
      level -= 1;
      AigBuilder builder = createBuilder(multigate, builders);
      // assert res.var() == maxAigVar + (multigate.size() - 1);
      // maxAigVar = res.var() + 1;
      return cache(multigate.label(), builder);
    }

    /**
     * @param multigate
     * @param builders
     * @return
     */
    AigBuilder createBuilder(MultiGate multigate, final AigBuilder[] builders) {
      return builderFactory.gate(convert(multigate.op()), multigate.label(), builders);
    }

    /*
     * (non-Javadoc)
     * 
     * @see kodkod.engine.bool.BooleanVisitor#visit(kodkod.engine.bool.ITEGate,
     * java.lang.Object)
     */
    @Override
    public AigBuilder visit(ITEGate ite, Integer level) {
      if (isCached(ite.label()))
        return cache.get(ite.label());
      level += 1;
      AigBuilder ifExpr = ite.input(0).accept(this, level);
      assert ifExpr != null;
      AigBuilder thenExpr = ite.input(1).accept(this, level);
      assert thenExpr != null;
      AigBuilder elseExpr = ite.input(2).accept(this, level);
      assert elseExpr != null;
      // i => t & !i => e == !i|t & i|e == !(i&!t) & !(!i&!e)
      level -= 1;
      AigBuilder ifthen = builderFactory.implicationGate(ifExpr.label(), ifExpr, thenExpr);
      AigBuilder nifelse = builderFactory.implicationGate(-ifExpr.label(), ifExpr.not(), elseExpr);
      return cache(ite.label(), builderFactory.andGate(ite.label(), ifthen, nifelse));
    }

    /*
     * (non-Javadoc)
     * 
     * @see kodkod.engine.bool.BooleanVisitor#visit(kodkod.engine.bool.NotGate,
     * java.lang.Object)
     */
    @Override
    public AigBuilder visit(NotGate negation, Integer level) {
      if (isCached(negation.label()))
        return cache.get(negation.label());
      level += 1;
      AigBuilder expr = negation.input(0).accept(this, level);
      level -= 1;
      final AigBuilder notGate = expr.not();
      // if (level == TOP) return factory.shortAndGate(notGate);
      assert negation.label() == notGate.label();
      return cache(negation.label(), notGate);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * kodkod.engine.bool.BooleanVisitor#visit(kodkod.engine.bool.BooleanVariable
     * , java.lang.Object)
     */
    @Override
    public AigBuilder visit(BooleanVariable variable, Integer level) {
      assert isCached(variable.label()) : variable;
      assert !cache.isPostLabel(variable.label());
      if (isCached(variable.label())) {
        final AigBuilder builder = cache.get(variable.label());
        assert builder != null;
        // if (level == TOP)
        // return factory.shortAndGate(builder);
        // else
        return builder;
      }
      throw new NoSuchElementException("Unknown boolean variable encountered.");
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.fol2aig.Translator#translate(java.util.Collection)
   */
  @Override
  public AigBuilder translate(Collection<Formula> premises) {
    final Collection<AigBuilder> premiseBuilders = new ArrayList<>(premises.size());
    for (Formula p : premises) {
      final boolean success;
      final BooleanValue gate = circuit.mapping(p);
      final AigBuilder builder = translate(gate);
      if (builder == AigBuilder.FALSE)
        return AigBuilder.FALSE;
      success = premiseBuilders.add(builder);
      assert success : "Failed to add gate: " + gate.label() + ", Type: "
          + gate.getClass().getName();
    }
    return builderFactory.andGate(premiseBuilders);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.fol2aig.Translator#translateAll(java.util.Collection[])
   */
  @SuppressWarnings("unchecked")
  @Override
  public AigBuilder translateAll(Collection<Formula>... conditions) {
    final Collection<AigBuilder> translations = new ArrayList<>(conditions.length);
    for (final Collection<Formula> c : conditions) {
      translations.add(translate(c));
    }
    return builderFactory.andGate(translations);
  }

}

// /**
// * @return
// */
// public AigTranslation translation() {
// return translation;
// }
