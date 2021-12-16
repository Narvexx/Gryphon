/*
 * org.modelevolution.furnace -- Copyright (c) 2015-present, Sebastian Gabmeyer
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
package org.modelevolution.gryphon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.modelevolution.gryphon.input.BenchmarkParams;
import org.modelevolution.gryphon.input.VerificationParams;
import org.modelevolution.gryphon.input.config.BenchmarkConfig;
import org.modelevolution.gryphon.input.config.CmdLineArgsConfig;
import org.modelevolution.gryphon.input.config.ConfigLoader;
import org.modelevolution.gryphon.input.config.VerificationConfig;
import org.modelevolution.gryphon.input.util.InputLoaderException;
import org.modelevolution.gryphon.input.util.NoOpSplitter;
import org.modelevolution.gryphon.runners.BenchmarkRunner;
import org.modelevolution.gryphon.runners.VerificationRunner;
import org.modelevolution.gryphon.solver.VerificationResult;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class GryphonApp {
  @Parameter(names = { "-p", "--workdir" }, description = "Path to the working directory")
  private String workdir;

  // @Parameter(names = { "-mm", "--metamodel" }, required = true,
  // description = "Name of the metamodel")
  // private String mmFileName;

  @Parameter(names = { "-m", "--model" }, variableArity = true,
      description = "Space sepaerated list of initial models.")
  private List<String> instanceFiles = new ArrayList<>();

  @Parameter(names = { "-t", "--transformations" },
      description = "Name of the Henshin transformation file")
  private String transformationFile;

  @Parameter(names = { "-b", "--bitwidth" },
      description = "The maximum bitwidth used for integer values")
  private Integer bitwidth = 0;

  @Parameter(names = "-merge", variableArity = true,
      description = "A semicolon-seperated list of class-feature pairs that should be "
          + "treated (ie. merged) as a single feature. The class-feature "
          + "pairs need to have the following format:\n"
          + "className1:feature1[,feature2]*[;className2:...]* "
          + "(no spaces!).  The merged feature will be named after feature1 from "
          + "className1. If you need to merge a second, third or more features then separate "
          + "the semicolon-seperated list of class-feature pairs by spaces, i.e.,\n"
          + "[className1:feature1[,feature2]*[;className2:...]* ]+")
  private List<String> mergers = new ArrayList<>();

  @Parameter(names = "-omit", variableArity = true, splitter = NoOpSplitter.class,
      description = "A space-seperated list of class-feature pairs that should be "
          + "omitted from the model during the verification. The class-feature "
          + "pairs need to have the following format: className1:feature1[,feature2]* "
          + "(no spaces!).")
  private List<String> omitted = new ArrayList<>();

  @Parameter(names = "-ubounds", variableArity = true, splitter = NoOpSplitter.class,
      description = "A space-seperated list of class-integer pairs that "
          + "define an upper bound on number of objects per class during the "
          + "verification. The class-feature pairs need to have the following format: "
          + "className:UpperBound where 'UpperBound' is an integer value.")
  private List<String> ubounds = new ArrayList<>();

  @Parameter(names = { "-h", "--help" }, help = true, description = "Display this help text")
  private boolean needHelp;

  @Parameter(names = { "-v", "--debug" }, description = "Increases the verbosity level")
  private boolean verbose;

  @Parameter(names = { "-s", "--statistics" }, description = "Print out runtime statistics")
  private boolean printStats;

  @Parameter(names = { "-er", "--enabledRules" },
      description = "Comma-seperated list (no spaces!) of rule names that should be "
          + "enabled, all other rules are disabled.  Note that this does not include "
          + "properties.")
  private String enabledRules;

  @Parameter(names = { "-ep", "--enabledProps" },
      description = "Comma-seperated list (no spaces!) of property names that should be "
          + "enabled, all other properties are disabled.")
  private String enabledProperties;

  @Parameter(names = { "-c", "--config" },
      description = "Load parameters from a configuration file.")
  private String configFile;

  /**
   * @param args
   * @return
   */
  public static void main(String[] args) {
    final GryphonApp verifier = new GryphonApp();
    final JCommander jc = new JCommander(verifier);

    try {
      jc.parse(args);
    } catch (ParameterException e) {
      printHelp(jc);
      return;
    }

    if (verifier.needHelp) {
      printHelp(jc);
      return;
    }

    try {
      verifier.run();
    } catch (IOException | InputLoaderException e) {
      e.printStackTrace();
    }
  }

  /**
   * @param jc
   * 
   */
  private static void printHelp(final JCommander jc) {
    // final StringBuffer sb = new StringBuffer();
    // sb.append("Usage: gryphon -p <work-directory> -mm <metamodel> -m <initial-model> -t <transformations> [-b|-h|-merge|-omit]");
    // sb.append("\n\n");
    // sb.append("Mandatory Options");
    jc.setProgramName("gryphon");
    jc.usage();
  }

  /**
   * @throws IOException
   * @throws InputLoaderException
   * 
   */
  private void run() throws IOException, InputLoaderException {
    if (configFile == null || configFile.isEmpty()) {
      final VerificationConfig config = new CmdLineArgsConfig(workdir, transformationFile,
                                                              instanceFiles, bitwidth,
                                                              enabledRules, enabledProperties,
                                                              omitted, mergers, ubounds);
      final VerificationParams params = VerificationParams.create(config);
      final VerificationRunner runner = new VerificationRunner(params, verbose);
      runner.run();
      runner.statistics().printStats();
      System.out.println();
      for (final VerificationResult res : runner.results()) {
        System.out.println(res.name() + ": " + res.result());
      }
    } else {
      ConfigLoader loader = ConfigLoader.load(configFile);
      for (VerificationConfig vconfig : loader.verificationConfigs()) {
        final VerificationParams params = VerificationParams.create(vconfig);
        final VerificationRunner runner = new VerificationRunner(params, verbose);
        runner.run();
        runner.statistics().printStats();
        System.out.println();
        for (final VerificationResult res : runner.results()) {
          System.out.println(res);
        }
      }
      for (BenchmarkConfig bconfig : loader.benchmarkConfigs()) {
        final BenchmarkParams params = BenchmarkParams.create(bconfig, verbose);
        final BenchmarkRunner runner = new BenchmarkRunner(params);
        runner.run();
        runner.statistics().printStats();
      }
    }
  }

  /**
   * @param start
   * @param initDone
   * @param sigDone
   * @param rulesDone
   * @param propsDone
   * @param aigDone
   * @param fileDone
   */
  private void printStatistics(final long start, final long initDone, final long sigDone,
      final long rulesDone, final long propsDone, final long aigDone, final long fileDone) {
    final long totalTime = fileDone - start;
    System.out.println();
    System.out.println("STATISTICS (in ms)");
    System.out.println("==================");
    final long initTime = initDone - start;
    System.out.println("Input Loading & Reading:" + initTime + "\t(" + (initTime * 100.0)
        / totalTime + "%)");
    final long sigTime = sigDone - initDone;
    System.out.println("Signature Creation:\t\t" + sigTime + "\t(" + (sigTime * 100.0) / totalTime
        + "%)");
    final long ruleTime = rulesDone - sigDone;
    System.out.println("Rule Translations:\t\t" + ruleTime + "\t(" + (ruleTime * 100.0) / totalTime
        + "%)");
    final long propsTime = propsDone - rulesDone;
    System.out.println("Property Translations:\t" + propsTime + "\t(" + (propsTime * 100.0)
        / totalTime + "%)");
    final long aigTime = aigDone - propsDone;
    System.out.println("AIG File Generation:\t" + aigTime + "\t(" + (aigTime * 100.0) / totalTime
        + "%)");
    final long fileTime = fileDone - aigDone;
    System.out.println("AIG File Writing:\t\t" + fileTime + "\t(" + (fileTime * 100.0) / totalTime
        + "%)");
    System.out.println("TOTAL:\t\t\t\t\t" + totalTime);
  }

  // private static Resource loadInstance(EPackage pkg, String instancepath)
  // throws IOException {
  // final ResourceSet resourceSet = new ResourceSetImpl();
  // resourceSet.getPackageRegistry().put(pkg.getNsURI(), pkg);
  // resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
  // .put("xmi", new XMIResourceFactoryImpl());
  // final Resource res =
  // resourceSet.createResource(URI.createFileURI(instancepath));
  // res.load(null);
  // return res;
  // }
  //
  // /**
  // * @param path
  // * @return
  // */
  // private static EPackage loadMetaModel(final String path) {
  // final ResourceSet resourceSet = new ResourceSetImpl();
  //
  // // Register the default resource factory -- only needed for stand-alone!
  // resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
  // .put("ecore", new EcoreResourceFactoryImpl());
  //
  // // Register the package -- only needed for stand-alone!
  // // final EcorePackage ecorePackage = EcorePackage.eINSTANCE;
  //
  // // Get the URI of the model file.
  // final URI fileURI = URI.createFileURI(new File(path).getAbsolutePath());
  //
  // // Demand load the resource for this file.
  // final Resource resource = resourceSet.getResource(fileURI, true);
  // return (EPackage) resource.getContents().get(0);
  // }
  //
  // private static FeatureMerger getPacmanMerger(final EPackage model) {
  // final EReference pacman_on = (EReference) ((EClass)
  // model.getEClassifier("Pacman")).getEStructuralFeature("on");
  // final EReference ghost_on = (EReference) ((EClass)
  // model.getEClassifier("Ghost")).getEStructuralFeature("on");
  // final FeatureMerger fm = FeatureMerger.create("on", pacman_on, ghost_on);
  // return fm;
  // }

}
