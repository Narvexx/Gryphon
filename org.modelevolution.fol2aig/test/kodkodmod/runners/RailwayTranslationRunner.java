/* 
 * org.modelevolution.fol2aig -- Copyright (c) 2015-present, Sebastian Gabmeyer
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
package kodkodmod.runners;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import kodkod.ast.Formula;
import kodkod.ast.Relation;
import kodkod.engine.config.ExtOptions;
import kodkod.engine.config.Options;
import kodkod.engine.fol2sat.BoolTranslation;
import kodkod.engine.fol2sat.TranslationRecord;
import kodkod.util.ints.IntIterator;
import kodkodmod.runners.util.InputLoader;
import kodkodmod.runners.util.InputLoaderException;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.henshin.model.Rule;
import org.modelevolution.aig.AigFile;
import org.modelevolution.emf2rel.FeatureOmitter;
import org.modelevolution.emf2rel.Signature;
import org.modelevolution.fol2aig.AigTranslator;
import org.modelevolution.fol2aig.Fol2AigProof;
import org.modelevolution.gts2rts.PropertyTranslator;
import org.modelevolution.gts2rts.RuleTranslator;
import org.modelevolution.gts2rts.VariabilityRuleTranslator;
import org.modelevolution.gts2rts.util.RuleUtil;
import org.modelevolution.rts.Property;
import org.modelevolution.rts.StateChanger;
import org.modelevolution.rts.TransitionRelation;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class RailwayTranslationRunner {
  private static final String HENSHIN_FILE = "interlocking.henshin";
  private static final String MODEL_PATH = "/home/sgbmyr/fame/relic3/org.modelevolution.models/model/railway";
  private static final String ECORE_MODEL_NAME = "railwaysystem.ecore";;
  private static final String INSTANCE_FILE = MODEL_PATH + "/RailwaySystem.xmi";

  private static final int BITWIDTH = 1;

  // private static final int ARRAYWIDTH = 0;

  public static void main(String[] args) throws IOException {
    /*
     * final PacmanModel model = new
     * PacmanModel(PacmanGameFactory.satSuperSimple(), BITWIDTH); final Formula
     * transitionRelation = model.movePacmanOnce(); final Formula badProperty =
     * model.badProperty();
     */
    InputLoader helper;
    try {
      helper = new InputLoader(null, MODEL_PATH, HENSHIN_FILE, new String[] { INSTANCE_FILE },
                               new String[0], new String[0]);
    } catch (InputLoaderException e) {
      e.printStackTrace();
      return;
    }
    final EPackage metamodel = helper.metamodel();
    assert metamodel != null;
    final Resource instanceModel = helper.instances()[0];
    assert instanceModel != null;
    final Collection<EStructuralFeature> features = extract(metamodel, "TrackElement", "id",
                                                            "Signal", "id", "Signal", "home",
                                                            "Route", "id");
    FeatureOmitter omitter = FeatureOmitter.create(features);
    final Collection<Rule> rules = helper.transitionRules();
    assert !rules.isEmpty();
    Signature sig = Signature.init(metamodel, VariabilityRuleTranslator.getFeatures(rules), instanceModel, null, null, omitter, BITWIDTH);
    
    for (final Rule rule : rules) {
      assert RuleUtil.isRootRule(rule);
    }
    final TransitionRelation transitions = RuleTranslator.translateAll(sig, rules);
    assert transitions != null;
    assert transitions.size() == rules.size();
    for (StateChanger t : transitions) {
      System.out.println(t);
      System.out.println();
    }

    final Options options = new Options();
    options.setCoreGranularity(3);
    options.setSharing(3);

    assert helper.badPropertyRules().size() > 0;
    final Collection<Rule> propertyRules = helper.specificationRules();
    final List<Property> properties = new ArrayList<>(propertyRules.size());
    final PropertyTranslator propertyTranslator = new PropertyTranslator(sig);
    for (final Rule propertyRule : propertyRules) {
      final Property property = propertyTranslator.translate(propertyRule);

      System.out.println(property);
      System.out.println();
      properties.add(property);
    }
    // AigTranslation aig =
    // translator.translate(vc, initialState, bounds, options);
    final AigTranslator translator = new AigTranslator(sig, new ExtOptions(options));
    assert translator != null;
    final AigFile aig = translator.translate(transitions, properties);
    final Fol2AigProof proof = translator.proof(transitions, properties);
    System.out.println(proof);

    System.out.println(aig.toString());
    BoolTranslation circuit = translator.circuit();

    for (StateChanger t : transitions) {
      System.out.println(t.name());
      for (Formula p : t.premises()) {
        System.out.print(p);
        System.out.print(" & ");
      }
      System.out.println();
      for (Formula p : t.premises()) {
        System.out.print(circuit.mapping(p));
        System.out.print(" & ");
      }
      System.out.println();
      for (Formula c : t.consequents()) {
        System.out.print(c);
        System.out.print(" & ");
      }
      System.out.println();
      for (Formula c : t.consequents()) {
        System.out.print(circuit.mapping(c));
        System.out.print(" & ");
      }
    }
    System.out.println();
    // System.out.println(t.elseConsequent());

    System.out.println("Relations and their primary variables:");
    for (Relation r : circuit.bounds().relations()) {
      System.out.print(r.name() + ": ");
      IntIterator it = circuit.labels(r).iterator();
      if (it.hasNext()) {
        int literal = (int) it.next();
        System.out.print(literal);
        while (it.hasNext()) {
          literal = (int) it.next();
          System.out.print(", " + literal);
        }
      }
      System.out.println();
    }

    System.out.println(sig.bounds());

    System.out.println();
    System.out.println("Replaying the translation in detail...");
    final Iterator<TranslationRecord> it = circuit.log().replay();
    while (it.hasNext()) {
      TranslationRecord tr = it.next();
      System.out.println(tr);
    }
  }

  /**
   * @param metamodel
   * @param string
   * @param string2
   * @return
   */
  private static Collection<EStructuralFeature> extract(EPackage metamodel, String... names) {
    assert names.length % 2 == 0;
    final Collection<EStructuralFeature> features = new ArrayList<>(names.length >> 1);
    for (int i = 0; i < names.length - 1; i += 2) {
      final EClassifier classifier = metamodel.getEClassifier(names[i]);
      if (classifier != null && classifier instanceof EClass) {
        final EClass clazz = (EClass) classifier;
        final EStructuralFeature feature = clazz.getEStructuralFeature(names[i + 1]);
        if (feature != null)
          features.add(feature);
      }
    }
    return features;
  }
}
