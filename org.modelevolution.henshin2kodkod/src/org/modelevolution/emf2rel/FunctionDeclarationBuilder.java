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

import static org.modelevolution.gts2rts.util.EcoreUtil.isEBoolean;
import static org.modelevolution.gts2rts.util.EcoreUtil.isEEnum;
import static org.modelevolution.gts2rts.util.EcoreUtil.isEInt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import kodkod.ast.Formula;
import kodkod.ast.Relation;

/**
 * @author Sebastian Gabmeyer
 * 
 */
@Deprecated
public class FunctionDeclarationBuilder {
  private final Collection<EStructuralFeature> totalFunctions;
  private final Collection<EStructuralFeature> partialFunctions;
  private final StateRegistry registry;

  /**
   * @param totalFunctions
   * @param partialFunctions
   */
  FunctionDeclarationBuilder(StateRegistry registry, Collection<EStructuralFeature> totalFunctions,
      Collection<EStructuralFeature> partialFunctions) {
    this.registry = registry;
    this.totalFunctions = totalFunctions;
    this.partialFunctions = partialFunctions;
  }

  private abstract class FuncDefCommand {
    abstract Formula funcDef(Relation r, Relation domain, Relation range);
  }

  public Formula functions() {
    // for each functional feature we create two function
    // definition, one for the pre-relation and one for
    // the post-relation
    final int size = (totalFunctions.size() + partialFunctions.size()) * 2;
    List<Formula> funcDefs = new ArrayList<>(size);
    // Creates total-function-definition formulas
    final FuncDefCommand total = new FuncDefCommand() {
      @Override
      Formula funcDef(Relation r, Relation domain, Relation range) {
        return r.function(domain, range);
      }
    };
    funcDefs.addAll(buildFuncDefs(totalFunctions, total));

    // Creates partial-function-definition formulas
    final FuncDefCommand partial = new FuncDefCommand() {

      @Override
      Formula funcDef(Relation r, Relation domain, Relation range) {
        return r.partialFunction(domain, range);
      }
    };
    funcDefs.addAll(buildFuncDefs(partialFunctions, partial));
    return Formula.and(funcDefs);
  }

  /**
   * @param funcs
   * @param cmd
   * @return
   * @throws InternalError
   */
  private Collection<Formula> buildFuncDefs(Collection<EStructuralFeature> funcs, FuncDefCommand cmd)
      throws InternalError {
    final List<Formula> funcFormulas = new ArrayList<>(funcs.size());
    // for (final EStructuralFeature feature : funcs) {
    // final StateRelation r = registry.prePost(feature);
    // final StateRelation domain = registry.prePost(feature
    // .getEContainingClass());
    // final StateRelation range = rangeOfRelation(feature);
    // final Formula def1 = cmd.funcDef(r.preState(), domain.preState(),
    // range.postState());
    // final Formula def2 = cmd.funcDef(r.postState(), domain.postState(),
    // range.postState());
    // funcFormulas.add(def1);
    // funcFormulas.add(def2);
    // }
    return funcFormulas;
  }

  /**
   * @param feature
   * @return
   * @throws InternalError
   */
  private StateRelation rangeOfRelation(final EStructuralFeature feature) throws InternalError {
    final StateRelation range;
    if (feature instanceof EReference || isEEnum(feature)) {
      range = registry.state(feature.getEType());
    } else {
      assert feature instanceof EAttribute;
      if (isEBoolean((EAttribute) feature) || isEInt((EAttribute) feature))
        range = registry.state(((EAttribute) feature).getEAttributeType());
      // else if (isEInt((EAttribute) feature))
      // range = registry.prePost(((EAttribute) feature).getEAttributeType());
      else
        throw new InternalError(); // this shouldn't be reachable
    }
    return range;
  }
}
