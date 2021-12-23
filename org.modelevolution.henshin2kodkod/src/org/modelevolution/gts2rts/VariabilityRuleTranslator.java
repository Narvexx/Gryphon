package org.modelevolution.gts2rts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EDataTypeImpl;
import org.eclipse.emf.henshin.model.Annotation;
import org.eclipse.emf.henshin.model.Attribute;
import org.eclipse.emf.henshin.model.Rule;
import org.modelevolution.emf2rel.Signature;
import org.modelevolution.emf2rel.StateRelation;
import org.modelevolution.gts2rts.attrexpr.AttrExprParser;

import kodkod.ast.BinaryExpression;
import kodkod.ast.ConstantExpression;
import kodkod.ast.ConstantFormula;
import kodkod.ast.Decl;
import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.IntConstant;
import kodkod.ast.IntExpression;
import kodkod.ast.IntToExprCast;
import kodkod.ast.LeafExpression;
import kodkod.ast.Relation;
import kodkod.ast.Variable;
import kodkod.ast.operator.IntCastOperator;
import kodkod.engine.bool.BooleanValue;
import kodkod.instance.TupleSet;
import kodkod.instance.Universe;

public class VariabilityRuleTranslator {

	private static VariabilityRuleTranslator instance = new VariabilityRuleTranslator();

	private VariabilityRuleTranslator() {
	}

	public static VariabilityRuleTranslator getInstance() {
		return instance;
	}

	private static final String TRUE = "0";
	private static final String FALSE = "-1";
	private static final String FEATURE = "Feature";

	public static Relation featureRelations = null;

	public static StateRelation stateRel;
	
	public static Signature signature = null;
	
	private static Set<Formula> pcs = new HashSet<Formula>();

	public static Formula getRelationByAtom(String atom) {
		
		
//		  public Formula translateToFormula(final Attribute attr, final Expression attrExpr) {
//			    final LeafExpression src = nodeCache.get(attr.getNode()).variable();
//			    final StateRelation attrState = attrState(attr);
//			    /*
//			     * Take the parsed string and generate a formula that the value of the
//			     * attribute is required to satisfied, ie, to match.
//			     */
//			    final Formula attrCondition = src.join(attrState.preState()).eq(attrExpr);
//			    return attrCondition;
//			  }
		
		
	// OLD
	// Create relational variable v from the atom
//		Relation v = Relation.unary(atom);
//		
//		TupleSet boolTuples = signature.univ().factory().range(signature.univ().factory().tuple(TRUE),
//				signature.univ().factory().tuple(FALSE));
//		
//		// Bound v by boolean values
//		signature.bounds().boundExactly(v, boolTuples);
//		
//		Formula pc = v.one();
//		pcs.add(pc);
//		
//		// Return the formula
//		return pc;
	
		//NEW
		
		final Relation refRelation = signature.state(EcorePackage.Literals.EBOOLEAN).relation();
		
		Relation v = Relation.unary(atom);
		// Bound v by boolean values
		signature.bounds().bound(v, signature.bounds().lowerBound(refRelation));

		//final AttrExprParser parser = new AttrExprParser(null,);
		final Expression preExpr = IntConstant.constant(0).cast(IntCastOperator.INTCAST);
		
		return v.join(stateRel.preState()).eq(preExpr);
			

	}
	
//	  public Expression translateToExpr(final Attribute attr) {
//		    final EDataType attributeType = attr.getType().getEAttributeType();
//		    final AttrExprParser parser = new AttrExprParser(attr.getGraph().getRule(), attributeType,
//		                                                     params, enums);
//		    final Expression attrExpr = parser.parse(attr.getValue());
//		    return attrExpr;
//		  }
	
	
	

	public static void annotateFeatureBounds(Signature sig, Collection<Rule> transitionRules) {
		//final List<String> varFeatures = getFeatures(transitionRules);

		signature = sig;
		
		//Register Upper Bounds on Features
		final TupleSet featureUppers = sig.univ().factory().range(sig.univ().factory().tuple("f_left"),
				sig.univ().factory().tuple("f_right"));
		
		final StateRelation hostRelation = StateRelation.create(FEATURE, 1);
		
		sig.bounds().bound(hostRelation.preState(), featureUppers);
		sig.bounds().bound(hostRelation.postState(), featureUppers);

		stateRel = addFeatureVariableDeclBounds(sig, hostRelation.preState());
		
		featureRelations = stateRel.relation();
		
	}	

	private static StateRelation addFeatureVariableDeclBounds(Signature sig, Relation hostRelation) {
		final Relation refRelation = sig.state(EcorePackage.Literals.EBOOLEAN).relation();
		
        final TupleSet hostAtoms = sig.bounds().upperBound(hostRelation);
        final TupleSet refAtoms = sig.bounds().upperBound(refRelation);
        final TupleSet upper = hostAtoms.product(refAtoms);
        
        final StateRelation state = StateRelation.create("Feature_state", 2);
        sig.bounds().bound(state.preState(), upper);
        sig.bounds().bound(state.postState(), upper);
        
        return state;
	}

	public static List<String> getFeatures(Collection<Rule> transitions) {
		List<String> features = new ArrayList<String>();
		for (final Rule r : transitions) {
			final EList<Annotation> annotations = r.getAnnotations();
			if (annotations != null && annotations.size() > 0) {
				for (final Annotation a : annotations) {
					if (a.getKey().contentEquals("features") && !a.getValue().isEmpty()) {
						Collections.addAll(features, a.getValue().replaceAll(" ", "").split(","));
					}
				}
			}
		}
		return features;
	}

	public static Set<Formula> getPresenceConditions() {
		return pcs;
	}

}
