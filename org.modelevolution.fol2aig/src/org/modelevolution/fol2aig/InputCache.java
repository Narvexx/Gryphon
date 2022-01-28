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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import kodkod.ast.LeafExpression;
import kodkod.ast.Relation;
import kodkod.engine.fol2sat.BoolTranslation;
import kodkod.util.ints.IntIterator;

import org.modelevolution.aig.builders.AbstractAigBuilder;
import org.modelevolution.aig.builders.AigBuilder;
import org.modelevolution.aig.builders.AigBuilderFactory;
import org.modelevolution.aig.builders.InputBuilder;
import org.modelevolution.gts2rts.VariabilityRuleTranslator;
import org.modelevolution.rts.BadState;
import org.modelevolution.rts.Property;
import org.modelevolution.rts.StateChanger;
import org.modelevolution.rts.TransitionRelation;

/**
 * @author Sebastian Gabmeyer
 * 
 */
final class InputCache {

  static InputCache create(final TransitionRelation transitions, final List<Property> properties,
      final BoolTranslation circuit, final AigBuilderFactory builderFactory) {
    final InputCache inputCache = new InputCache();
    for (final StateChanger t : transitions) {
      for (final LeafExpression var : t.variables()) {
        final IntIterator it = circuit.labels(var).iterator();
        while (it.hasNext()) {
          final Integer label = it.next();
          final InputBuilder input = builderFactory.createInputBuilder(label);
          inputCache.cache(input);
        }
      }
    }
    for (Property property : properties) {
      for (LeafExpression var : property.variables()) {
        IntIterator it = circuit.labels(var).iterator();
        while (it.hasNext()) {
          Integer label = it.next();
          InputBuilder input = builderFactory.createInputBuilder(label);
          inputCache.cache(input);
        }
      }
    }
    
    // Workaround: Cache the labels of the feature variables
    for (int label : VariabilityRuleTranslator.labelsCache) {
    	inputCache.cache(builderFactory.createInputBuilder(label));
    }

    return inputCache;
  }

  private Map<Integer, AigBuilder> internalCache;
  private int maxLabel = Integer.MIN_VALUE;

  private InputCache() {
    this.internalCache = new TreeMap<>();
  }

  AigBuilder cache(AigBuilder builder) {
    if (builder == null)
      throw new NullPointerException("builder == null.");

    final int label = builder.label();

    if (label < 0)
      throw new IllegalArgumentException("label < 0.");

    internalCache.put(label, builder);
    maxLabel = maxLabel < label ? label : maxLabel;
    return builder;
  }

  boolean contains(int label) {
    return internalCache.containsKey(label);
  }

  /**
   * @return
   */
  Collection<AigBuilder> inputs() {
    return internalCache.values();
  }

  int maxLabel() {
    return maxLabel;
  }

  /**
   * @param label
   * @return
   */
  AigBuilder get(int label) {
    assert internalCache.containsKey(label);
    return internalCache.get(label);
  }
}
