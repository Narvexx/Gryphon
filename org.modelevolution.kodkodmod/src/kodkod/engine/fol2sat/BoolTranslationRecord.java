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

import kodkod.engine.bool.BooleanValue;

/**
 * @author Sebastian Gabmeyer
 *
 */
@ModifiedBy("Sebastian Gabmeyer")
@Modification(ModType.NEW)
@BasedOn(TranslationRecord.class)
public abstract class BoolTranslationRecord extends TranslationRecord {
	public abstract BooleanValue bool();
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		final StringBuilder ret = new StringBuilder();
		ret.append("< node: ");
		ret.append(node());
		ret.append(" = ");
		ret.append(translated());
		ret.append(", literal: ");
		ret.append(literal());
		ret.append("= ");
		ret.append(bool());
		ret.append(", env: ");
		ret.append(env());
		ret.append(">");
		return ret.toString();
	}
}
