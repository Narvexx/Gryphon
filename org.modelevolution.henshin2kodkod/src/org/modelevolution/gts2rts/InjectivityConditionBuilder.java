/*
 * henshin2kodkod -- Copyright (c) 2015-present, Sebastian Gabmeyer
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
package org.modelevolution.gts2rts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.LeafExpression;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.henshin.model.Graph;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Rule;

/**
 * @author Sebastian Gabmeyer
 * TODO: Make this class a listener of the NodeCache: once a NodeInfo gets added to the NodeCache call {@link #addInfo(NodeInfo)}. Make {@link #addInfo(NodeInfo)} private, remove {@link #addAll(Collection)}.
 */
class InjectivityConditionBuilder {
  private static class EmptyInjectivityConstraint extends InjectivityConditionBuilder {
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.modelevolution.henshin2kodkod.InjectivityConstraint#addAll(java.util
     * .Collection)
     */
    @Override
    InjectivityConditionBuilder addAll(final Collection<NodeInfo<?>> infos) {
      return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modelevolution.henshin2kodkod.InjectivityConstraint#add(org.
     * modelevolution.henshin2kodkod.NodeInfo)
     */
    @Override
    void addInfo(final NodeInfo<?> info) {
      return;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.modelevolution.henshin2kodkod.InjectivityConstraint#createLoopExtension
     * (org.eclipse.emf.henshin.model.Rule)
     */
    @Override
    InjectivityConditionBuilder createLoopExtension(final Rule loopRule) {
      return this;
    }
    
    /* (non-Javadoc)
     * @see org.modelevolution.gts2rts.InjectivityConditionBuilder#createNested(org.eclipse.emf.henshin.model.NestedCondition)
     */
    @Override
    InjectivityConditionBuilder createNested(NestedCondition nestedCondition) {
      return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modelevolution.henshin2kodkod.InjectivityConstraint#toFormula()
     */
    @Override
    Formula toFormula() {
      return Formula.TRUE;
    }

  }

  private class LoopInjectivityConditionBuilder extends InjectivityConditionBuilder {

    private final InjectivityConditionBuilder base;

    /**
     * @param base
     * @param loop
     */
    LoopInjectivityConditionBuilder(final InjectivityConditionBuilder base, final Rule loop) {
      super(loop);
      this.base = base;
    }

    @Override
    Collection<Formula> conditions() {
      final Collection<Formula> injs = new ArrayList<>();
      for (final EClass type : typeToGraphToVarMap.keySet()) {
        // final EClass type = typeEntry.getKey();
        final Expression[] baseLhsVars = base.collectLhsVars(type);
        final Expression[] lhsVars = collectLhsVars(type);
        for (int i = 0; i < baseLhsVars.length; i += 1)
          for (int j = 0; j < lhsVars.length; j += 1)
            injs.add(baseLhsVars[i].eq(lhsVars[j]).not());

        /*
         * Note that in case of a loop rule the RHS mappings of the base rule
         * need to be distinct from those of the loop rule; otherwise, the loop
         * rule would modify elements of the match of the base rule.
         */
        final Expression[] baseRhsVars = base.collectRhsVars(type);
        final Expression[] rhsVars = collectRhsVars(type);
        for (int i = 0; i < baseRhsVars.length; i += 1)
          for (int j = 0; j < rhsVars.length; j += 1)
            injs.add(baseRhsVars[i].eq(rhsVars[j]).not());

        final Set<Graph> typedGraphs = typeToGraphToVarMap.get(type).keySet();
        for (final Graph graph : typedGraphs) {
          final Expression[] vars = collectVars(type, graph);
          for (int i = 0; i < vars.length; ++i) {
            for (int j = i + 1; j < vars.length; ++j)
              injs.add(vars[i].eq(vars[j]).not());

            if (graph != lhs && lhsVars.length > 0)
              for (int j = 0; j < lhsVars.length; ++j)
                injs.add(lhsVars[j].eq(vars[i]).not());
            if (baseLhsVars.length > 0)
              for (int j = 0; j < baseLhsVars.length; ++j)
                injs.add(baseLhsVars[j].eq(vars[i]).not());
          }
        }
      }
      // for (Map<Graph, Collection<Expression>> gMap :
      // variableTypeMap.values()) {
      // }
      return injs;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modelevolution.henshin2kodkod.InjectivityConstraint#toFormula()
     */
    @Override
    Formula toFormula() {
      return Formula.and(conditions());
    }

    /**
     * @throws UnsupportedOperationException
     */
    @Override
    InjectivityConditionBuilder createLoopExtension(Rule loopRule) {
      throw new UnsupportedOperationException();
    }

    // /**
    // * @param type
    // * @return
    // */
    // private Expression[] collectBaseVars(final EClass type) {
    // final Collection<Expression> baseVars = new ArrayList<>(0);
    // if (base.variableTypeMap.containsKey(type)) {
    // final Map<Graph, Collection<Expression>> baseGraphMap =
    // base.variableTypeMap.get(type);
    // for (Collection<Expression> vars : baseGraphMap.values())
    // baseVars.addAll(vars);
    // }
    // return baseVars.toArray(new Expression[0]);
    // }
  }

  /**
   * @author Sebastian Gabmeyer
   * @invariant nestedTypeVarMap.keySet() \subset containerTypeVarMap.keySet();
   */
  private static class NestedInjectivityConditionBuilder extends InjectivityConditionBuilder {
    private final Map<EClass, List<LeafExpression>> nestedTypeVarMap;
    private final Map<EClass, List<LeafExpression>> containerTypeVarMap;
    private final NestedCondition nestedCondition;

    /**
     * @param nestedCondition
     * @param containerTypeVarMap
     */
    private NestedInjectivityConditionBuilder(NestedCondition nestedCondition,
        final Map<EClass, List<LeafExpression>> containerTypeVarMap) {
      this.nestedCondition = nestedCondition;
      this.containerTypeVarMap = containerTypeVarMap;
      this.nestedTypeVarMap = new IdentityHashMap<>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.modelevolution.henshin2kodkod.InjectivityConditionBuilder#addAll(
     * java.util.Collection)
     */
    @Override
    InjectivityConditionBuilder addAll(final Collection<NodeInfo<?>> infos) {
      for (final NodeInfo<?> info : infos)
        addInfo(info);

      return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.modelevolution.henshin2kodkod.InjectivityConditionBuilder#addInfo
     * (org.modelevolution.henshin2kodkod.NodeInfo)
     */
    @Override
    void addInfo(final NodeInfo<?> info) {
      final EClass type = info.node().getType();
      final List<LeafExpression> vars;
      if (nestedTypeVarMap.containsKey(type))
        vars = nestedTypeVarMap.get(type);
      else {
        vars = new ArrayList<>();
        nestedTypeVarMap.put(type, vars);
      }
      /* Ensure that the class invariant holds (see above) */
      if (!containerTypeVarMap.containsKey(type))
        containerTypeVarMap.put(type, Collections.<LeafExpression> emptyList());

      vars.add(info.variable());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.modelevolution.henshin2kodkod.InjectivityConditionBuilder#conditions
     * ()
     */
    @Override
    Collection<Formula> conditions() {
      final Collection<Formula> conditions = new ArrayList<>();
      for (final EClass type : nestedTypeVarMap.keySet()) {
        final List<LeafExpression> nestedVars = nestedTypeVarMap.get(type);
        final List<LeafExpression> containerVars = containerTypeVarMap.get(type);

        for (int i = 0; i < nestedVars.size(); ++i) {
          final Expression var1 = nestedVars.get(i);
          for (int j = i + 1; j < nestedVars.size(); ++j) {
            final Expression var2 = nestedVars.get(j);
            conditions.add(var1.eq(var2).not());
          }
          for (final Iterator<LeafExpression> iter = containerVars.iterator(); iter.hasNext();) {
            final Expression var2 = iter.next();
            conditions.add(var1.eq(var2).not());
          }
        }
      }
      return conditions;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.modelevolution.henshin2kodkod.InjectivityConditionBuilder#
     * createLoopExtension(org.eclipse.emf.henshin.model.Rule)
     */
    @Override
    InjectivityConditionBuilder createLoopExtension(final Rule loopRule) {
      throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.modelevolution.henshin2kodkod.InjectivityConditionBuilder#toFormula()
     */
    @Override
    Formula toFormula() {
      return Formula.and(conditions());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.modelevolution.henshin2kodkod.InjectivityConditionBuilder#createNested
     * (org.eclipse.emf.henshin.model.Graph)
     */
    @Override
    public InjectivityConditionBuilder createNested(final NestedCondition nestedCondition) {
      if (this.nestedCondition.getConclusion() != nestedCondition.getHost())
        throw new IllegalArgumentException();
      final Map<EClass, List<LeafExpression>> containerTypeVarMap = new IdentityHashMap<>();
      for (final EClass type : this.containerTypeVarMap.keySet()) {
        final List<LeafExpression> vars = new ArrayList<>(this.containerTypeVarMap.get(type));
        vars.addAll(this.nestedTypeVarMap.get(type));
        containerTypeVarMap.put(type, Collections.unmodifiableList(vars));
      }
      return new NestedInjectivityConditionBuilder(nestedCondition, containerTypeVarMap);
    }
  }

  /**
   * @param rule
   * @return
   */
  public static InjectivityConditionBuilder create(final Rule rule) {
    if (rule.isInjectiveMatching())
      return new InjectivityConditionBuilder(rule);
    else
      return new EmptyInjectivityConstraint();
  }

  private final Graph lhs;
  private final Graph rhs;
  private final Map<EClass, Map<Graph, List<LeafExpression>>> typeToGraphToVarMap;
  private Formula injectivityConstraint;
  private boolean modified;

  private Collection<Formula> conditions;

  private InjectivityConditionBuilder() {
    this.lhs = null;
    this.rhs = null;
    typeToGraphToVarMap = Collections.emptyMap();
    modified = false;
  }

  /**
   * @param rule
   * 
   */
  private InjectivityConditionBuilder(final Rule rule) {
    if (rule == null)
      throw new NullPointerException();
    this.lhs = rule.getLhs();
    this.rhs = rule.getRhs();
    typeToGraphToVarMap = new IdentityHashMap<>();
    modified = false;
  }

  /**
   * @param nestedCondition
   * @return
   */
  InjectivityConditionBuilder createNested(final NestedCondition nestedCondition) {
    final Graph container = nestedCondition.getHost();
    if (container == null)
      throw new NullPointerException("container == null.");

    final Map<EClass, List<LeafExpression>> containerTypeVarMap;
    containerTypeVarMap = new IdentityHashMap<>(typeToGraphToVarMap.size());

    for (final EClass type : typeToGraphToVarMap.keySet()) {
      final Map<Graph, List<LeafExpression>> graphVarMap = typeToGraphToVarMap.get(type);
      if (graphVarMap.containsKey(container))
        containerTypeVarMap.put(type, Collections.unmodifiableList(graphVarMap.get(container)));
      else
        containerTypeVarMap.put(type, Collections.<LeafExpression> emptyList());
    }
    return new NestedInjectivityConditionBuilder(nestedCondition, containerTypeVarMap);
  }

  // InjectivityConditionBuilder createNested() {
  // return createNested(lhs, nac);
  // }

  /**
   * @return
   */
  private Collection<Formula> buildInjectivityConditions() {
    final Collection<Formula> injs = new ArrayList<>();

    for (final EClass type : typeToGraphToVarMap.keySet()) {
      final Expression[] lhsVars = collectLhsVars(type);
      final Set<Graph> typedGraphs = typeToGraphToVarMap.get(type).keySet();
      for (final Graph graph : typedGraphs) {
        final Expression[] vars = collectVars(type, graph);
        for (int i = 0; i < vars.length; ++i) {
          for (int j = i + 1; j < vars.length; ++j)
            injs.add(vars[i].eq(vars[j]).not());

          if (graph != lhs && lhsVars.length > 0)
            for (int j = 0; j < lhsVars.length; ++j)
              injs.add(lhsVars[j].eq(vars[i]).not());
        }
      }
    }
    return injs;
  }

  /**
   * @param type
   * @param graphMap
   * @return
   */
  private Expression[] collectLhsVars(final EClass type) {
    return collectVars(type, lhs);
  }

  private Expression[] collectRhsVars(final EClass type) {
    return collectVars(type, rhs);
  }

  /**
   * @param type
   * @param graph
   * @param graphMap
   * @return
   */
  private Expression[] collectVars(final EClass type, final Graph graph) {
    final Map<Graph, List<LeafExpression>> graphMap = typeToGraphToVarMap.get(type);
    final Expression[] lhsVars;
    if (graphMap.containsKey(graph))
      lhsVars = graphMap.get(graph).toArray(new Expression[graphMap.get(graph).size()]);
    else
      lhsVars = new Expression[0];
    return lhsVars;
  }

  InjectivityConditionBuilder addAll(final Collection<NodeInfo<?>> infos) {
    for (final NodeInfo<?> info : infos)
      addInfo(info);

    return this;
  }

  void addInfo(final NodeInfo<?> info) {
    final Map<Graph, List<LeafExpression>> graphToVarMap;
    if (typeToGraphToVarMap.containsKey(info.type()))
      graphToVarMap = typeToGraphToVarMap.get(info.type());
    else {
      graphToVarMap = new IdentityHashMap<>();
      typeToGraphToVarMap.put(info.type(), graphToVarMap);
    }

    final Graph graph = info.node().getGraph();
    final List<LeafExpression> vars;
    if (graphToVarMap.containsKey(graph))
      vars = graphToVarMap.get(graph);
    else {
      vars = new ArrayList<>();
      graphToVarMap.put(graph, vars);
    }

    modified |= vars.add(info.variable());
  }

  Collection<Formula> conditions() {
    if (conditions == null || modified)
      conditions = buildInjectivityConditions();

    modified = false;
    return conditions;
  }

  InjectivityConditionBuilder createLoopExtension(final Rule loopRule) {
    return new LoopInjectivityConditionBuilder(this, loopRule);
  }

  Formula toFormula() {
    if (injectivityConstraint == null || modified)
      injectivityConstraint = Formula.and(conditions());

    modified = false;
    return injectivityConstraint;
  }
}
