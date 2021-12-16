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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.modelevolution.gts2rts;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

import kodkod.ast.Relation;
import kodkod.instance.TupleSet;

import org.eclipse.emf.henshin.model.Node;
import org.modelevolution.emf2rel.Signature;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class VarNodeCache extends NodeCache<VarNodeInfo> {

  private final Signature sig;
  private final Map<Node, VarNodeInfo> infos;
  private final Namer namer;

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.henshin2kodkod.NodeCache#add(org.eclipse.emf.henshin
   * .model.Node)
   */
  /**
   * @param sig
   * @param numOfNodes
   */
  protected VarNodeCache(final Signature sig, final int numOfNodes) {
    this.sig = sig;
    this.infos = new IdentityHashMap<Node, VarNodeInfo>(numOfNodes);
    this.namer = new Namer();
  }

  @Override
  protected VarNodeInfo add(Node node) {
    assert !infos.containsKey(node);
    final Relation binding = sig.pre(node.getType());
    final VarNodeInfo info = new VarNodeInfo(namer.name(node), node, binding);
    final VarNodeInfo old = infos.put(node, info);
    assert old == null;
    final TupleSet upperBound = sig.bounds().upperBound(binding);
    sig.bounds().bound((Relation)info.variable(), upperBound);
    return info;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.henshin2kodkod.NodeCache#get(org.eclipse.emf.henshin
   * .model.Node)
   */
  @Override
  protected VarNodeInfo get(Node node) {
    assert infos.containsKey(node);
    return infos.get(node);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.henshin2kodkod.NodeCache#contains(org.eclipse.emf.henshin
   * .model.Node)
   */
  @Override
  protected boolean contains(Node node) {
    return infos.containsKey(node);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.henshin2kodkod.NodeCache#iterator()
   */
  @Override
  public Iterator<VarNodeInfo> iterator() {
    return Collections.unmodifiableCollection(infos.values()).iterator();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.henshin2kodkod.NodeCache#size()
   */
  @Override
  public int size() {
    return infos.size();
  }
}
