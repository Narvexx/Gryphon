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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public final class Enums {
  final Map<EEnum, Map<String, Integer>> enumNameValueTable;

  Enums() {
    enumNameValueTable = new IdentityHashMap<>();
  }

  void add(final EEnum e, final EList<EEnumLiteral> lits, final int minVal) {
    if (enumNameValueTable.containsKey(e))
      throw new IllegalArgumentException("EEnum e already added.");

    final Map<String, Integer> nameValTable = new HashMap<>(lits.size());
    int i = minVal;
    for (Iterator<EEnumLiteral> it = lits.iterator(); it.hasNext(); ++i) {
      nameValTable.put(it.next().getLiteral(), i);
    }
    enumNameValueTable.put(e, nameValTable);
  }

  public String lookupLitName(final EEnum e, final int litVal) {
    if (enumNameValueTable.containsKey(e)) {
      for (final Entry<String, Integer> entry : enumNameValueTable.get(e).entrySet()) {
        if (entry.getValue() == litVal)
          return entry.getKey();
      }
    }
    return null;
  }

  public Integer lookupLitValue(final EEnum e, final String litName) {
    if (enumNameValueTable.containsKey(e)) {
      final Map<String, Integer> nameValTable = enumNameValueTable.get(e);
      if (nameValTable.containsKey(litName))
        return nameValTable.get(litName);
    }
    return null;
  }

  /**
   * Retrieve the {@link EEnumLiteral literal} of the given <code>litName</code>
   * . Note that if the <code>litName</code> is unqualified, ie., without a
   * preceding enumeration name as in <code>eenumName#litName</code> or
   * <code>eenumName.litName</code>, the returned literal is taken from the
   * first enumeration in <code>this.enumMap</code> that contains a literal with
   * the given <code>litName</code>.
   * 
   * @param litName
   * @return
   */
  public EEnumLiteral lookupLiteral(final String litName) {
    final String[] qualified = litName.split("#\\.");
    if (qualified.length > 2)
      throw new IllegalArgumentException("The litName contains more than one "
          + "qualifying delimiter.");
    if (qualified.length > 1)
      return lookupLiteral(qualified);

    for (Entry<EEnum, Map<String, Integer>> enumNameValue : enumNameValueTable.entrySet()) {
      if (enumNameValue.getValue().containsKey(litName))
        return enumNameValue.getKey().getEEnumLiteral(litName);
    }
    return null;
  }

  /**
   * @param qualified
   * @return
   */
  private EEnumLiteral lookupLiteral(final String[] qualified) {
    assert qualified.length == 2;
    final String enumName = qualified[0];
    final String litName = qualified[1];
    return lookupLiteral(enumName, litName);
  }

  /**
   * @param enumName
   * @param litName
   * @return
   */
  public EEnumLiteral lookupLiteral(final String enumName, final String litName) {
    for (EEnum eenum : enumNameValueTable.keySet()) {
      if (eenum.getName().equals(enumName))
        return eenum.getEEnumLiteral(litName);
    }

    throw new UnsupportedOperationException();
  }

  /**
   * @return
   */
  Collection<EEnum> allEEnums() {
    return enumNameValueTable.keySet();
  }

  /**
   * @param e
   * @return
   */
  Collection<String> literalNames(final EEnum e) {
    if (enumNameValueTable.containsKey(e))
      return Collections.unmodifiableCollection(enumNameValueTable.get(e).keySet());
    return Collections.emptyList();
  }

  Collection<Integer> literalValues(final EEnum e) {
    if (enumNameValueTable.containsKey(e))
      return Collections.unmodifiableCollection(enumNameValueTable.get(e).values());
    return Collections.emptyList();
  }

  /**
   * @return
   */
  int size() {
    return enumNameValueTable.size();
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer();
    for (final EEnum eenum : enumNameValueTable.keySet()) {
      sb.append(eenum.getName());
      sb.append(": ");
      sb.append(enumNameValueTable.get(eenum));
      sb.append(System.lineSeparator());
    }
    return sb.toString();
  }

  // /**
  // * The value of a literal (obtained by {@link EEnumLiteral#getValue()}) is
  // * internally mapped to an integer value in the range of the verification
  // * problem's bitwidth. In particular, the first literal of an {@link EEnum}
  // is
  // * mapped internally to <code>-2^(b-1)</code>, ie., the minimum integer
  // value
  // * representable with <code>b</code> bits, the second literal to
  // * <code>-2^(b-1) + 1</code>, and so forth.
  // *
  // * @param literal
  // * @return
  // */
  // public int getInternalValue(final EEnumLiteral literal) {
  // final int offset = literal.getEEnum().getELiterals().indexOf(literal);
  //
  // return 0;
  // }
}
