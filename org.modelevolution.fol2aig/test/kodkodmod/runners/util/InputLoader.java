/*
 * henshin2kodkod -- Copyright (c) 2014-present, Sebastian Gabmeyer
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
package kodkodmod.runners.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.henshin.model.Module;
import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.model.Unit;
import org.eclipse.emf.henshin.model.resource.HenshinResourceSet;
import org.modelevolution.emf2rel.FeatureMerger;
import org.modelevolution.emf2rel.FeatureOmitter;
import org.modelevolution.gts2rts.util.HenshinLoader;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class InputLoader {

  private static final class RuleCollector {
    private static final String BAD_PREFIX = "bad_";
    private static final String INV_PREFIX = "inv_";
    private static final String REACH_PREFIX = "reach_";

    private boolean specChanged = false;
    private final Collection<Rule> transitionRules;
    private final Collection<Rule> invariantRules;
    private final Collection<Rule> badPropertyRules;
    private final Collection<Rule> reachPropertyRules;
    private Collection<String> filter;
    private Collection<Rule> allProperties;

    /**
     * 
     */
    RuleCollector() {
      transitionRules = new ArrayList<>();
      invariantRules = new ArrayList<>();
      badPropertyRules = new ArrayList<>();
      reachPropertyRules = new ArrayList<>();
    }

    RuleCollector(final Collection<String> filteredRules) {
      this();
      this.filter = filteredRules;
    }

    boolean add(final Rule rule) {
      final String prefix = prefix(rule.getName());
      final String baseName = rule.getName().substring(prefix.length());
      if (filter.isEmpty() || filter.contains(rule.getName()) || filter.contains(baseName)) {
        switch (prefix) {
        case BAD_PREFIX:
          return addBadProperty(rule);
        case INV_PREFIX:
          return addInvariant(rule);
        case REACH_PREFIX:
          return addReachabilityProperty(rule);
        default:
          return addTransition(rule);
        }
      }
      return false;
    }

    boolean addAll(final Collection<Rule> rules) {
      boolean rv = false;
      for (final Rule rule : rules) {
        rv |= add(rule);
      }
      return rv;
    }

    /**
     * @param name
     * @return
     */
    private String prefix(final String ruleName) {
      if (ruleName.startsWith(BAD_PREFIX))
        return BAD_PREFIX;
      if (ruleName.startsWith(INV_PREFIX))
        return INV_PREFIX;
      if (ruleName.startsWith(REACH_PREFIX))
        return REACH_PREFIX;
      return new String();
    }

    boolean addTransition(Rule transitionRule) {
      return transitionRules.add(transitionRule);
    }

    boolean addInvariant(Rule invariantRule) {
      specChanged = invariantRules.add(invariantRule);
      return specChanged;
    }

    boolean addBadProperty(Rule badPropertyRule) {
      specChanged = badPropertyRules.add(badPropertyRule);
      return specChanged;
    }

    /**
     * @param reachPropertyRule
     * @return
     */
    public boolean addReachabilityProperty(final Rule reachPropertyRule) {
      specChanged = reachPropertyRules.add(reachPropertyRule);
      return specChanged;
    }

    final Collection<Rule> transitions() {
      return transitionRules;
    }

    final Collection<Rule> invariants() {
      return invariantRules;
    }

    final Collection<Rule> badProperties() {
      return badPropertyRules;
    }

    final Collection<Rule> reachabilityProperties() {
      return reachPropertyRules;
    }

    final Collection<Rule> allProperties() {
      if (specChanged) {
        final int size = badProperties().size() + invariants().size()
            + reachabilityProperties().size();
        allProperties = new ArrayList<>(size);
        allProperties.addAll(badProperties());
        allProperties.addAll(invariants());
        allProperties.addAll(reachabilityProperties());
        specChanged = false;
      }
      return allProperties;
    }
  }

  private final EPackage metamodel;
  private final Resource[] instances;
  private final RuleCollector collector;
  private final String runId;
  private final EmfModelResolver resolver;
  private String[][] outputFilepaths;

  /**
   * @param runId
   * @param workdir
   * @param transformationFile
   * @param initialStates
   * @param enabledRules
   * @param enabledProperties
   * @throws IOException
   *           if an initial state cannot be loaded
   * @throws NullPointerException
   *           if workdir == null || transformationFile == null || initialStates
   *           == null
   * @throws IllegalArgumentException
   *           if workdir.isEmpty() || transformationFile.isEmpty() ||
   *           initialStates.length < 1
   * @throws InputLoaderException
   *           if this.module == null || this.module.getImports().isEmpty() ||
   *           module.getImports().size() != 1
   */
  public InputLoader(final String runId, final String workdir, final String transformationFile,
      final String[] initialStates, String[] enabledRules, String[] enabledProperties)
      throws IOException {
    if (workdir == null || transformationFile == null || initialStates == null)
      throw new NullPointerException();
    if (workdir.isEmpty() || transformationFile.isEmpty() || initialStates.length < 1)
      throw new IllegalArgumentException();
    if (enabledRules == null)
      enabledRules = new String[0];
    if (enabledProperties == null)
      enabledProperties = new String[0];

    final HenshinLoader henshinLoader = new HenshinLoader(workdir, transformationFile);
    if (runId == null || runId.isEmpty())
      this.runId = henshinLoader.moduleName();
    else
      this.runId = runId;

    if (henshinLoader.getMetamodels().size() > 1)
      throw new InputLoaderException("The module loaded from " + transformationFile
          + " uses more than one import. Modules with more than one import are not supported.");

    metamodel = henshinLoader.getMetamodel();
    resolver = new EmfModelResolver(metamodel);

    final Collection<String> enabled = Arrays.asList(enabledRules);
    enabled.addAll(Arrays.asList(enabledProperties));
    collector = new RuleCollector(enabled);
    collector.addAll(henshinLoader.getTransformations());

    instances = loadInstances(workdir, initialStates);
    outputFilepaths = buildOutputFilepaths(workdir, this.runId, instances);
  }

  public String getRunId() {
    return runId;
  }

  /**
   * @return the outputFilepaths
   */
  public String[][] getOutputFilepaths() {
    return outputFilepaths;
  }

  public Collection<Rule> transitionRules() {
    return Collections.unmodifiableCollection(collector.transitions());
  }

  public Collection<Rule> invariantRules() {
    return Collections.unmodifiableCollection(collector.invariants());
  }

  public Collection<Rule> badPropertyRules() {
    return Collections.unmodifiableCollection(collector.badProperties());
  }

  public Collection<Rule> reachabilityRules() {
    return Collections.unmodifiableCollection(collector.reachabilityProperties());
  }

  public Collection<Rule> specificationRules() {
    return Collections.unmodifiableCollection(collector.allProperties());
  }

  /**
   * @return
   */
  public EPackage metamodel() {
    return this.metamodel;
  }

  /**
   * @return
   */
  public Resource[] instances() {
    return this.instances;
  }

  /**
   * @param mergers
   * @return
   */
  public FeatureMerger getFeatureMerger(String[] mergers) {
    if (mergers == null || mergers.length < 1)
      return FeatureMerger.create();
    return FeatureMerger.create(extractFeatures(mergers));
  }

  /**
   * @param omit
   * @return
   */
  public FeatureOmitter getFeatureOmitter(String[] omit) {
    if (omit == null || omit.length < 1)
      return FeatureOmitter.create();
    return FeatureOmitter.create(extractFeatures(omit));
  }

  /**
   * @param upperBounds
   * @return
   */
  public Map<EClass, Integer> getUpperBounds(String[] upperBounds) {
    if (upperBounds == null || upperBounds.length < 1)
      return Collections.emptyMap();
    return extractBounds(upperBounds);
  }

  private Resource[] loadInstances(String workdir, String[] instanceFiles) throws IOException {
    final ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getPackageRegistry().put(metamodel.getNsURI(), metamodel);
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
               .put("xmi", new XMIResourceFactoryImpl());
    final Resource[] instances = new Resource[instanceFiles.length];
    for (int i = 0; i < instanceFiles.length; ++i) {
      final String instancePath = new StringBuffer(workdir).append(File.separator)
                                                           .append(instanceFiles[i]).toString();
      final Resource instance = resourceSet.createResource(URI.createFileURI(instancePath));
      instance.load(null);
      instances[i] = instance;
    }
    return instances;
  }

  /**
   * @param outputDir
   * @param name
   * @param initialStates
   * @return
   */
  private String[][] buildOutputFilepaths(final String outputDir, String name,
      Resource[] initialStates) {
    final StringBuffer dirPath = new StringBuffer(outputDir).append(File.separator);
    String[][] outputfilepaths = new String[initialStates.length][2];
    for (int i = 0; i < initialStates.length; ++i) {
      final String filepath = dirPath.append(name).append("_")
                                     .append(filename(initialStates[i].getURI().lastSegment()))
                                     .toString();
      outputfilepaths[i][0] = filepath + ".aag";
      outputfilepaths[i][1] = filepath + ".stats";
    }
    return outputfilepaths;
  }

  /**
   * @param lastSegment
   * @return
   */
  private String filename(final String file) {
    final int dotPos = file.lastIndexOf('.');
    if (dotPos < 0)
      return file;
    else
      return file.substring(0, dotPos);
  }

  private Map<EClass, Integer> extractBounds(final String[] classUboundPairs) {
    final Map<EClass, Integer> bounds = new IdentityHashMap<>(classUboundPairs.length);
    for (final String pair : classUboundPairs) {
      final String[] classUBound = pair.replaceAll("\\s*", "").split(":");
      final EClass eClass = resolver.resolveEClass(classUBound[0]);
      if (eClass == null)
        throw new NullPointerException("EClass " + classUBound[0] + " not found.");
      bounds.put(eClass, Integer.valueOf(classUBound[1]));
    }
    return bounds;
  }

  private List<EStructuralFeature> extractFeatures(final String[] classFeatureList) {
    final List<EStructuralFeature> features = new ArrayList<>();

    for (final String string : classFeatureList) {
      final String[] classFeatureNames = string.replaceAll("\\s*", "").split(":|,");
      final Collection<EStructuralFeature> extracted = resolver.resolveFeatures(classFeatureNames);
      features.addAll(extracted);
    }
    return features;
  }
}
