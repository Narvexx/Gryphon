/* 
 * KodkodMod -- Copyright (c) 2014-present, Sebastian Gabmeyer
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
package kodkodmod.engine.fol2sat;

import static kodkod.engine.bool.BooleanCreateCopyUpdateFactory.var;
import static kodkod.engine.bool.BooleanCreateCopyUpdateFactory.and;
import static kodkod.engine.bool.BooleanCreateCopyUpdateFactory.ite;
import static kodkod.engine.bool.BooleanCreateCopyUpdateFactory.negate;
import static kodkod.engine.bool.BooleanCreateCopyUpdateFactory.or;

import java.util.HashSet;
import java.util.Set;

import kodkod.engine.bool.BooleanFormula;
import kodkod.engine.bool.BooleanVariable;
import kodkod.engine.bool.ITEGate;
import kodkod.engine.bool.MultiGate;
import kodkod.engine.bool.NotGate;
import kodkod.engine.bool.Operator.Nary;
import kodkod.engine.fol2sat.NegationNormalFormer;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class NegationNormalFormTester {
  // private int label = 1;
  // private final BooleanVariable varA = var(label++);
  // private final BooleanVariable varB = var(label++);
  // private final BooleanVariable varC = var(label++);
  // private NegationNormalFormer nnf;
  //
  // @Before
  // public void setup() {
  // nnf = new NegationNormalFormer();
  // }
  //
  // @Test
  // public void testNNFwithVar() {
  // BooleanFormula result = varA.accept(new NegationNormalFormer(), false);
  // Assert.assertTrue(varA == result);
  // }
  //
  // @Test
  // public void testNNFwithNotVar() {
  // BooleanFormula not = negate(varA);
  // BooleanFormula res1 = not.accept(nnf, false);
  // BooleanFormula res2 = not.accept(nnf, true);
  // Assert.assertTrue(res1 == not);
  // Assert.assertTrue(res2 == varA);
  // }
  //
  // @Test
  // public void testNNFwithNotNotVar() {
  // final NotGate notA = negate(varA);
  // BooleanFormula notnotA = negate(notA);
  // BooleanFormula res1 = notnotA.accept(nnf, false);
  // BooleanFormula res2 = notnotA.accept(nnf, true);
  // Assert.assertTrue(res1 == varA);
  // Assert.assertTrue(res2 == notA);
  // }
  //
  // @Test
  // public void testNNFwithITEGate() {
  // final ITEGate ite = ite(label, 1, varA, varB, varC);
  // final BooleanFormula res1 = ite.accept(nnf, false);
  // Assert.assertTrue(res1==ite);
  // final BooleanFormula res2 = ite.accept(nnf, true);
  // Assert.assertTrue(res2 instanceof ITEGate);
  // Assert.assertTrue(res2.input(0)==varA);
  // Assert.assertEquals(-varB.label(), res2.input(1).label());
  // Assert.assertEquals(-varC.label(), res2.input(2).label());
  // }
  //
  // @Test
  // public void testNNFwithNotITEGate() {
  // final ITEGate ite = ite(label, 1, varA, varB, varC);
  // final BooleanFormula notITE = negate(ite);
  // final BooleanFormula res1 = notITE.accept(nnf, false);
  // Assert.assertTrue(res1 instanceof ITEGate);
  // Assert.assertTrue(res1.input(0)==varA);
  // Assert.assertEquals(-varB.label(), res1.input(1).label());
  // Assert.assertEquals(-varC.label(), res1.input(2).label());
  // final BooleanFormula res2 = notITE.accept(nnf, true);
  // Assert.assertTrue(res2==ite);
  // }
  //
  // @Test
  // public void testNNFwithAndGate() {
  // final MultiGate and = and(label,1,varA,varB);
  // final BooleanFormula res1 = and.accept(nnf, false);
  // Assert.assertTrue(res1==and);
  // final BooleanFormula res2 = and.accept(nnf, true);
  // Assert.assertTrue(res2 instanceof MultiGate);
  // Assert.assertEquals(and.size(),res2.size());
  // Assert.assertEquals(-and.input(0).label(), res2.input(0).label());
  // Assert.assertEquals(-and.input(1).label(), res2.input(1).label());
  // Assert.assertEquals(Nary.OR, res2.op());
  // }
  //
  // @Test
  // public void testNNFwithNotAndGate() {
  // final MultiGate and = and(label,1,varA,varB);
  // final BooleanFormula notAnd = negate(and);
  // final BooleanFormula res1 = notAnd.accept(nnf, false);
  // Assert.assertTrue(res1 instanceof MultiGate);
  // Assert.assertEquals(notAnd.input(0).size(),res1.size());
  // Assert.assertEquals(-and.input(0).label(), res1.input(0).label());
  // Assert.assertEquals(-and.input(1).label(), res1.input(1).label());
  // Assert.assertEquals(Nary.OR, res1.op());
  // final BooleanFormula res2 = notAnd.accept(nnf, true);
  // Assert.assertTrue(and==res2);
  // }
  //
  // @Test
  // public void testNNFwithOrGate() {
  // final MultiGate or = or(label,1,varA,varB);
  // final BooleanFormula res1 = or.accept(nnf, false);
  // Assert.assertTrue(res1==or);
  // final BooleanFormula res2 = or.accept(nnf, true);
  // Assert.assertTrue(res2 instanceof MultiGate);
  // Assert.assertEquals(or.size(),res2.size());
  // Assert.assertEquals(-or.input(0).label(), res2.input(0).label());
  // Assert.assertEquals(-or.input(1).label(), res2.input(1).label());
  // Assert.assertEquals(Nary.AND, res2.op());
  // }
  //
  // @Test
  // public void testNNFwithNotOrGate() {
  // final MultiGate or = or(label,1,varA,varB);
  // final BooleanFormula notOr = negate(or);
  // final BooleanFormula res1 = notOr.accept(nnf, false);
  // Assert.assertTrue(res1 instanceof MultiGate);
  // Assert.assertEquals(notOr.input(0).size(),res1.size());
  // Assert.assertEquals(-or.input(0).label(), res1.input(0).label());
  // Assert.assertEquals(-or.input(1).label(), res1.input(1).label());
  // Assert.assertEquals(Nary.AND, res1.op());
  // final BooleanFormula res2 = notOr.accept(nnf, true);
  // Assert.assertTrue(or==res2);
  // }
  //
  // @Test
  // public void testNNFwithNaryAndGate() {
  // final MultiGate and = and(label,1,varA,varB,varC);
  // final BooleanFormula res1 = and.accept(nnf, false);
  // Assert.assertTrue(res1==and);
  // final BooleanFormula res2 = and.accept(nnf, true);
  // Assert.assertTrue(res2 instanceof MultiGate);
  // Assert.assertEquals(and.size(),res2.size());
  // final Set<Integer> andInputs = inputs(and);
  // final Set<Integer> res2Inputs = inputs(res2);
  // for(int i = 0; i < and.size(); i++) {
  // Assert.assertTrue(andInputs.contains(-res2.input(i).label()));
  // Assert.assertTrue(res2Inputs.contains(-and.input(i).label()));
  // }
  // Assert.assertEquals(Nary.OR, res2.op());
  // }
  //
  // @Test
  // public void testNNFwithNotNaryAndGate() {
  // final MultiGate and = and(label,1,varA,varB,varC);
  // final NotGate notAnd = negate(and);
  // final BooleanFormula res1 = notAnd.accept(nnf, false);
  // Assert.assertTrue(res1 instanceof MultiGate);
  // Assert.assertEquals(and.size(),res1.size());
  // final Set<Integer> andInputs = inputs(and);
  // final Set<Integer> res2Inputs = inputs(res1);
  // for(int i = 0; i < and.size(); i++) {
  // Assert.assertTrue(andInputs.contains(-res1.input(i).label()));
  // Assert.assertTrue(res2Inputs.contains(-and.input(i).label()));
  // }
  // Assert.assertEquals(Nary.OR, res1.op());
  //
  // final BooleanFormula res2 = notAnd.accept(nnf, true);
  // Assert.assertTrue(res2==and);
  // }
  //
  // @Test
  // public void testNNFwithNaryOrGate() {
  // final MultiGate or = or(label,1,varA,varB,varC);
  // final BooleanFormula res1 = or.accept(nnf, false);
  // Assert.assertTrue(res1==or);
  // final BooleanFormula res2 = or.accept(nnf, true);
  // Assert.assertTrue(res2 instanceof MultiGate);
  // Assert.assertEquals(or.size(),res2.size());
  // final Set<Integer> andInputs = inputs(or);
  // final Set<Integer> res2Inputs = inputs(res2);
  // for(int i = 0; i < or.size(); i++) {
  // Assert.assertTrue(andInputs.contains(-res2.input(i).label()));
  // Assert.assertTrue(res2Inputs.contains(-or.input(i).label()));
  // }
  // Assert.assertEquals(Nary.AND, res2.op());
  // }
  //
  // @Test
  // public void testNNFwithNotNaryOrGate() {
  // final MultiGate or = or(label,1,varA,varB,varC);
  // final NotGate notOr = negate(or);
  // final BooleanFormula res1 = notOr.accept(nnf, false);
  // Assert.assertTrue(res1 instanceof MultiGate);
  // Assert.assertEquals(or.size(),res1.size());
  // final Set<Integer> andInputs = inputs(or);
  // final Set<Integer> res2Inputs = inputs(res1);
  // for(int i = 0; i < or.size(); i++) {
  // Assert.assertTrue(andInputs.contains(-res1.input(i).label()));
  // Assert.assertTrue(res2Inputs.contains(-or.input(i).label()));
  // }
  // Assert.assertEquals(Nary.AND, res1.op());
  //
  // final BooleanFormula res2 = notOr.accept(nnf, true);
  // Assert.assertTrue(res2==or);
  // }
  //
  // /**
  // * @param res2
  // * @return
  // */
  // private Set<Integer> inputs(BooleanFormula formula) {
  // final Set<Integer> res = new HashSet<>((formula.size()/3)*4);
  // for(int i=0; i<formula.size(); i++) {
  // res.add(formula.input(i).label());
  // }
  // return res;
  // }
}
