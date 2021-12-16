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
import java.util.Iterator;

import org.modelevolution.aig.builders.AbstractAigBuilder;
import org.modelevolution.aig.builders.AigBuilder;
import org.modelevolution.aig.builders.AigBuilderFactory;
import org.modelevolution.aig.builders.LatchBuilder;

/**
 * @author Sebastian Gabmeyer
 * 
 */
class LatchBindings {
  private final class Binding {
    private final int label;
    private final Collection<AigBuilder> conditions;
    private final Collection<AigBuilder> memoryConditions;

    // private final boolean negated;

    /**
     * 
     */
    Binding(final int label) {
      this.label = label;
      conditions = new ArrayList<>();
      memoryConditions = new ArrayList<>();
    }

    /**
     * @param memoryCondition
     */
    public void addMemoryCondition(final AigBuilder memoryCondition) {
      memoryConditions.add(memoryCondition);
    }

    /**
     * @return
     */
    public Collection<AigBuilder> memoryConditions() {
      return memoryConditions;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      final StringBuffer sb = new StringBuffer();
      sb.append(label());
      sb.append(": ");
      appendAll(sb, conditions, ", ");
      return sb.toString();
    }

    void addCondition(final AigBuilder aigBuilder) {
      conditions.add(aigBuilder);
    }

    Collection<AigBuilder> conditions() {
      return conditions;
    }

    /**
     * Indicates whether this {@link Binding} has a negative polarity.
     * 
     * @return true if the latch var entry has negative polarity
     */
    boolean hasNegativePolarity() {
      return label < 0;
    }

    /**
     * Indicates whether this {@link Binding} has a positive polarity.
     * 
     * @return true if the latch var entry has positive polarity
     */
    boolean hasPositivePolarity() {
      return label > 0;
    }

    /**
     * @return the label of the post state of the associated latch
     */
    int label() {
      return label;
    }
  }

  private final Collection<Binding> entries;
  private final AigBuilderFactory builderFactory;
  private AigBuilder premise;
  private final Collection<LatchBindings> loops;

  // private AigBuilder constraints;

  private AigBuilder memoryPremise;

  LatchBindings(final AigBuilderFactory factory) {
    entries = new ArrayList<>();
    loops = new ArrayList<>();
    this.builderFactory = factory;
  }

  /**
   * @param loopBindings
   */
  public boolean addLoop(final LatchBindings loopBindings) {
    return loops.add(loopBindings);
  }

  /**
   * @param loopPremise
   * @return
   */
  public LatchBindings setPremise(final AigBuilder premise) {
    return setPremise(premise, AigBuilder.FALSE);
  }

  /**
   * @param memoryActivator
   * @param andGate
   * @return
   */
  public LatchBindings setPremise(final AigBuilder premise, final AigBuilder memoryActivator) {
    if (memoryActivator == AigBuilder.FALSE) {
      this.premise = premise;
      this.memoryPremise = AigBuilder.FALSE;
    } else {
      this.premise = builderFactory.andGate(premise, memoryActivator.not());
      this.memoryPremise = builderFactory.andGate(premise, memoryActivator);
    }
    return this;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer("Premise: ");
    sb.append(premise);
    sb.append("\n");
    sb.append("Entries:\n\t");
    appendAll(sb, entries, "\n\t");
    sb.append("\n");
    return sb.toString();
  }

  /**
   * @param sb
   * @param entries
   * @param seperator
   * @param i
   * @return
   */
  private <T> void appendAll(final StringBuffer sb, final Collection<T> entries,
      final String seperator) {
    int i = 0;
    for (final Iterator<T> it = entries.iterator(); it.hasNext(); i++) {
      sb.append(it.next());
      if (i < entries.size() - 1)
        sb.append(seperator);
    }
  }

  // /**
  // * @param premise
  // * @param constraints
  // * @return
  // */
  // LatchBindings setPremise(AigBuilder premise, AigBuilder constraints) {
  // this.premise = factory.andGate(constraints, premise);
  // // this.constraints = constraints;
  // return this;
  // }

  void add(final int label) {
    entries.add(new Binding(label));
  }

  /**
   * @param latchVars
   */
  void addAll(final LatchBindings latchVars) {
    this.entries.addAll(latchVars.entries);
  }

  void addCondition(final AigBuilder condition) {
    // assert !(op instanceof Nary) || multiOp == op;
    for (final Binding entry : entries)
      entry.addCondition(entry.hasPositivePolarity() ? condition.not() : condition);
  }

  void addCondition(final AigBuilder condition, final AigBuilder memoryCondition) {
    for (final Binding entry : entries) {
      entry.addCondition(entry.hasPositivePolarity() ? condition.not() : condition);
      entry.addMemoryCondition(entry.hasPositivePolarity() ? memoryCondition.not()
                                                          : memoryCondition);
    }
  }

  /**
   * @param cache
   * @return
   */
  boolean exportTo(final AigBuilderCache cache) {
    boolean success = true;
    for (final Binding entry : entries) {
      final int absLabel = Math.abs(entry.label());
      assert cache.isPostLabel(absLabel);
      final LatchBuilder latch = cache.getLatch(absLabel);
      if (latch.hasFixedNextState())
        continue;
      assert entry.label != 0;
      final AigBuilder consequent = builderFactory.andGate(entry.conditions());

      AigBuilder latchCondition = builderFactory.implicationGate(premise, consequent);

      if (memoryPremise != AigBuilder.FALSE) {
        final AigBuilder memoryConsequent = builderFactory.andGate(entry.memoryConditions());
        final AigBuilder memoryLatchCondition = builderFactory.implicationGate(memoryPremise,
                                                                               memoryConsequent);
        latchCondition = builderFactory.andGate(latchCondition, memoryLatchCondition);
      }

      if (entry.hasNegativePolarity())
        success &= latch.addNegativeInput(latchCondition);
      else
        success &= latch.addPositiveInput(latchCondition);
    }
    return success;
  }

  // Activator activator() {
  // return activator;
  // }
  //
  // /**
  // * @param activitor
  // */
  // void setActivator(final Activator activitor) {
  // this.activator = activitor;
  // }

  /**
   * @return
   */
  int size() {
    return entries.size();
  }
}