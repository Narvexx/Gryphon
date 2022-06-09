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
import kodkod.ast.Node;
import kodkod.ast.Relation;
import kodkod.ast.Variable;
import kodkod.ast.operator.IntCastOperator;
import kodkod.engine.bool.BooleanConstant;
import kodkod.engine.bool.BooleanValue;
import kodkod.instance.Tuple;
import kodkod.instance.TupleSet;
import kodkod.instance.Universe;

public class VariabilityRuleTranslator {

	private static VariabilityRuleTranslator instance = new VariabilityRuleTranslator();

	private VariabilityRuleTranslator() {
	}

	public static VariabilityRuleTranslator getInstance() {
		return instance;
	}

	private static final int TRUE = 0;
	private static final int FALSE = -1;
	private static final String FEATURE = "Feature";

	public static Relation featureRelations = null;
	
	public static Set<Relation> features = new HashSet<Relation>();

	public static StateRelation stateRel;
	
	private static Set<Formula> pcs = new HashSet<Formula>();
	private static StateRelation hostRel;
	
	public static ArrayList<Integer> labelsCache = new ArrayList<Integer>();

	public static boolean isVariabilityBased() {
		return features.size() > 0;
	}
	
	public static void cache(int label) {
		labelsCache.add(Math.abs(label));
	}
	
	public static Formula getRelationByAtom(String atom, Signature sig) {
		
		
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
		
		
		// As Variable
		final Relation binding = sig.pre(EcorePackage.Literals.EBOOLEAN);
		
		boolean value = true;
		if (atom.charAt(0) == '!') {
			atom = atom.substring(1, atom.length());
			value = false;
		}
		
		final VarNodeInfo info = new VarNodeInfo(atom, null, binding);
		final TupleSet upperBound = sig.bounds().upperBound(binding);
		sig.bounds().bound((Relation)info.variable(), upperBound);
	    // End Variable
	    
	    // As Relation
//		final Relation v = Relation.unary(atom);
//		final Relation binding = signature.pre(EcorePackage.Literals.EBOOLEAN);
//		final TupleSet upperBound = signature.bounds().upperBound(binding);
//		signature.bounds().bound(v, upperBound);
	    // End Relation
	    
		//final AttrExprParser parser = new AttrExprParser(null,);
		
		Expression preExpr = null;
		if (value) {
			preExpr = IntConstant.constant(TRUE).cast(IntCastOperator.INTCAST);
		} else {
			preExpr = IntConstant.constant(FALSE).cast(IntCastOperator.INTCAST);
		}
		
		return info.variable().eq(preExpr);
		//return info.variable().override(stateRel.preState()).eq(preExpr);
			

	}
	
//	  public Expression translateToExpr(final Attribute attr) {
//		    final EDataType attributeType = attr.getType().getEAttributeType();
//		    final AttrExprParser parser = new AttrExprParser(attr.getGraph().getRule(), attributeType,
//		                                                     params, enums);
//		    final Expression attrExpr = parser.parse(attr.getValue());
//		    return attrExpr;
//		  }
	
	
	

	public static void annotateFeatureBounds(Signature sig, Collection<Rule> transitionRules) {
//		List<String> features = getFeatures(transitionRules);
//		
//		final ArrayList<Tuple> tups = new ArrayList<Tuple>();
//		for (String f : features) {
//			tups.add(sig.univ().factory().tuple(f));
//		}
//		final TupleSet featureUppers = sig.univ().factory().setOf(tups);
//
//		final StateRelation hostRelation = StateRelation.create(FEATURE, 1);
//		
//		sig.bounds().bound(hostRelation.preState(), featureUppers);
//		sig.bounds().bound(hostRelation.postState(), featureUppers);
//		
//		hostRel = hostRelation;
//		
//		stateRel = addFeatureVariableDeclBounds(sig, hostRelation.preState());
//		
//		featureRelations = stateRel.relation();
		
	}	

	private static StateRelation addFeatureVariableDeclBounds(Signature sig, Relation hostRelation) {
		final Relation refRelation = sig.pre(EcorePackage.Literals.EBOOLEAN);
		
        final TupleSet hostAtoms = sig.bounds().upperBound(hostRelation);
        final TupleSet refAtoms = sig.bounds().upperBound(refRelation);
        final TupleSet upper = refAtoms.product(hostAtoms);
        
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
		for (final String f : features) {
			VariabilityRuleTranslator.features.add(Relation.unary(f));
		}
		return features;
	}

	public static Set<Formula> getPresenceConditions() {
		return pcs;
	}

}
