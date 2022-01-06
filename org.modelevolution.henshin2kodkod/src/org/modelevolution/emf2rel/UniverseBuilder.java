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
package org.modelevolution.emf2rel;

import java.util.ArrayList;
import java.util.List;

import kodkod.ast.Relation;
import kodkod.instance.Universe;

import org.eclipse.emf.ecore.EClass;
import org.modelevolution.gts2rts.VariabilityRuleTranslator;

/**
 * @author Sebastian Gabmeyer
 * 
 */
final class UniverseBuilder {

  // private static final String TRUE = "-1";
  // private static final String FALSE = "0";
  private final ModelData mdata;
  private final List<String> features;
  private final int bitwidth;

  // private final Map<EClass, Integer> lowerObjBounds = new
  // IdentityHashMap<>();
  // private final Map<EObject, String> nameRegistry = new IdentityHashMap<>();
  // private final Map<EEnum, List<String>> enumBounds = new
  // IdentityHashMap<>();

  private UniverseBuilder(final ModelData mdata, final List<String> features, int bitwidth) {
    this.mdata = mdata;
    this.bitwidth = bitwidth;
    this.features = features;
  }

  public static Universe createUniverse(final ModelData mdata, final List<String> features) {
    if (mdata == null)
      throw new NullPointerException();
    final UniverseBuilder builder = new UniverseBuilder(mdata, features, mdata.bitwidth());
    return builder.univ();
  }

  // Map<EClass, Integer> lowerObjBounds() {
  // return lowerObjBounds;
  // }
  //
  // Map<EEnum, List<String>> enumBounds() {
  // return enumBounds;
  // }

  // void register(final EEnum eenum) {
  // final List<String> literals = new ArrayList<>(eenum.getELiterals().size());
  // for (final EEnumLiteral l : eenum.getELiterals()) {
  // literals.add(l.getName());
  // }
  // enumBounds.put(eenum, literals);
  // }

  // void register(final EClass clazz) {
  // lowerObjBounds.put(clazz, 0);
  // }

  Universe univ() {
    final List<Object> atoms = new ArrayList<>();

    // add integers
    if (mdata.hasIntsBoolsOrEnums()) {
      final int minInt = mdata.minInt();
      final int maxInt = mdata.maxInt();
      for (int i = minInt; i <= maxInt; ++i) {
        // atoms.add(String.valueOf(i));
        atoms.add(String.valueOf(i));
      }
      
      
    }
    //
    // // add booleans
    // if (mInfo.hasBools() && !mInfo.hasIntsBoolsOrEnums()) {
    // atoms.add(TRUE);
    // atoms.add(FALSE);
    // }

    // add classes
    for (final EClass c : mdata.classes()) {
      final int max = mdata.getUpperObjBound(c);
      assert max >= 0;
      for (int i = 1; i <= max; ++i) {
        atoms.add(c.getName() + i);
      }
    }
    
	for (final String f : features) {
    	atoms.add(f);
    }

    // // add enums
    // for (final EEnum e : mInfo.enums())
    // atoms.addAll(mInfo.literals(e));

    return new Universe(atoms);
  }

  // /**
  // * Create or retrieve the name for this <code>obj</code>.
  // *
  // * @param obj
  // * @return the name of this <code>obj</code>
  // */
  // String name(final EObject obj) {
  // final String name;
  // if (nameRegistry.containsKey(obj)) {
  // name = nameRegistry.get(obj);
  // } else {
  // final EClass clazz = obj.eClass();
  // final int cnt;
  // if (lowerObjBounds.containsKey(clazz))
  // cnt = lowerObjBounds.get(clazz) + 1;
  // else
  // cnt = 1;
  // name = clazz.getName() + cnt;
  // lowerObjBounds.put(clazz, cnt);
  // nameRegistry.put(obj, name);
  // }
  // return name;
  // }

  // /**
  // * @return
  // */
  // Map<EObject, String> nameRegistry() {
  // return nameRegistry;
  // }
}
