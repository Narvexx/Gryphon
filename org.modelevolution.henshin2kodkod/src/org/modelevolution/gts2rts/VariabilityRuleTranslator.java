package org.modelevolution.gts2rts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.henshin.model.Annotation;
import org.eclipse.emf.henshin.model.Attribute;
import org.eclipse.emf.henshin.model.Rule;
import org.modelevolution.emf2rel.Signature;
import org.modelevolution.emf2rel.StateRelation;

import kodkod.ast.BinaryExpression;
import kodkod.ast.ConstantExpression;
import kodkod.ast.ConstantFormula;
import kodkod.ast.Decl;
import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.IntConstant;
import kodkod.ast.LeafExpression;
import kodkod.ast.Relation;
import kodkod.ast.Variable;
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

	private static final String TRUE = "-1";
	private static final String FALSE = "0";
	private static final String FEATURES = "Features";

	public static Relation featureRelations = null;

	public static StateRelation stateRel;
	
	public static Signature signature = null;

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
	
			

			Relation v = Relation.unary(atom);
			
			TupleSet boolTuples = signature.univ().factory().range(signature.univ().factory().tuple(TRUE),
					signature.univ().factory().tuple(FALSE));
			signature.bounds().boundExactly(v, boolTuples);
			
			Relation t = Relation.unary(TRUE);
			signature.bounds().boundExactly(t, signature.bounds().exactBound(0));
			
			return v.join(stateRel.preState()).eq(t);
			
//			
//			if (rel.name().contentEquals(atom)) {
//				return rel.one();
//			}
	}

	public static void annotateFeatureBounds(Signature sig, Collection<Rule> transitionRules) {
		final List<String> varFeatures = getFeatures(transitionRules);

		// Create Relational Variables from Features with Bounds Information
		//List<String> relFeatures = new ArrayList<String>();
//		for (final String feature : varFeatures) {
//			featureRelations.add(Relation.unary(feature));
//		}
		signature = sig;

		final TupleSet featureTuples = sig.univ().factory().range(sig.univ().factory().tuple("f_left"),
				sig.univ().factory().tuple("f_right"));
		final Relation hostRelation = Relation.unary(FEATURES);
		sig.bounds().boundExactly(hostRelation, featureTuples);
		
		stateRel = addFeatureVariableDeclBounds(sig, hostRelation);
		
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

}
