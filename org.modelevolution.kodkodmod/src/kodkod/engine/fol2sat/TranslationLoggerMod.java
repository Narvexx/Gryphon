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

import kodkod.ast.Formula;
import kodkod.engine.bool.BooleanMatrix;
import kodkod.engine.bool.BooleanValue;

/**
 * This class lifts the package-private visibility of the
 * {@link kodkod.engine.fol2sat.TranslationLogger TranslationLogger} to public
 * visibility. This class was introduced because changing the visibility of the
 * abstract methods to <i>public</i> would require to change the visibility of
 * these methods in all existing subclasses of the
 * {@link kodkod.engine.fol2sat.TranslationLogger TranslationLogger} class.
 * 
 * @author Sebastian Gabmeyer
 * 
 */
@ModifiedBy("Sebastian Gabmeyer")
@Modification(ModType.NEW)
@BasedOn(TranslationLogger.class)
public abstract class TranslationLoggerMod extends TranslationLogger {

	@Override
	public abstract void log(Formula f, BooleanValue translation,
			Environment<BooleanMatrix> env);

	@Override
	public abstract void close();

	@Override
	public abstract ExtTranslationLog log();
}
