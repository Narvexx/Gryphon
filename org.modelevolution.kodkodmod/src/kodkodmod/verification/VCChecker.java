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
package kodkodmod.verification;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import kodkod.ast.BinaryExpression;
import kodkod.ast.BinaryFormula;
import kodkod.ast.BinaryIntExpression;
import kodkod.ast.ComparisonFormula;
import kodkod.ast.Comprehension;
import kodkod.ast.ConstantFormula;
import kodkod.ast.Decl;
import kodkod.ast.Decls;
import kodkod.ast.ExprToIntCast;
import kodkod.ast.Formula;
import kodkod.ast.IfExpression;
import kodkod.ast.IfIntExpression;
import kodkod.ast.IntComparisonFormula;
import kodkod.ast.IntConstant;
import kodkod.ast.IntToExprCast;
import kodkod.ast.LeafExpression;
import kodkod.ast.MultiplicityFormula;
import kodkod.ast.NaryExpression;
import kodkod.ast.NaryFormula;
import kodkod.ast.NaryIntExpression;
import kodkod.ast.Node;
import kodkod.ast.NotFormula;
import kodkod.ast.ProjectExpression;
import kodkod.ast.QuantifiedFormula;
import kodkod.ast.Relation;
import kodkod.ast.RelationPredicate;
import kodkod.ast.SumExpression;
import kodkod.ast.UnaryExpression;
import kodkod.ast.UnaryIntExpression;
import kodkod.ast.Variable;
import kodkod.util.collections.CacheSet;
import kodkod.util.nodes.Nodes;

/**
 * This class can check whether a given {@link VerificationCondition
 * verification condition} satisfies the following constraints:
 * 
 * <ol>
 * <li>The verification condition's {@link VerificationCondition#formula()
 * formula} consist of two conjuncts, the
 * {@link VerificationCondition#transitionRelation() transition relation} and
 * the {@link VerificationCondition#badProperty() bad property}
 * <li>The {@link VerificationCondition#transitionRelation() transition
 * relation} is a conjunction <code>T1 & T2 & ... & Tn</code> of implications
 * where <code>T1, T2,..., Tn</code> are transitions of the form
 * <code>Pre => Post</code>.
 * <li>For each transition <code>T:= Pre => Post</code>, <code>Pre</code>
 * contains no next state variables.
 * <li>For each transition <code>T:= Pre => Post</code>, <code>Post</code> is a
 * conjunction or a negated disjunction of formulas that <i>define</i> the value
 * of a next state variable/relation.
 * </ol>
 * 
 * @author Sebastian Gabmeyer
 * 
 */
@Deprecated
class VCChecker extends kodkod.ast.visitor.AbstractVoidVisitor {

	private Set<Node> cache = new CacheSet<>();
	private Set<Node> seenStateVars = new CacheSet<>();
	private final StateVector states;

	/**
	 * 
	 */
	private VCChecker(StateVector states) {
		this.states = states; 
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kodkod.ast.visitor.VoidVisitor#visit(kodkod.ast.Decls)
	 */
	@Override
	public void visit(Decls decls) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kodkod.ast.visitor.VoidVisitor#visit(kodkod.ast.Decl)
	 */
	@Override
	public void visit(Decl decl) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kodkod.ast.visitor.VoidVisitor#visit(kodkod.ast.UnaryExpression)
	 */
	@Override
	public void visit(UnaryExpression unaryExpr) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kodkod.ast.visitor.VoidVisitor#visit(kodkod.ast.BinaryExpression)
	 */
	@Override
	public void visit(BinaryExpression binExpr) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kodkod.ast.visitor.VoidVisitor#visit(kodkod.ast.NaryExpression)
	 */
	@Override
	public void visit(NaryExpression expr) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kodkod.ast.visitor.VoidVisitor#visit(kodkod.ast.Comprehension)
	 */
	@Override
	public void visit(Comprehension comprehension) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kodkod.ast.visitor.VoidVisitor#visit(kodkod.ast.IfExpression)
	 */
	@Override
	public void visit(IfExpression ifExpr) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kodkod.ast.visitor.VoidVisitor#visit(kodkod.ast.ProjectExpression)
	 */
	@Override
	public void visit(ProjectExpression project) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kodkod.ast.visitor.VoidVisitor#visit(kodkod.ast.IntToExprCast)
	 */
	@Override
	public void visit(IntToExprCast castExpr) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kodkod.ast.visitor.VoidVisitor#visit(kodkod.ast.IntConstant)
	 */
	@Override
	public void visit(IntConstant intConst) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kodkod.ast.visitor.VoidVisitor#visit(kodkod.ast.ExprToIntCast)
	 */
	@Override
	public void visit(ExprToIntCast intExpr) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kodkod.ast.visitor.VoidVisitor#visit(kodkod.ast.IfIntExpression)
	 */
	@Override
	public void visit(IfIntExpression intExpr) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kodkod.ast.visitor.VoidVisitor#visit(kodkod.ast.NaryIntExpression)
	 */
	@Override
	public void visit(NaryIntExpression intExpr) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kodkod.ast.visitor.VoidVisitor#visit(kodkod.ast.BinaryIntExpression)
	 */
	@Override
	public void visit(BinaryIntExpression intExpr) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kodkod.ast.visitor.VoidVisitor#visit(kodkod.ast.UnaryIntExpression)
	 */
	@Override
	public void visit(UnaryIntExpression intExpr) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kodkod.ast.visitor.VoidVisitor#visit(kodkod.ast.SumExpression)
	 */
	@Override
	public void visit(SumExpression intExpr) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * kodkod.ast.visitor.VoidVisitor#visit(kodkod.ast.IntComparisonFormula)
	 */
	@Override
	public void visit(IntComparisonFormula intComp) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kodkod.ast.visitor.VoidVisitor#visit(kodkod.ast.QuantifiedFormula)
	 */
	@Override
	public void visit(QuantifiedFormula quantFormula) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kodkod.ast.visitor.VoidVisitor#visit(kodkod.ast.NaryFormula)
	 */
	@Override
	public void visit(NaryFormula formula) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kodkod.ast.visitor.VoidVisitor#visit(kodkod.ast.BinaryFormula)
	 */
	@Override
	public void visit(BinaryFormula binFormula) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kodkod.ast.visitor.VoidVisitor#visit(kodkod.ast.NotFormula)
	 */
	@Override
	public void visit(NotFormula not) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kodkod.ast.visitor.VoidVisitor#visit(kodkod.ast.ConstantFormula)
	 */
	@Override
	public void visit(ConstantFormula constant) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kodkod.ast.visitor.VoidVisitor#visit(kodkod.ast.ComparisonFormula)
	 */
	@Override
	public void visit(ComparisonFormula compFormula) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kodkod.ast.visitor.VoidVisitor#visit(kodkod.ast.MultiplicityFormula)
	 */
	@Override
	public void visit(MultiplicityFormula multFormula) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kodkod.ast.visitor.VoidVisitor#visit(kodkod.ast.RelationPredicate)
	 */
	@Override
	public void visit(RelationPredicate predicate) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kodkod.ast.visitor.AbstractVoidVisitor#visited(kodkod.ast.Node)
	 */
	@Override
	protected boolean visited(Node n) {
		if (cache.contains(n))
			return true;
		return false;
	}

	private static class StateVariableCollector extends
			kodkod.ast.visitor.AbstractVoidVisitor {

		private Set<Node> cache = new CacheSet<>();
		private Map<String, LeafExpression> visistedLeafs = new HashMap<>();
		private final Map<LeafExpression, LeafExpression> stateVars = new HashMap<>();

		@Override
		public void visit(Relation relation) {
			if (visited(relation))
				return;
			visit((LeafExpression) relation);
		}

		@Override
		public void visit(Variable variable) {
			if (visited(variable))
				return;
			visit((LeafExpression) variable);
		}

		public Map<StateVariable, StateVariable> stateVars() {
			Map<StateVariable, StateVariable> map = new HashMap<>(
					(stateVars.size() * 4) / 3);
			for (Entry<LeafExpression, LeafExpression> e : stateVars.entrySet()) {
				map.put(new StateVariable(e.getKey()),
						new StateVariable(e.getValue()));
			}
			return map;
		}

		// public Collection<LeafExpression> inputVars() {
		// Collection<LeafExpression> leafs = new
		// HashSet<>(visistedLeafs.values());
		// leafs.removeAll(stateVars.keySet());
		// leafs.removeAll(stateVars.values());
		// return leafs;
		// }

		/**
		 * @param leaf
		 */
		private void visit(LeafExpression leaf) {
			if (isNextStateVar(leaf)) {
				final String currentStateVarName = buildCurrentStateVarName(leaf);
				if (visistedLeafs.containsKey(currentStateVarName)) {
					final LeafExpression currentState = visistedLeafs
							.get(currentStateVarName);
					stateVars.put(currentState, leaf);
				}
			} else if (visistedLeafs.containsKey(buildNextStateVarName(leaf))) {
				final LeafExpression nextState = visistedLeafs
						.get(buildNextStateVarName(leaf));
				stateVars.put(leaf, nextState);
			} else {
				visistedLeafs.put(leaf.name(), leaf);
			}
		}

		/**
		 * @param leaf
		 * @return
		 */
		private String buildNextStateVarName(LeafExpression leaf) {
			return leaf.name() + "'";
		}

		/**
		 * @param leaf
		 * @return
		 */
		private String buildCurrentStateVarName(LeafExpression leaf) {
			return leaf.name().substring(0, (leaf.name().length() - 1));
		}

		/**
		 * @param leaf
		 * @return
		 */
		private boolean isNextStateVar(LeafExpression leaf) {
			return leaf.name().endsWith("'");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see kodkod.ast.visitor.AbstractVoidVisitor#visited(kodkod.ast.Node)
		 */
		@Override
		protected boolean visited(Node n) {
			if (cache.contains(n))
				return true;
			return false;
		}

	}
	
	public static void checkVerificationCondition(final Formula transitionRelation, final Formula badProperty) {
		if(badProperty == null) {
			throw new IllegalArgumentException("badProperty must not be null.");
		}
		StateVariableCollector collector = new StateVariableCollector();
		transitionRelation.accept(collector);
		transitionRelation.accept(new VCChecker(StateVector.create(collector.stateVars())));
	}
	
	public static void checkVerificationCondition(final Formula formula)  {
		if(Nodes.conjuncts(formula).size() != 2) {
			throw new IllegalArgumentException(
					"The formula does not satisfy the required format of transitionRelation && badProperty.");
		}
		BinaryFormula conjunt = (BinaryFormula) formula;
		checkVerificationCondition(conjunt.left(), conjunt.right());
	}
}