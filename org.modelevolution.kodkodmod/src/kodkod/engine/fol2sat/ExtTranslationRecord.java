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

import org.modelevolution.commons.modifications.BasedOn;
import org.modelevolution.commons.modifications.ModType;
import org.modelevolution.commons.modifications.Modification;
import org.modelevolution.commons.modifications.ModifiedBy;

import kodkod.engine.bool.BooleanAccumulator;
import kodkod.engine.bool.BooleanValue;

/**
 * This class extends a {@link TranslationRecord} by an additional field that
 * holds the Boolean circuit as a {@link BooleanValue} that results from the
 * FOL-to-Boolean translation.
 * 
 * @see TranslationRecord
 * @see BooleanValue
 * @see FOL2BoolTranslator
 * @author Sebastian Gabmeyer
 * 
 */
@Modification(ModType.NEW)
@ModifiedBy("Sebastian Gabmeyer")
@BasedOn(TranslationRecord.class)
public abstract class ExtTranslationRecord extends TranslationRecord {
	public abstract BooleanValue circuit();

	@Override
	public String toString() {
		final StringBuilder ret = new StringBuilder();
		ret.append("< node: ");
		ret.append(node());
		ret.append(", literal: ");
		if(circuit() instanceof BooleanAccumulator) {
			ret.append(circuit().toString());
		} else {
			ret.append(literal());
			ret.append("=");
			ret.append(circuit().toString());
		}
		ret.append(", env: ");
		ret.append(env());
		ret.append(">");
		return ret.toString();
	}
}
