/*
 * KodkodMod -- Copyright (c) 2014-present, Sebastian Gabmeyer
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package kodkodmod.verification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.print.attribute.Size2DSyntax;

import kodkod.ast.Relation;
import kodkod.util.ints.IntSet;
import kodkod.util.ints.IntTreeSet;

/**
 * @author Sebastian Gabmeyer
 * 
 */
@Deprecated
public class StateVector {
  /**
   * Stores pairs of current and next state variables.
   */
  // private Map<StateVariable, StateVariable> stateMap;
  final private List<StateVariable> currentStates;
  final private List<StateVariable> nextStates;
  // private Map<String, StateVariable> nameStateMap = null;
  private final IntSet nextStateBoolVars;
  private int size = 0;

  // public static StateVector extract(Map<Relation, IntSet> relVars) {
  //
  // }

  public static StateVector create() {
    return new StateVector(new ArrayList<StateVariable>(), new ArrayList<StateVariable>());
  }

  static StateVector create(final Map<StateVariable, StateVariable> map) {
    return new StateVector(map);
  }

  private StateVector(final List<StateVariable> currentStates, final List<StateVariable> nextStates) {
    if (currentStates.size() != nextStates.size())
      throw new IllegalArgumentException("Reason: currentStates.size() != nextStates.size().");
    this.currentStates = currentStates;
    this.nextStates = nextStates;
    this.size = currentStates.size();
    this.nextStateBoolVars = new IntTreeSet();
  }

  /**
   * @param stateVars
   */
  private StateVector(final Map<StateVariable, StateVariable> stateVarMap) {
    // this.stateMap = stateVarMap;
    final int initialsize = stateVarMap.size() == 0 ? 10 : stateVarMap.size();
    currentStates = new ArrayList<>(initialsize);
    nextStates = new ArrayList<>(initialsize);
    this.nextStateBoolVars = new IntTreeSet();

    for (Entry<StateVariable, StateVariable> e : stateVarMap.entrySet()) {
      addState(e.getKey(), e.getValue());
    }
  }

  public List<StateVariable> currentStates() {
    return Collections.unmodifiableList(currentStates);
  }

  public List<StateVariable> nextStates() {
    return Collections.unmodifiableList(nextStates);
  }

  public Map<String, StateVariable> currentStateMap() {
    final Map<String, StateVariable> map = new HashMap<>((size * 4) / 3);
    for (StateVariable s : currentStates()) {
      map.put(s.name(), s);
    }
    return Collections.unmodifiableMap(map);
  }

  public Map<String, StateVariable> nextStateMap() {
    final Map<String, StateVariable> map = new HashMap<>((size * 4) / 3);
    for (StateVariable s : nextStates()) {
      map.put(s.name(), s);
    }
    return Collections.unmodifiableMap(map);
  }

  public Map<String, StateVariable> nameStateMap() {
    // if (nameStateMap == null) {
    // NB: We store both current and next state String-StateVariable pairs,
    // so we need to double the size of the map
    final Map<String, StateVariable> map = new HashMap<>((size * 2 * 4) / 3);
    map.putAll(currentStateMap());
    map.putAll(nextStateMap());
    // }
    return Collections.unmodifiableMap(map);
  }

  public boolean addState(StateVariable current, StateVariable next) {
    if (current == null || next == null)
      throw new IllegalArgumentException("Reason: current == null || next == null.");
    if (currentStates.add(current)) {
      if (!nextStates.add(next)) {
        currentStates.remove(currentStates.size() - 1);
        return false;
      }
      nextStateBoolVars.addAll(next.boolVars());
      size = size + 1;
      return true;
    } else {
      return false;
    }
  }

  public boolean isNextStateVar(int boolVar) {
    return nextStateBoolVars.contains(boolVar);
  }

  /**
   * The worst case execution time is linear in the size of this.currentStates + this.nextStates.
   * 
   * @param boolVar
   * @return the state associated with the given boolVar; <code>null</code> otherwise.
   */
  public StateVariable lookup(final int boolVar) {
    for (StateVariable s : currentStates) {
      if (s.boolVars().contains(boolVar)) return s;
    }
    for (StateVariable s : nextStates) {
      if (s.boolVars().contains(boolVar)) return s;
    }
    return null;
  }

  // /**
  // * @param relVars
  // */
  // public void updateStateVars(Map<Relation, IntSet> relVars) {
  // final Map<String, StateVariable> varMap = nameStateMap();
  // for(Relation r : relVars.keySet()) {
  // StateVariable var = null;
  // if((var = varMap.get(r.name())) != null || (r.name().startsWith("$") && (var =
  // varMap.get(r.name().substring(1))) != null)) {
  // var.update(r,relVars.get(r));
  // }
  // }
  // }
}
