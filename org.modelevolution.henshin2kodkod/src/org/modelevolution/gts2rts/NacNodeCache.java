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

import kodkod.ast.Decl;
import kodkod.ast.Decls;

import org.eclipse.emf.henshin.model.Node;
import org.modelevolution.emf2rel.Signature;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class NacNodeCache extends NodeCache<NacNodeInfo> {
  private final Signature sig;
  private final Map<Node, NacNodeInfo> infos;
  private final Namer namer;

  /**
   * @param sig
   * @param infos
   */
  NacNodeCache(final Signature sig) {
    this.sig = sig;
    this.infos = new IdentityHashMap<>();
    this.namer = new Namer();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.henshin2kodkod.NodeCache#iterator()
   */
  @Override
  public Iterator<NacNodeInfo> iterator() {
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

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.henshin2kodkod.NodeCache#add(org.eclipse.emf.henshin
   * .model.Node)
   */
  @Override
  protected NacNodeInfo add(final Node node) {
    assert !infos.containsKey(node);
    final NacNodeInfo info = new NacNodeInfo(namer.name(node), node, sig.pre(node.getType()));
    final NacNodeInfo old = infos.put(node, info);
    assert old == null;
    return info;

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.henshin2kodkod.NodeCache#contains(org.eclipse.emf.henshin
   * .model.Node)
   */
  @Override
  protected boolean contains(final Node node) {
    return infos.containsKey(node);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.henshin2kodkod.NodeCache#get(org.eclipse.emf.henshin
   * .model.Node)
   */
  @Override
  protected NacNodeInfo get(final Node node) {
    assert infos.containsKey(node);
    return infos.get(node);
  }

  /**
   * @return
   */
  public Decls decls() {
    Iterator<NacNodeInfo> it = iterator();
    if (!it.hasNext())
      return null;
    Decls decls = it.next().decl();
    while (it.hasNext()) {
      decls = decls.and(it.next().decl());
    }
    return decls;
  }

}
