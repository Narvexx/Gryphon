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

import static org.modelevolution.gts2rts.util.RuleUtil.isRootRule;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kodkod.ast.LeafExpression;
import kodkod.ast.Relation;
import kodkod.ast.Variable;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.henshin.model.Parameter;
import org.eclipse.emf.henshin.model.Rule;

public final class ParamData {
  /**
   * 
   */
  static final String NAME_PREFIX = "@";
  private final Parameter param;
  // private final String paramName;
  private final Relation paramVar;
  private boolean rootRuleUseFound;
  private Collection<Rule> usedBy;

  // Set<ParamData> createAll(final List<Parameter> params) {
  // return null;
  // }

  ParamData(final Parameter param) {
    this.param = param;
    final String name = new StringBuffer(NAME_PREFIX).append(param.getName()).toString();
    this.paramVar = Relation.unary(name);
    this.rootRuleUseFound = false;
    this.usedBy = new HashSet<>();
  }

  public String name() {
    return param.getName();
  }

  public Relation var() {
    return paramVar;
  }

  /**
   * Get the {@link EClassifier type} of the parameter.
   * 
   * @return the type of the parameter
   */
  public EClassifier type() {
    return param.getType();
  }

  /**
   * Set the type of the parameter to <code>type</code>.
   * 
   * @param type
   */
  public void type(final EClassifier type) {
    param.setType(type);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ParamData) {
      ParamData other = (ParamData) obj;
      if (this.name().equals(other.name()))
        return true;
      return false;
    }
    return super.equals(obj);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return name().hashCode();
  }

  /**
   * @param rule
   */
  public void setUsedByRule(final Rule rule) {
    if (!rootRuleUseFound && rule != null) {
      if (isRootRule(rule)) {
        rootRuleUseFound = true;
        usedBy.clear();
      }
      usedBy.add(rule);
    }
  }

  public boolean isUsedByRule(final Rule rule) {
    return usedBy.contains(rule);
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuffer sb =new StringBuffer(param.getName());
    sb.append(":");
    if (type()!= null)
      sb.append(type().getName());
    else
      sb.append("??");
    sb.append(" (");
    sb.append(var().name());
    sb.append(")");
    return sb.toString();
  }

  // void type(final EClassifier value) {
  // param.setType(value);
  // }
}