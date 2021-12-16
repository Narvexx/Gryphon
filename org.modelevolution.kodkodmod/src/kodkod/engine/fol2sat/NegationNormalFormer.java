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
package kodkod.engine.fol2sat;

import static kodkod.engine.bool.BooleanCreateCopyUpdateFactory.copyUpdate;
import static kodkod.engine.bool.BooleanCreateCopyUpdateFactory.negate;
import kodkod.engine.bool.BooleanFormula;
import kodkod.engine.bool.BooleanVariable;
import kodkod.engine.bool.BooleanVisitor;
import kodkod.engine.bool.ITEGate;
import kodkod.engine.bool.MultiGate;
import kodkod.engine.bool.NotGate;
import kodkod.engine.bool.Operator.Nary;

/**
 * @author Sebastian Gabmeyer
 *
 */
@Deprecated
public final class NegationNormalFormer implements BooleanVisitor<BooleanFormula, Boolean> {

  /* (non-Javadoc)
   * @see kodkod.engine.bool.BooleanVisitor#visit(kodkod.engine.bool.MultiGate, java.lang.Object)
   */
  @Override
  public BooleanFormula visit(MultiGate multigate, Boolean neg) {
    final Nary op = (neg ? multigate.op().complement() : multigate.op());
    boolean changed = neg;
    final BooleanFormula[] inputs = new BooleanFormula[multigate.size()];
    for (int i = 0; i < multigate.size(); i++) {
      BooleanFormula f = multigate.input(i).accept(this, neg);
      inputs[i] = f;
      changed = changed || (multigate.input(i) != f);
    }
    if(!changed) return multigate;
    return copyUpdate(multigate, op, inputs);
  }

  /* (non-Javadoc)
   * @see kodkod.engine.bool.BooleanVisitor#visit(kodkod.engine.bool.ITEGate, java.lang.Object)
   */
  @Override
  public BooleanFormula visit(ITEGate ite, Boolean neg) {
    final BooleanFormula newIf = ite.input(0).accept(this, false);
    final BooleanFormula newThen = ite.input(1).accept(this, neg);
    final BooleanFormula newElse = ite.input(2).accept(this, neg);
    if(newIf == ite.input(0) && newThen == ite.input(1) && newElse == ite.input(2))
      return ite;
    return copyUpdate(ite, newIf, newThen, newElse);
  }

  /* (non-Javadoc)
   * @see kodkod.engine.bool.BooleanVisitor#visit(kodkod.engine.bool.NotGate, java.lang.Object)
   */
  @Override
  public BooleanFormula visit(NotGate negation, Boolean neg) {
    return (negation.input(0) instanceof BooleanVariable && !neg ? negation : negation.input(0).accept(this, !neg));
  }

  /* (non-Javadoc)
   * @see kodkod.engine.bool.BooleanVisitor#visit(kodkod.engine.bool.BooleanVariable, java.lang.Object)
   */
  @Override
  public BooleanFormula visit(BooleanVariable variable, Boolean neg) {
    return neg ? negate(variable) : variable;
  }
}
