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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package kodkod.engine.fol2sat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import kodkod.ast.Formula;
import kodkod.ast.Node;
import kodkod.ast.Variable;
import kodkod.engine.bool.BooleanMatrix;
import kodkod.engine.bool.BooleanValue;
import kodkod.instance.Bounds;
import kodkod.instance.TupleSet;
import kodkod.util.nodes.AnnotatedNode;

import org.modelevolution.commons.modifications.BasedOn;
import org.modelevolution.commons.modifications.ModType;
import org.modelevolution.commons.modifications.Modification;
import org.modelevolution.commons.modifications.ModifiedBy;

/**
 * This class provides {@link MemoryLogger} for a user-specified set of
 * formulas.
 * 
 * @author Sebastian Gabmeyer
 * 
 */
@Modification(ModType.NEW)
@ModifiedBy("Sebastian Gabmeyer")
@BasedOn(MemoryLogger.class)
public class CustomMemoryLogger extends TranslationLoggerMod {
	private final Map<Formula, BooleanValue> logMap;
	private final Bounds bounds;
	private AnnotatedNode<Formula> annotated;
	private Set<Formula> formulas;

	/**
	 * @param annotated
	 * @param bounds
	 * @param formulas
	 */
	public CustomMemoryLogger(AnnotatedNode<Formula> annotated, Bounds bounds, Set<Formula> formulas) {
		this.annotated = annotated;
		this.bounds = bounds;
		this.formulas = formulas;
		this.logMap = new HashMap<>((formulas.size() / 3) * 4);
	}

	// /**
	// * @param annotated
	// * @param bounds
	// * @param formulas
	// */
	// public CustomMemoryLogger(AnnotatedNode<Formula> annotated, Bounds bounds,
	// Formula... formulas) {
	// this.annotated = annotated;
	// this.bounds = bounds;
	// this.logMap = new HashMap<>();
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see kodkod.engine.fol2sat.TranslationLoggerMod#log(kodkod.ast.Formula,
	 * kodkod.engine.bool.BooleanValue, kodkod.engine.fol2sat.Environment)
	 */
	@Override
	public void log(Formula f, BooleanValue translation, Environment<BooleanMatrix> env) {
		// Added a check for feature variables
		if (formulas.contains(f) || f.toString().contains("f_")) {
			final BooleanValue old;
			// if (translation instanceof BooleanFormula) {
			// final NegationNormalFormer nnf = new NegationNormalFormer();
			// old = logMap.put(f, ((BooleanFormula) translation).accept(nnf,
			// false));
			// } else {
			// old = logMap.put(f, translation);
			// }
			old = logMap.put(f, translation);
			if (old != null && old != translation)
				throw new IllegalArgumentException(
						"translation of root corresponding to the formula has already been logged: " + f);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kodkod.engine.fol2sat.TranslationLoggerMod#close()
	 */
	@Override
	public void close() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kodkod.engine.fol2sat.TranslationLoggerMod#log()
	 */
	@Override
	public ExtTranslationLog log() {
		return new CustomMemoryLog(annotated, logMap, bounds);
	}

	/**
	 * A memory-based translation log, written by a MemoryLogger.
	 * 
	 * @author Sebastian Gabmeyer
	 */
	@Modification(ModType.NEW)
	@BasedOn(MemoryLogger.class)
	private static class CustomMemoryLog extends ExtTranslationLog {
		private final Set<Formula> roots;
		private final Bounds bounds;
		private final Node[] original;
		private final BooleanValue[] transl;
		private Map<Formula, BooleanValue> logMap;

		/**
		 * Constructs a new memory log out of the given node and its corresponding log
		 * map.
		 */
		CustomMemoryLog(AnnotatedNode<Formula> annotated, Map<Formula, BooleanValue> logMap, Bounds bounds) {
			this.bounds = bounds;
			this.roots = logMap.keySet();
			// assert roots.size() == logMap.size();
			this.transl = new BooleanValue[roots.size()];
			this.original = new Node[roots.size()];
			final Iterator<Formula> itr = roots.iterator();
			for (int i = 0; i < transl.length; i++) {
				final Formula root = itr.next();
				transl[i] = logMap.get(root);
				original[i] = annotated.sourceOf(root);
			}
			this.logMap = logMap;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see kodkod.engine.fol2sat.TranslationLog#bounds()
		 */
		public Bounds bounds() {
			return bounds;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see kodkod.engine.fol2sat.TranslationLog#replay(kodkod.engine.fol2sat.RecordFilter)
		 */
		@Override
		public Iterator<TranslationRecord> replay(final RecordFilter filter) {
			return new Iterator<TranslationRecord>() {
				final Iterator<Formula> itr = roots.iterator();
				boolean ready = false;
				int index = -1;
				Formula root = null;
				final ExtTranslationRecord current = new ExtTranslationRecord() {
					@Override
					public Map<Variable, TupleSet> env() {
						return Collections.emptyMap();
					}

					@Override
					public int literal() {
						return transl[index].label();
					}

					@Override
					public Node node() {
						return original[index];
					}

					@Override
					public Formula translated() {
						return root;
					}

					@Override
					public BooleanValue circuit() {
						return transl[index];
					}
				};

				@SuppressWarnings("unchecked")
				public boolean hasNext() {
					while (!ready && itr.hasNext()) {
						root = itr.next();
						index++;
						if (filter.accept(original[index], root, transl[index].label(), Collections.EMPTY_MAP)) {
							ready = true;
							break;
						}
					}
					return ready;
				}

				public ExtTranslationRecord next() {
					if (!hasNext())
						throw new NoSuchElementException();
					ready = false;
					return current;
				}

				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @see kodkod.engine.fol2sat.TranslationLog#roots()
		 */
		@Override
		public Set<Formula> roots() {
			return roots;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see kodkod.engine.fol2sat.ExtTranslationLog#circuit()
		 */
		@Override
		public BooleanValue[] circuits() {
			return transl;
		}

		@Override
		public Map<Formula, BooleanValue> translation() {
			return logMap;
		}

	}

}
