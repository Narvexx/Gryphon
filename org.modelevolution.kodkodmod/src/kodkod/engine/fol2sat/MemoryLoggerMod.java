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

import kodkod.ast.Formula;
import kodkod.engine.bool.BooleanMatrix;
import kodkod.engine.bool.BooleanValue;
import kodkod.instance.Bounds;
import kodkod.util.nodes.AnnotatedNode;

/**
 * @author Sebastian Gabmeyer
 * @deprecated Use {@link CustomMemoryLogger} instead.
 */
@Deprecated
public class MemoryLoggerMod extends TranslationLogger {
	TranslationLogger logger;
	
	public MemoryLoggerMod() {
		
	}
	
	public MemoryLoggerMod(AnnotatedNode<Formula> annotated, Bounds bounds) {
		logger = new MemoryLogger(annotated, bounds);
	}
	/* (non-Javadoc)
	 * @see kodkod.engine.fol2sat.TranslationLoggerMod#log(kodkod.ast.Formula, kodkod.engine.bool.BooleanValue, kodkod.engine.fol2sat.Environment)
	 */
	@Override
	public void log(Formula f, BooleanValue translation,
			Environment<BooleanMatrix> env) {
		if(logger == null) {
			throw new IllegalStateException("Logger not initialized. Set 'annotated' and 'bounds'.");
		}
		logger.log(f, translation, env);

	}

	/* (non-Javadoc)
	 * @see kodkod.engine.fol2sat.TranslationLoggerMod#close()
	 */
	@Override
	public void close() {
		if(logger == null) {
			throw new IllegalStateException("Logger not initialized. Set 'annotated' and 'bounds'.");
		}
		logger.close();
	}

	/* (non-Javadoc)
	 * @see kodkod.engine.fol2sat.TranslationLoggerMod#log()
	 */
	@Override
	public TranslationLog log() {
		if(logger == null) {
			throw new IllegalStateException("Logger not initialized. Set 'annotated' and 'bounds'.");
		}
		return logger.log();
	}

}
