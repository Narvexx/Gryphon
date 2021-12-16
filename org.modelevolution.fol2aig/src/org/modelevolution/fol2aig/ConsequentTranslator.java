/*
 * org.modelevolution.fol2aig -- Copyright (c) 2015-present, Sebastian
 * Gabmeyer
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

import static kodkod.engine.bool.Operator.ITE;
import static kodkod.engine.bool.Operator.NOT;
import static kodkod.engine.bool.Operator.OR;

import java.util.Collection;

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

import org.modelevolution.aig.builders.AigBuilder;
import org.modelevolution.aig.builders.AigBuilderFactory;

/**
 * <p>
 * This class provides methods that perform assertional checks on the structure
 * of a rule's consequent. Currently, we ensure that the consequent resulting
 * from a translation of a rule's RHS has either of the following structures:
 * <ol>
 * <li><code>m1 && m2 && ... && mN</code>, i.e., a conjunction of
 * {@link MultiGate multi-gates} <code>m1,...,mN</code>, or</li>
 * <li><code>ite1 && ite2 && ... && iteN</code>, i.e., a conjunction of
 * {@link ITEGate if-then-else gates} <code>ite1,...,iteN</code>.
 * </ol>
 * 
 * <p>
 * In the first case, we ensure that the multi-gates <code>m1,...,mN</code> are
 * either AND gates or OR gates. In case of an AND gate, we recursively analyze
 * its inputs until we find an OR gate. In case of an OR gate, we ensure that
 * exactly one of its inputs contains a next state variable. Further, we ensure
 * that an OR gate does not contain an if-then-else gate that contains next
 * state variables [CAUTION: this is an assumption to simplify the translation
 * from FOL to AIG; in general, an ITE gate with next state variables might
 * occur as an input to an OR gate that contains no next state variables other
 * than those occurring in the ITE gate. In particular, the next state function
 * for two next state variables <code>a',b'</code> occurring in an ITE gate of
 * the form <code>IF c THEN a' ELSE b'</code> that is contained in an OR gate of
 * the form <code>d | ITE | e</code> are <code>!d => !e => !c => a'</code> and
 * <code>!d => !e  => c => b'</code>, or equivalently,
 * <code>!(d && e && c) => a'</code> and <code>!(d && e && !c) => b'</code>].
 * Note that an OR gate represents an implication. Since the RHS of a rule
 * prescribes the changes that a rule performs upon application, the translation
 * of a rule's RHS results in a conjunction of implications. These implications
 * are translated into OR gates. Consequently, an OR gate must have the
 * following structure: <code>a|!b'</code> or <code>!a|b'</code> or
 * <code>a|c|!b'</code> or <code>(a&c)|b'</code> and so on, where primed/latch
 * variables represent next state variables. Thus, if an OR gate does not
 * contain a next state variable, the translation of the rule's RHS is
 * considered erroneous. Further, each next state variable needs to occur with
 * positive and negative polarity in each consequent (=translation of a rule's
 * RHS) [<i>currently this is not checked</i>].
 * 
 * <p>
 * In the second case,
 * 
 * @author Sebastian Gabmeyer
 * 
 */
public final class ConsequentTranslator implements Translator<LatchBindings> {
  /**
   * @author Sebastian Gabmeyer
   * 
   */
  private final class LatchCollectingVisitor implements
      BooleanVisitor<InputCollector, InputCollector> {

    /*
     * (non-Javadoc)
     * 
     * @see
     * kodkod.engine.bool.BooleanVisitor#visit(kodkod.engine.bool.MultiGate,
     * java.lang.Object)
     */
    @Override
    public InputCollector visit(MultiGate multigate, InputCollector parent) {
      InputCollector collector = InputCollector.create(multigate, parent);
      if (isCached(multigate.label())) {
        final AigBuilder common = cache.get(multigate.label());
        assert common != null;
        collector.setCommon(common);
        return collector;
      }

      for (final BooleanFormula input : multigate) {
        InputCollector inputCollector = input.accept(this, collector);
        assert !collector.isRoot()
            || (inputCollector.containsLatchVars() ^ (inputCollector.containsCommons()));
        collector.collectVarsOrCommon(inputCollector);
      }
      if (collector.containsCommons())
        collector.assembleCommons();
      if (!collector.containsLatchVars()) {
        assert collector.containsCommons();
        cache(multigate.label(), collector.common());
      }
      if (collector.nop() == OR && collector.containsLatchVars())
        collector.assignCommonToVars();

      assert collector.containsLatchVars() ^ collector.containsCommons();
      return collector;
      // InputCollector collector = InputCollector.create(multigate, parent);
      // if (collector.op() == AND) {
      // for (final BooleanFormula input : multigate) {
      // final InputCollector inputCollector = input.accept(this,
      // collector);
      // assert !collector.isRoot() || inputCollector.containsLatchVars();
      // //if (collector.isRoot())
      // //inputCollector.exportLatches(cache);
      // //else
      // collector.collectVarsOrCommon(inputCollector);
      // }
      // collector.assembleCommons();
      // } else {
      // assert collector.op() == OR;
      // for (final BooleanFormula input : multigate) {
      // InputCollector inputCollector = input.accept(this, collector);
      // assert !collector.isRoot()
      // || (inputCollector.containsLatchVars() == (inputCollector.common() ==
      // null));
      // // if (collector.isRoot())
      // // inputCollector.exportLatches(cache);
      // // else
      // collector.collectVarsOrCommon(inputCollector);
      // }
      // collector.assembleCommons();
      // if (collector.op() == OR && collector.containsLatchVars())
      // collector.assignCommonToVars();
      // // }
      //
      // assert collector.containsLatchVars() ^ collector.containsCommons();
      // return collector;
    }

    /*
     * (non-Javadoc)
     * 
     * @see kodkod.engine.bool.BooleanVisitor#visit(kodkod.engine.bool.ITEGate,
     * java.lang.Object)
     */
    @Override
    public InputCollector visit(ITEGate ite, InputCollector parent) {
      InputCollector collector = InputCollector.create(ite, parent);
      // res.enterIf();
      assert collector != null;
      final BooleanFormula ifGate = ite.input(0);
      assert ifGate != null;
      InputCollector ifCollector = ifGate.accept(this, collector);
      assert ifCollector != null;
      assert !ifCollector.containsLatchVars();
      AigBuilder tmpCommon = collector.common();
      AigBuilder ifCommon = ifCollector.common();
      assert ifCommon != null;
      // collector.assembleCommons();

      InputCollector thenCollector = null;
      AigBuilder tmpThenCommon = null;
      if (ifCommon != AigBuilder.FALSE) {
        collector.setCommon(builderFactory.andGate(tmpCommon, ifCommon));
        thenCollector = ite.input(1).accept(this, collector);
        assert thenCollector != null;
        if (thenCollector.containsLatchVars() && thenCollector.op() != ITE) {
          collector.collectVars(thenCollector.latchVars());
          collector.assignCommonToVars();
          // build latch?? note: there could be more commons if the
          // ITE gate is
          // part of an OR gate
          // if any, build the commons, ie. this is the case if the
          // then gate
          // contains an OR gate... no the visitor of the OR gate
          // should build
          // the
          // commons such that the returned InputCollector contains a
          // single
          // common element and a single primedVar if any; in case the
          // THEN gate
          // contains an AND gate the

          // Note that the latch building process encompasses the
          // building steps
          // of a nested AND gate.
          // The latch building process is as follows:
          // for each primed var label l
          // if l > 0 then create
          // res1.buildCommons();
        } else if (thenCollector.containsCommons()
        /* && thenCollector.op() != ITE */) {
          collector.collectCommons(thenCollector);
          /* save commons for later */
          collector.assembleCommons();
          tmpThenCommon = collector.common();
          // TODO: add a tmpThenMemoryCommon
        }
      }
      InputCollector elseCollector = null;
      AigBuilder tmpElseCommon = null;
      if (ifCommon != AigBuilder.TRUE) {
        final AigBuilder nifCommon = ifCommon.not();
        collector.setCommon(builderFactory.andGate(tmpCommon, nifCommon));
        elseCollector = ite.input(2).accept(this, collector);
        assert elseCollector != null;
        assert thenCollector == null
            || thenCollector.containsLatchVars() == elseCollector.containsLatchVars();
        if (elseCollector.containsLatchVars() && elseCollector.op() != ITE) {
          collector.collectVars(elseCollector.latchVars());
          collector.assignCommonToVars();
        } else if (elseCollector.containsCommons()
        /* && elseCollector.op() != ITE */) {
          collector.collectCommons(elseCollector);
          /* save commons for later */
          collector.assembleCommons();
          tmpElseCommon = collector.common();
          // TODO: add a tmpElseMemoryCommon
        }
      }
      assert (thenCollector == null) || (elseCollector == null)
          || (tmpThenCommon != null) == (tmpElseCommon != null);
      assert (tmpThenCommon == null) == thenCollector.containsLatchVars();
      assert (tmpElseCommon == null) == elseCollector.containsLatchVars();

      collector.setCommon(builderFactory.andGate(tmpThenCommon, tmpElseCommon));
      assert collector.containsLatchVars() ^ collector.containsCommons();
      return collector;
    }

    /*
     * (non-Javadoc)
     * 
     * @see kodkod.engine.bool.BooleanVisitor#visit(kodkod.engine.bool.NotGate,
     * java.lang.Object)
     */
    @Override
    public InputCollector visit(NotGate negation, InputCollector parent) {
      final InputCollector collector = InputCollector.create(negation, parent);
      if (isCached(negation.label())) {
        final AigBuilder common = cache.get(negation.label());
        assert common != null;
        collector.setCommon(common);
        return collector;
      }

      assert !parent.isNegated() == collector.isNegated();

      final InputCollector inputCollector = negation.input(0).accept(this, collector);

      assert inputCollector.op() == NOT || inputCollector.isNegated() == collector.isNegated();
      assert !parent.isNegated() == collector.isNegated();

      collector.collectVarsOrCommon(inputCollector);

      if (!collector.containsLatchVars()) {
        assert collector.containsCommons();
        cache(negation.label(), collector.common());
      }
      return collector;
    }

    /*
     * (non-Javadoc)
     * 
     * @see kodkod.engine.bool.BooleanVisitor#visit(kodkod.engine.bool.
     * BooleanVariable , java.lang.Object)
     */
    @Override
    public InputCollector visit(BooleanVariable variable, InputCollector parent) {
      InputCollector collector = InputCollector.create(variable, parent);
      assert parent != null && (parent.hasRoot() || parent.isRoot()) : "Illegal consequent structure";
      final int label = variable.label();
      assert cache.contains(label);
      if (cache.isPostLabel(label)) {
        collector.collectVar(label);
      } else {
        // collector.collectCommon(cache.get(label));
        if (cache.hasMemoryLatch(label))
          collector.addCommons(cache.getInput(label), cache.getLatch(label));
        else
          collector.addCommon(cache.get(label));
        // final AigBuilder common = cache.get(label);
        // collector.collectCommons(common, null);
      }
      return collector;
    }
  }

  private final AigBuilderCache cache;
  private final AigBuilderFactory builderFactory;
  private final BoolTranslation circuit;

  ConsequentTranslator(AigBuilderFactory builderFactory, AigBuilderCache cache,
      BoolTranslation circuit) {
    this.cache = cache;
    this.builderFactory = builderFactory;
    InputCollector.init(builderFactory);
    this.circuit = circuit;
  }

  public LatchBindings translate(Collection<Formula> consequents) {
    LatchBindings allLatchVars = new LatchBindings(builderFactory);
    for (Formula c : consequents) {
      BooleanValue gate = circuit.mapping(c);
      allLatchVars.addAll(translate(gate));
    }
    return allLatchVars;
  }

  /**
   * @param consequent
   * @return <code>true</code> if the consequent was successfully translated and
   *         added to the respective latches.
   */
  private LatchBindings translate(final BooleanValue consequent) {
    if (consequent instanceof BooleanVariable)
      throw new MalformedGateException("consequent.op() == Operator.VAR");
    else if (consequent instanceof BooleanAccumulator)
      throw new MalformedGateException("consequent instanceof " + "BooleanAccumulator");
    else if (consequent instanceof BooleanConstant) {
      if (consequent == BooleanConstant.FALSE)
        System.out.println("WARNING: The consequent " + consequent
            + " evaluates to FALSE. This (usually) indicates an inconsitency "
            + "in the transition. Check the bounds of the affected relations.");
      /* return empty latch bindings */
      return new LatchBindings(builderFactory);
    } else {
      assert consequent instanceof BooleanFormula;

      // final List<BooleanFormula> worklist = new ArrayList<>();
      // BooleanFormula flattened;
      // worklist.add(consequent);
      // while (!worklist.isEmpty()) {
      // BooleanFormula f0 = worklist.remove(0);
      // if (f0.op() == AND) {
      // flattened = flatten(worklist, f0);
      // }
      // }
      InputCollector root = ((BooleanFormula) consequent).accept(new LatchCollectingVisitor(), null);
      return root.latchVars();
    }
  }

  /**
   * @param booleanValue
   * @param builderFactory
   * @return
   */
  private AigBuilder cache(final Integer label, final AigBuilder builder) {
    assert builder != null;
    cache.add(label, builder);
    return builder;
  }

  /**
   * @param label
   * @return
   */
  private boolean isCached(final Integer label) {
    return cache.contains(label);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.fol2aig.Translator#translateAll(java.util.Collection[])
   */
  @SuppressWarnings("unchecked")
  @Override
  public LatchBindings translateAll(Collection<Formula>... conditions) {
    throw new UnsupportedOperationException();
  }

  // /**
  // * @param worklist
  // * @param f0
  // * @return
  // */
  // BooleanFormula
  // flatten(final List<BooleanFormula> worklist, BooleanFormula f0) {
  // List<BooleanFormula> tmpWorklist = new ArrayList<>(f0.size());
  // for (BooleanFormula input : f0) {
  // if (input.op() == AND) {
  // tmpWorklist.add(input);
  // } else {
  //
  // }
  // }
  // return null;
  // }
}
