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
package org.modelevolution.aig.builders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.modelevolution.aig.AigExpr;
import org.modelevolution.aig.AigFactory;
import org.modelevolution.aig.AigFile;
import org.modelevolution.aig.AigLatch.AigLatchProxy;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class AigFileBuilder {
  /**
   * @author Sebastian Gabmeyer
   * 
   */

  private final Collection<AigBuilder> inputs;
  private final Collection<LatchBuilder> latches;
  private final Collection<OutputBuilder> outputs;
  private final Collection<BadBuilder> bads;
  private final Collection<InvBuilder> invariants;
  private AigFactory factory;

  /**
   * @param factory
   * 
   */
  public AigFileBuilder(AigFactory factory) {
    inputs = new ArrayList<>();
    latches = new ArrayList<>();
    outputs = new ArrayList<>();
    invariants = new ArrayList<>();
    bads = new ArrayList<>();
    this.factory = factory;
  }

  public void add(InputBuilder inputBuilder) {
    if (inputBuilder != null)
      inputs.add(inputBuilder);
  }

  public void add(LatchBuilder latchBuilder) {
    if (latchBuilder != null)
      latches.add(latchBuilder);
  }

  public void addLatches(Collection<LatchBuilder> latchBuilders) {
    if (latchBuilders != null)
      latches.addAll(latchBuilders);
  }

  public void add(OutputBuilder outputBuilder) {
    if (outputBuilder != null)
      outputs.add(outputBuilder);
  }

  public void add(InvBuilder invBuilder) {
    if (invBuilder != null)
      invariants.add(invBuilder);
  }

  public void add(BadBuilder badBuilder) {
    if (badBuilder != null)
      this.bads.add(badBuilder);
  }

  // public void add(AndBuilder andBuilder) {
  // if (andBuilder != null)
  // andGates.add(andBuilder);
  // }

  public AigFile toFile() {
    final List<AigExpr> aigInputs = new ArrayList<>(inputs.size());
    for (AigBuilder input : inputs)
      aigInputs.add(input.toAig());

    assert startsWith2(aigInputs) && incrementsBy2(aigInputs);
    // final AndCollector andCollector = new AndCollector();
    // final List<AigExpr> aigAnds = new ArrayList<>(latches.size());
    final List<AigBuilder> latchBuilders = initLatches(latches);
    // for (LatchBuilder latch : latches)
    // latchBuilders.add((LatchBuilder)latch.toAig());
    final List<AigExpr> aigLatches = new ArrayList<>(latches.size());
    for (AigBuilder latch : latchBuilders) {
      final AigExpr aigLatch = latch.toAig();

      /* Assert all latch.inputs() are cached */
      assert allLatchInputsAreCached(latch);
      aigLatches.add(aigLatch);
    }
    assert continuesIncrementAfter(aigLatches, aigInputs);
    assert incrementsBy2(aigLatches);

    final List<AigExpr> aigOutputs = new ArrayList<>(outputs.size());
    for (OutputBuilder output : outputs) {
      final AigExpr aigOutput = output.toAig();
      aigOutputs.add(aigOutput);
      // aigOutput.accept(andCollector, aigAnds);
    }

    final List<AigExpr> aigBads = new ArrayList<>(bads.size());
    for (BadBuilder badBuilder : bads) {
      final AigExpr aigBad = badBuilder.toAig();
      aigBads.add(aigBad);
      // aigBad.accept(andCollector, aigAnds);
    }

    final List<AigExpr> aigInvs = new ArrayList<>();
    for (InvBuilder invBuilder : invariants) {
      final AigExpr aigInv = invBuilder.toAig();
      aigInvs.add(aigInv);
      // aigInv.accept(andCollector, aigAnds);
    }

    final List<AigExpr> aigAnds = factory.andGates();
    assert continuesIncrementAfter(aigAnds, aigLatches);
    assert incrementsBy2(aigAnds);

    return new AigFile(aigInputs, aigLatches, aigOutputs, aigAnds, aigBads, aigInvs);
  }

  /**
   * @param latches
   * @return
   */
  private List<AigBuilder> initLatches(Collection<LatchBuilder> latches) {
    final List<AigBuilder> initLatches = new ArrayList<>(latches.size());
    for (AbstractLatchBuilder latch : latches) {
      final AigExpr expr = latch.toAig();
      assert !latch.hasFixedNextState() || (expr == AigExpr.TRUE || expr == AigExpr.FALSE);
      if (!latch.hasFixedNextState()) {
        assert expr instanceof AigLatchProxy;
        initLatches.add((AigLatchProxy) expr);
      }
    }
    return initLatches;
  }

  /**
   * This method is used only in assertions. Its purpose is to verify that all
   * of a latch's inputs are known to, i.e., cached in, the factory. If an input
   * is not cached, an {@link AssertionError} is thrown. If no
   * {@link AssertionError} is thrown, then <code>true</code> is returned by the
   * method.
   * 
   * @param latch
   * @return true, if not {@link AssertionError} is thrown
   * @throws AssertionError
   *           if the latch's input is not cached in the factory
   */
  private boolean allLatchInputsAreCached(AigBuilder latch) {
    latch.accept(new AigBuilderVisitor() {

      @Override
      public void visit(AndBuilder builder) {
        for (final AigBuilder input : builder.inputs()) {
          if (!factory.isCached(input))
            throw new AssertionError();
          input.accept(this);
        }
      }

      @Override
      public void visit(OrBuilder builder) {
        for (final AigBuilder input : builder.inputs()) {
          if (!factory.isCached(input))
            throw new AssertionError();
          input.accept(this);
        }
      }

      @Override
      public void visit(AbstractLatchBuilder builder) {
        for (final AigBuilder input : builder.inputs()) {
          if (!factory.isCached(input))
            throw new AssertionError();
          input.accept(this);
        }
      }

      @Override
      public void visit(BadBuilder builder) {
        for (final AigBuilder input : builder.inputs()) {
          if (!factory.isCached(input))
            throw new AssertionError();
          input.accept(this);
        }
      }

      @Override
      public void visit(InvBuilder builder) {
        for (final AigBuilder input : builder.inputs()) {
          if (!factory.isCached(input))
            throw new AssertionError();
          input.accept(this);
        }
      }

      @Override
      public void visit(InputBuilder builder) {
        return;
      }

      @Override
      public void visit(NotBuilder builder) {
        for (final AigBuilder input : builder.inputs()) {
          if (!factory.isCached(input))
            throw new AssertionError();
          input.accept(this);
        }
      }

      @Override
      public void visit(AbstractLiteralBuilder builder) {
        return;
      }

      @Override
      public void visit(OutputBuilder builder) {
        for (final AigBuilder input : builder.inputs()) {
          if (!factory.isCached(input))
            throw new AssertionError();
          input.accept(this);
        }
      }

      @Override
      public <T> void visit(Activator<T> builder) {
        if (builder.inputs().isEmpty())
          return;

        for (final AigBuilder input : builder.inputs()) {
          if (!factory.isCached(input))
            throw new AssertionError();
          input.accept(this);
        }
      }
    });
    return true;
  }

  /**
   * @param aigExprs
   * @return
   */
  private boolean startsWith2(List<AigExpr> aigExprs) {
    if (aigExprs == null)
      return false;
    if (aigExprs.isEmpty())
      return true;
    return aigExprs.get(0).literal() == 2;
  }

  /**
   * @param aigExprs
   * @return
   */
  private boolean incrementsBy2(List<AigExpr> aigExprs) {
    if (aigExprs == null)
      return false;
    if (aigExprs.isEmpty() || aigExprs.size() == 1)
      return true;
    assert aigExprs.size() > 1;
    boolean success = true;
    Iterator<AigExpr> i = aigExprs.iterator();
    // Iterator<AigExpr> j = aigInputs.iterator();
    AigExpr current = i.next();
    do {
      AigExpr next = i.next();
      success &= (current.literal() + 2) == (next.literal());
      current = next;
    } while (i.hasNext());
    return success;
  }

  /**
   * @param aigLatches
   * @param aigInputs
   * @return
   */
  private boolean continuesIncrementAfter(List<AigExpr> aigLatches, List<AigExpr> aigInputs) {
    if (aigLatches == null)
      return false;
    if (aigLatches.isEmpty())
      return true;
    final AigExpr firstLatch = aigLatches.get(0);
    if ((aigInputs == null || aigInputs.isEmpty()) && (firstLatch.literal() == 2))
      return true;
    else if ((aigInputs == null || aigInputs.isEmpty()) && (firstLatch.literal() != 2))
      return false;
    final AigExpr lastAigInput = aigInputs.get(aigInputs.size() - 1);
    return lastAigInput.literal() + 2 == firstLatch.literal();
  }

  /**
   * @param inputs2
   */
  public void addInputs(Collection<AigBuilder> inputBuilders) {
    if (inputBuilders != null)
      inputs.addAll(inputBuilders);
  }
}
