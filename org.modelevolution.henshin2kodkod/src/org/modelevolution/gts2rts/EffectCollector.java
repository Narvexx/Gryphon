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

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.Relation;

import org.modelevolution.emf2rel.Signature;
import org.modelevolution.emf2rel.StateRelation;

/**
 * This class acts as a lookup table for modifications performed on relations.
 * It records all create and delete modification associated with a given
 * {@link StateRelation pair of relations} .
 * 
 * @author Sebastian Gabmeyer
 * 
 */
class EffectCollector {

  /**
   * 
   */
  private final Map<StateRelation, Modificator> modRegistry;
  private final Signature sig;
  // private Collection<Formula> attributeMods;
  // private Collection<NodeInfo> infos;
  private boolean modified = false;
  // private Set<StateRelation> affected;
  private Collection<Formula> effects = null;

  // static EffectCollector init(int expectedSize) {
  // instance = new EffectCollector(expectedSize);
  // return instance;
  // }

  // static EffectCollector baseModCollector() {
  // if (instance == null)
  // return init(DEFAULT_SIZE);
  // else
  // return instance;
  // }

  /**
   * Copy constructor.
   * 
   * @param collector
   */
  private EffectCollector(final EffectCollector collector) {
    modRegistry = new IdentityHashMap<>(collector.modRegistry);
    sig = collector.sig;
    // affected = new HashSet<>();
  }

  /**
   * @param sig
   */
  EffectCollector(Signature sig) {
    this.sig = sig;
    modRegistry = new IdentityHashMap<>(sig.numRelations());
    // attributeMods = new ArrayList<>();
    // infos = new ArrayList<>();
    // affected = new HashSet<>();
  }

  /**
   * Construct a Create modification for the given {@link StateRelation pair};
   * the modification is captured by the <code>createExpr</code>ession.
   * 
   * @param tuple
   * @param info
   */
  void addCreateMod(final StateRelation state, final Expression tuple) {
    final Modificator mod = getMod(state);

    // if (mod.addCreateExpr(tuple)) {
    // state.setModified(true);
    // for (StateRelation superState : sig.superStates(state))
    // superState.setModified(true);
    modified |= mod.addCreateExpr(tuple);
    // }
  }

  /**
   * @param deleteExpr
   */
  void addDeleteMod(final StateRelation state, final Expression deleteExpr) {
    final Modificator mod = getMod(state);
    // if (mod.addDeleteExpr(deleteExpr)) {
    // state.setModified(true);
    // for (StateRelation superState : sig.superStates(state))
    // superState.setModified(true);
    modified |= mod.addDeleteExpr(deleteExpr);
    // }
  }

  /**
   * @param src2attr
   * @param tuple
   */
  void addOverrideMod(StateRelation state, Expression tuple) {
    Modificator mod = getMod(state);
    // if (mod.addOverrideExpr(tuple)) {
    // state.setModified(true);
    // for (StateRelation superState : sig.superStates(state))
    // superState.setModified(true);
    modified |= mod.addOverrideExpr(tuple);
    // }
  }

  /**
   * @param state
   * @return
   */
  private Modificator getMod(final StateRelation state) {
    final Modificator mod;
    if (modRegistry.containsKey(state)) {
      mod = modRegistry.get(state);
    } else {
      mod = new Modificator(state);
      state.setModified(true);
      for (StateRelation superState : sig.superStates(state))
        superState.setModified(true);
      modRegistry.put(state, mod);
    }
    return mod;
  }

  // /*
  // * (non-Javadoc)
  // *
  // * @see java.lang.Iterable#iterator()
  // */
  // @Override
  // public Iterator<RelationModifier> iterator() {
  // return modRegistry.values().iterator();
  // }

  /**
   * @return the number of modified relations
   */
  private int size() {
    return modRegistry.size();
  }

  /**
   * @return
   */
  Formula toFormula() {
    return Formula.and(conditions());
  }

  /**
   * @return
   * @deprecated unused
   */
  @Deprecated
  EffectCollector loopCollector() {
    return new EffectCollector(this);
  }

  // /**
  // * @param attrAssignEffect
  // */
  // void addAttrMod(Formula attrMod) {
  // attributeMods.add(attrMod);
  // }

  // /**
  // * @return
  // * @deprecated unused: Marked for deletion
  // */
  // @Deprecated()
  // Collection<? extends NodeInfo> infos() {
  // return infos;
  // }

  // /**
  // * @param info
  // * @deprecated replaced by {@link
  // InjectivityConditionBuilder.addRhsInfo(NodeInfo)}
  // */
  // @Deprecated
  // void addInfo(final NodeInfo info) {
  // infos.add(info);
  // }

  /**
   * @return
   */
  Collection<Formula> conditions() {
    if (modified || effects == null) {
      effects = new ArrayList<>(2 * size());
      // affected = modRegistry.keySet();

      for (Modificator mod : modRegistry.values()) {

        Expression dels = null;
        if (!mod.deleteExprs().isEmpty())
          dels = Expression.union(mod.deleteExprs());

        Expression adds = null;
        if (!mod.createExprs().isEmpty())
          adds = Expression.union(mod.createExprs());
        if (mod.state().isNode()) {
          /*
           * For each new "node object" we need to make sure that it really is
           * new and not contained in the (lower) bounds of the pre-state
           * relation.
           */
          for (Expression var : mod.createExprs()) {
            effects.add(var.one());
            effects.add(var.in(mod.pre()).not());
          }
        }

        Expression overs = null;
        if (!mod.overrideExprs().isEmpty())
          overs = Expression.union(mod.overrideExprs());

        final Relation pre = mod.pre();
        final Expression modExpr;
        if (dels == null) {
          if (adds == null) {
            if (overs == null)
              modExpr = pre;
            else
              modExpr = pre.override(overs);
          } else {
            if (overs == null)
              modExpr = pre.union(adds);
            else
              modExpr = pre.union(adds).override(overs);
          }
        } else {
          if (adds == null) {
            if (overs == null)
              modExpr = pre.difference(dels);
            else
              modExpr = pre.difference(dels).override(overs);
          } else {
            if (overs == null)
              modExpr = pre.difference(dels).union(adds);
            else
              modExpr = pre.difference(dels).union(adds).override(overs);
          }
        }

        final Relation post = mod.post();
        final Formula effect = post.eq(modExpr);
        effects.add(effect);

        for (StateRelation superState : sig.superStates(mod.state())) {
          Formula superEffect = superState.postState().eq(superState.preState().difference(dels)
                                                                    .union(adds));
          effects.add(superEffect);
          // superState.setModified(true);
          // affected.add(superState);
        }
      }
      for (final StateRelation r : sig.states()) {
        if (r.isStatic() || this.modifies(r) /* affected.contains(r) */)
          continue;
        effects.add(r.postState().eq(r.preState()));
      }
      modified = false;
    }
    // effects.addAll(attributeMods);
    return effects;
  }

  /**
   * @param r
   * @return
   */
  private boolean modifies(final StateRelation r) {
    return modRegistry.containsKey(r);
  }

  // public Set<StateRelation> affected() {
  // if (modified) {
  // affected = new HashSet<>(modRegistry.keySet());
  // for (StateRelation state : modRegistry.keySet()) {
  // for (StateRelation superState : sig.superStates(state))
  // affected.add(superState);
  // }
  // }
  // return affected;
  // }
}