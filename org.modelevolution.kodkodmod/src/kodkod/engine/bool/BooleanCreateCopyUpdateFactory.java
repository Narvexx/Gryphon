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
package kodkod.engine.bool;

import kodkod.engine.bool.Operator.Nary;

/**
 * @author Sebastian Gabmeyer
 *
 */
public final class BooleanCreateCopyUpdateFactory {
  public static ITEGate copyUpdate(final ITEGate old, final BooleanFormula newIf, final BooleanFormula newThen, final BooleanFormula newElse) {
    int hashCode = old.hashCode();
    int label = old.label();
    return new ITEGate(label, hashCode, newIf, newThen, newElse);
  }
  
//  public static MultiGate copyUpdate(final MultiGate old, final Nary op, final BooleanFormula l, final BooleanFormula h) {
//    int hashCode = old.hashCode();
//    int label = old.label();
//    return new BinaryGate(op, label, hashCode, l, h);
//  }
//  
//  public static MultiGate copyUpdate(final MultiGate old, final BooleanAccumulator acc) {
//    int hashCode = old.hashCode();
//    int label = old.label();
//    return new NaryGate(acc, label, hashCode);
//  }
  
  /**
   * @param multigate
   * @param op
   * @param inputs
   * @return
   */
  public static MultiGate copyUpdate(final MultiGate multigate, final Nary op, final BooleanFormula[] inputs) {
    if(inputs.length > 2) {
      BooleanAccumulator acc = BooleanAccumulator.treeGate(op, inputs);
      return new NaryGate(acc, multigate.label(), multigate.hashCode());
    }
    assert inputs.length == 2;
    return new BinaryGate(op, multigate.label(), multigate.hashCode(), inputs[0], inputs[1]);
  }
  
  public static BooleanVariable var(final int label) {
    return new BooleanVariable(label);
  }
  
  public static NotGate negate(BooleanFormula f) {
    return new NotGate(f);
  }
  
  public static MultiGate and(final int label, final int hashcode, final BooleanFormula l, final BooleanFormula h) {
    return new BinaryGate(Nary.AND, label, hashcode, l, h);
  }
  
  public static MultiGate and(final int label, final int hashcode, final BooleanFormula... inputs) {
    return new NaryGate(BooleanAccumulator.treeGate(Nary.AND, inputs), label, hashcode);
  }
  
  public static MultiGate or(final int label, final int hashcode, final BooleanFormula l, final BooleanFormula h) {
    return new BinaryGate(Nary.OR, label, hashcode, l, h);
  }
  
  public static MultiGate or(final int label, final int hashcode, final BooleanFormula... inputs) {
    return new NaryGate(BooleanAccumulator.treeGate(Nary.OR, inputs), label, hashcode);
  }
  
  public static ITEGate ite(final int label, final int hashcode, final BooleanFormula ifFormula, final BooleanFormula thenFormula, final BooleanFormula elseFormula) {
    return new ITEGate(label, hashcode, ifFormula, thenFormula, elseFormula);
  }
}
