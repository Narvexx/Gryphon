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
package org.modelevolution.rts;

import java.util.Collection;

import kodkod.ast.Formula;
import kodkod.ast.LeafExpression;

import org.modelevolution.gts2rts.RuleTranslation;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public final class PropertyFactory {

  /**
   * @param vars
   * @param propertyTranslation
   *          TODO
   * @param conditions
   *          TODO
   * @param decls
   *          TODO
   * @param injectivity
   *          TODO
   * @param name
   * @param varDecls
   * @param and
   * @return
   */
  public static Property create(final String name, final Collection<LeafExpression> vars,
      final RuleTranslation propertyTranslation) {
    final Collection<Formula> propConditions = propertyTranslation.premises();
    final Collection<Formula> declarations = propertyTranslation.declarations();
    final Collection<Formula> injectivities = propertyTranslation.injectivityConditions();
    if (name == null)
      throw new NullPointerException();
    if (propConditions == null)
      throw new NullPointerException();
    if (declarations == null)
      throw new NullPointerException();
    if (injectivities == null)
      throw new NullPointerException();

    final String canonic = name.toLowerCase();
    if (canonic.startsWith(Invariant.PREFIX)) {
      throw new UnsupportedOperationException();
      // return new Invariant(unprefix(name, Invariant.PREFIX), vars,
      // declarations, propConditions,
      // injectivities);
    } else if (canonic.startsWith(BadState.PREFIX)) {
      return new BadState(unprefix(name, BadState.PREFIX), vars, declarations,
                             propConditions, injectivities);
    } else if (canonic.startsWith(GoodState.PREFIX)) {
      return new GoodState(unprefix(name, GoodState.PREFIX), vars, declarations,
                              propConditions, injectivities);
    } else {
      throw new IllegalArgumentException("Unknown property type.");
    }
  }

  /**
   * @param propName
   * @param prefix
   *          TODO
   * @return
   */
  private static String unprefix(String propName, String prefix) {
    return propName.substring(prefix.length());
  }
}
