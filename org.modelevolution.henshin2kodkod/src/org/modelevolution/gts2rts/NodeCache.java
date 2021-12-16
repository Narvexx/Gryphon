/*
 * henshin2kodkod -- Copyright (c) 2014-present, Sebastian Gabmeyer
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

import static org.modelevolution.gts2rts.util.NodeUtil.isActionElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import kodkod.ast.LeafExpression;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.NestedCondition;
import org.eclipse.emf.henshin.model.Node;

/**
 * @author Sebastian Gabmeyer
 * @param <T>
 * 
 */
abstract class NodeCache<T extends NodeInfo<?>> implements Iterable<T> {
  protected static final class Namer {
    private static final String INFIX = ".";
    private static final String DEFAULT_SEPERATOR = "$";
    private static final String OTHER_SEPERATOR = "@@";
    private static final String NAC_SEPERATOR = "!";
    private static final String PAC_SEPERATOR = "?";
    private static final String NESTED_SEPERATOR = "*";

    private final Map<EClass, String> nameRegistry = new IdentityHashMap<>();
    private final Map<EClass, Integer> counter = new IdentityHashMap<>();

    /**
     * @param node
     * @return
     */
    private String infix(final Node node) {
      final StringBuffer sb = new StringBuffer();
      final Module module = node.getGraph().getRule().getModule();
      final int ruleIndex = module.getUnits().indexOf(node.getGraph().getRule());
      if (ruleIndex < 0)
        throw new AssertionError();
      sb.append(ruleIndex + 1).append(INFIX);
      if (node.getGraph().isNestedCondition()) {
        if (node.getGraph().getRule().getLhs().getNestedConditions().isEmpty())
          throw new AssertionError();
        final int nestedIndex = node.getGraph().getRule().getLhs().getNestedConditions()
                                    .indexOf(node.getGraph().eContainer());
        sb.append(nestedIndex + 1).append(INFIX);
      }
      return sb.toString();

    }

    /**
     * @param node
     * @return
     */
    private String seperator(final Node node) {
      if (node.getGraph().isLhs() || node.getGraph().isRhs())
        return DEFAULT_SEPERATOR;
      else if (node.getGraph().isNestedCondition()) {
        final NestedCondition nested = (NestedCondition) node.getGraph().eContainer();
        if (nested.isNAC())
          return NAC_SEPERATOR;
        else if (nested.isPAC())
          return PAC_SEPERATOR;
        else
          return NESTED_SEPERATOR;
      } else
        return OTHER_SEPERATOR;
    }

    String name(final Node node) {
      final EClass type = node.getType();
      String name;
      Integer cnt;
      if (nameRegistry.containsKey(type)) {
        name = nameRegistry.get(type);
        assert counter.containsKey(type) && counter.get(type) != null;
        cnt = counter.get(type);
      } else {
        name = type.getName();
        nameRegistry.put(type, name);
        cnt = 1;
      }
      final String seperator = seperator(node);
      final String infix = infix(node);
      name = new StringBuffer(name).append(seperator).append(infix).append(cnt).toString();
      cnt += 1;
      counter.put(type, cnt);
      return name;
    }

    void reset() {
      for (final Entry<EClass, Integer> e : counter.entrySet())
        e.setValue(1);
    }
  }

  private NodeCache<T> readOnlyCache;

  /**
   * @param nodeCache
   */
  private NodeCache(final NodeCache<T> cache) {
    this.readOnlyCache = cache;
  }

  /**
   * 
   */
  protected NodeCache() {

  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Iterable#iterator()
   */
  @Override
  public abstract Iterator<T> iterator();

  public abstract int size();

  /**
   * @return
   */
  public Collection<LeafExpression> variables() {
    final Collection<LeafExpression> vars = new ArrayList<>(size());
    for (final Iterator<T> iter = this.iterator(); iter.hasNext();) {
      final T info = iter.next();
      vars.add(info.variable());
    }
    return vars;
  }

  // /**
  // * @return
  // */
  // protected Collection<Relation> bindings() {
  // final Collection<Relation> bindings = new ArrayList<>(size());
  // for (final Iterator<T> iter = this.iterator(); iter.hasNext();) {
  // final T info = iter.next();
  // bindings.add(info.binding());
  // }
  // return bindings;
  // }

  protected abstract T add(final Node node);

  /**
   * Retrieve variable for given node or create and store new variable for given
   * node.
   * 
   * @param node
   */
  protected T cache(final Node node) {
    assert isActionElement(node) : node + " graph: " + node.getGraph() + " action-node: "
        + node.getActionNode();
    final T info;
    if (isChached(node))
      info = get(node);
    else
      info = add(node);
    return info;
  }

  protected abstract boolean contains(final Node node);

  protected abstract T get(final Node node);

  /**
   * @param node
   * @return
   */
  protected boolean isChached(final Node node) {
    return contains(node);
  }

  /**
   * Provides an unmodifiable view onto the {@link NodeCache}, adding a node to
   * the cache via its {@link #add(Node)} method throws an
   * {@link UnsupportedOperationException}.
   * 
   * @return an unmodifiable view onto the {@link NodeCache}.
   */
  protected NodeCache<T> readOnly() {
    return new NodeCache<T>(this) {

      @Override
      public Iterator<T> iterator() {
        return super.readOnlyCache.iterator();
      }

      @Override
      public int size() {
        return super.readOnlyCache.size();
      }

      @Override
      protected T add(final Node node) {
        throw new UnsupportedOperationException();
      }

      @Override
      protected boolean contains(final Node node) {
        return super.readOnlyCache.contains(node);
      }

      @Override
      protected T get(final Node node) {
        return super.readOnlyCache.get(node);
      }
    };
  }

  // private static final int DEFAULT_SIZE = 21;

  // private final Map<Node, T> infos;
  // private final Signature sig;

  // private final Map<EClass, TypeGraphGroup> typeGraphVarMap;
  // private NodeInfo multiNode;

  // /**
  // * Number of nested conditions (PACs/NACs) plus multi-rules.
  // */
  // private final int numOfNestedGraphs;
  // private int numOfMultiGraphs;

  // /**
  // * @return
  // */
  // public static NodeCache init(final Signature sig) {
  // return init(sig, DEFAULT_SIZE);
  // }

  // @Deprecated
  // static NodeCache init(Signature sig, final int numOfNodes) {
  // if (numOfNodes <= 0)
  // throw new IllegalArgumentException("Reason: size <= 0.");
  // Namer.reset();
  // return new NodeCache(sig, numOfNodes) {
  // @Override
  // protected NodeInfo cache(final Node node) {
  // assert isActionElement(node) : node + " graph: " + node.getGraph() +
  // " action-node: "
  // + node.getActionNode();
  // final NodeInfo info;
  // if (isChached(node)) {
  // info = get(node);
  // } else {
  // info = add(node);
  // }
  // return info;
  // }
  //
  // @Override
  // protected NodeInfo add(Node node) {
  // throw new UnsupportedOperationException();
  // }
  // };
  // }

  // /**
  // * Let's help the garbage collector, shall we?
  // */
  // private static void dispose() {
  // assert instance != null;
  // instance.infos.clear();
  // instance.typeGraphVarMap.clear();
  // }

  // static NodeCache instance() {
  // assert instance != null;
  // return instance;
  // }

  // /**
  // * @param sig
  // *
  // */
  // protected NodeCache(Signature sig, final int numOfNodes) {
  // // this(new IdentityHashMap<Node, NodeInfo>(numOfNodes));
  // this.sig = sig;
  // this.infos = new IdentityHashMap<Node, T>(numOfNodes);
  // }

  // /**
  // *
  // */
  // private NodeCache(Map<Node, NodeInfo> infos) {
  // this.infos = infos;
  // // this.typeGraphVarMap = varTypeMap;
  // // this.numOfNestedGraphs = numOfNestedGraphs;
  // // this.numOfMultiGraphs = numOfMultiGraphs;
  // // this.multiNodes = multiNodes;
  // }
}