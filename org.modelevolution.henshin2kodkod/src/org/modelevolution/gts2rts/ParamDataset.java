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

import static org.modelevolution.gts2rts.util.EcoreUtil.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import kodkod.ast.IntExpression;
import kodkod.ast.Relation;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.henshin.model.Parameter;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public final class ParamDataset implements Iterable<ParamData> {
  // private static ParamDataset instance;
  private final Collection<ParamData> internal_set;

  static ParamDataset create(Collection<Parameter> params) {
    if (params == null)
      throw new NullPointerException();
    final ParamDataset dataset = new ParamDataset(params.size());
    for (final Parameter p : params) {
      final EClassifier paramType = p.getType();
      if (paramType == null || isEBoolean(paramType) || isEInt(paramType) || isEEnum(paramType))
        dataset.internal_set.add(new ParamData(p));
      else
        throw new IllegalStateException("Parameters of type '" + paramType + "' are not supported");
    }
    return dataset;
  }

  // static ParamDataset instance() {
  // assert instance != null;
  // return instance;
  // }

  private ParamDataset(final int size) {
    internal_set = new ArrayList<>(size);
  }

  public boolean contains(final String name) {
    for (ParamData data : internal_set) {
      if (data.name().equals(name))
        return true;
    }
    return false;
  }

  /**
   * @param id
   * @return
   */
  public ParamData paramDataByName(final String name) {
    for (ParamData data : internal_set) {
      if (data.name().equals(name))
        return data;
    }
    return null;
  }

  public ParamData paramData(final IntExpression expr) {
    final String prefix = new StringBuffer("sum(").append(ParamData.NAME_PREFIX).toString();
    assert expr.toString().startsWith(prefix);
    assert expr.toString().endsWith(")");
    final int endIndex = expr.toString().length() - 1; // exclude ")"
    final String name = expr.toString().substring(prefix.length(), endIndex);
    final ParamData param = paramDataByName(name);
    // We throw an error because this shouldn't happen if expr represents a
    // Variable converted to an
    // IntExpression
    if (param == null)
      throw new InternalError();
    return param;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Iterable#iterator()
   */
  @Override
  public Iterator<ParamData> iterator() {
    return internal_set.iterator();
  }

  /**
   * @return
   */
  Collection<? extends Relation> vars() {
    final Collection<Relation> vars = new ArrayList<>(internal_set.size());
    for (ParamData p : internal_set)
      vars.add(p.var());
    return vars;
  }
}
