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
package org.modelevolution.henshin2kodkod;

import static org.modelevolution.henshin2kodkod.TestHelpers.*;

import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import kodkod.ast.Formula;
import kodkod.ast.IntExpression;
import kodkod.ast.Relation;
import kodkod.ast.Variable;
import kodkod.ast.operator.ExprCastOperator;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.junit.Assert;
import org.junit.Test;
import org.modelevolution.emf2rel.FeatureMerger;
import org.modelevolution.emf2rel.Signature;
import org.modelevolution.gts2rts.RuleTranslator;
import org.modelevolution.gts2rts.util.HenshinLoader;
import org.modelevolution.rts.StateChanger;
import org.modelevolution.rts.TransitionRelation;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class TranslationTester {

  private static final String HENSHIN_FILE_NAME = "pacman_red.henshin";
  private static final String MODEL_PATH = "model/pacman";
  private static final String ECORE_MODEL_NAME = "pacman_red.ecore";;
  private static final String INSTANCE = "model/pacman/Game_red.xmi";
  private static final Integer UPPER_BOUND = 0;
  private static final int BITWIDTH = 0;
  private static final int ARRAYWIDTH = 0;

  @Test
  public void testTranslator() throws IOException {
    final HenshinLoader loader = new HenshinLoader(MODEL_PATH, HENSHIN_FILE_NAME);

    final EPackage model = loader.getMetamodels().get(0);
    final Signature sig = createSignature(model, INSTANCE);

    final List<Rule> rules = loader.getTransformations();
    TransitionRelation transitions = RuleTranslator.translateAll(sig, rules);
    assert transitions != null;
    assert transitions.size() == rules.size();
    for (StateChanger t : transitions) {
      System.out.println(t);
      System.out.println();
    }
  }

  private Signature createSignature(EPackage model, String instancePath) throws IOException {
    // final EPackage model = TestHelpers.loadModel(modelPath);
    final Resource instance = TestHelpers.loadInstance(model, instancePath);
    final Map<EClass, Integer> upperObjBounds = new IdentityHashMap<>();
    final FeatureMerger merger = getPacmanMerger(model);

    Signature sig = Signature.init(model, null, instance, upperObjBounds, merger, null, BITWIDTH);
    return sig;
  }

  @Test
  public void testVariableToString() {
    Variable var = Variable.unary("test");
    IntExpression expr = var.apply(ExprCastOperator.SUM);
    Assert.assertTrue(expr.toString().contains("test"));
    System.out.println(expr.toString());
    System.out.println(expr.toExpression().toString());
  }
}
