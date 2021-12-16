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
package org.modelevolution.aig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class AigFile {

  public static AigFile TRUE = new AigFile(
                                           new ArrayList<AigExpr>(),
                                           new ArrayList<AigExpr>(),
                                           new ArrayList<AigExpr>(
                                                                  Collections.singletonList(new AigOutput(
                                                                                                          AigLit.TRUE))),
                                           new ArrayList<AigExpr>(), new ArrayList<AigExpr>(),
                                           new ArrayList<AigExpr>());

  public static AigFile FALSE = new AigFile(
                                            new ArrayList<AigExpr>(),
                                            new ArrayList<AigExpr>(),
                                            new ArrayList<AigExpr>(
                                                                   Collections.singletonList(new AigOutput(
                                                                                                           AigLit.FALSE))),
                                            new ArrayList<AigExpr>(), new ArrayList<AigExpr>(),
                                            new ArrayList<AigExpr>());

  private final List<AigExpr> inputs;
  private final List<AigExpr> latches;
  private final List<AigExpr> outputs;
  private final List<AigExpr> ands;
  private final List<AigExpr> bads;
  private final List<AigExpr> invs;

  // public AigTranslation() { // final int numInputs, final int maxLabel,
  // final
  // // int numBads
  // inputs = new ArrayList<AigInput>();
  // latches = new ArrayList<>();
  // outputs = new ArrayList<>();
  // ands = new ArrayList<>();
  // bads = new ArrayList<>();
  // invs = new ArrayList<>();
  // }

  // public AigFile() {
  // this(new ArrayList<AigExpr>(), new ArrayList<AigExpr>(),
  // new ArrayList<AigExpr>(), new ArrayList<AigExpr>(),
  // new ArrayList<AigExpr>(), new ArrayList<AigExpr>());
  // }

  // public AigFile(final List<AigInput> inputs, final List<AigLatch> latches,
  // final List<AigOutput> outputs, final List<AigAnd> ands,
  // final List<AigBad> bads, final List<AigInv> invs) {
  // this.inputs = inputs;
  // this.latches = latches;
  // this.outputs = outputs;
  // this.ands = ands;
  // this.bads = bads;
  // this.invs = invs;
  // }

  // static AigTranslation createIC3AigModel(final List<AigInput> inputs) {
  // AigTranslation translation =
  // new AigTranslation(inputs, new ArrayList<AigLatch>(),
  // new ArrayList<AigOutput>(),
  // new ArrayList<AigAnd>(), new ArrayList<AigBad>(),
  // new ArrayList<AigInv>());
  // // for (int i = 1; i <= numInputs; i++) {
  // // translation.inputs.add(translation.chache(i));
  // // }
  // // reserve the next var for the current state of the latch representing
  // // the bad property
  // translation.offset++;
  // return translation;
  // }

  /**
   * @param aigInputs
   * @param aigLatches
   * @param aigOutputs
   * @param aigBads
   * @param aigInvs
   * @param aigAnds
   */
  public AigFile(List<AigExpr> aigInputs, List<AigExpr> aigLatches, List<AigExpr> aigOutputs,
      List<AigExpr> aigAnds, List<AigExpr> aigBads, List<AigExpr> aigInvs) {
    this.inputs = aigInputs;
    this.latches = aigLatches;
    this.outputs = aigOutputs;
    this.ands = aigAnds;
    this.bads = aigBads;
    this.invs = aigInvs;
  }

  // boolean add(AigInput input) {
  // return inputs.add(input);
  // }
  //
  // boolean addInputs(List<AigInput> inputs) {
  // return inputs.addAll(inputs);
  // }
  //
  // boolean add(AigLatch latch) {
  // return latches.add(latch);
  // }
  //
  // boolean addLatches(List<AigLatch> latches) {
  // return latches.addAll(latches);
  // }
  //
  // boolean add(AigOutput output) {
  // return outputs.add(output);
  // }
  //
  // boolean addOutputs(List<AigOutput> outputs) {
  // return outputs.addAll(outputs);
  // }
  //
  // boolean add(AigAnd gate) {
  // return ands.add(gate);
  // }
  //
  // boolean addAndGates(List<AigAnd> gates) {
  // return ands.addAll(gates);
  // }
  //
  // boolean add(AigBad badProperty) {
  // return bads.add(badProperty);
  // }
  //
  // boolean addBads(List<AigBad> bads) {
  // return bads.addAll(bads);
  // }
  //
  // boolean add(AigInv invariant) {
  // return invs.add(invariant);
  // }
  //
  // boolean addInvs(List<AigInv> invs) {
  // return invs.addAll(invs);
  // }

  public File writeAsciiToTmp() throws IOException {
    File tmpDir = new File(System.getProperty("java.io.tmpdir"));
    final String filename = "tmp_aag";
    File file = File.createTempFile(filename, ".tmp", tmpDir);
    FileWriter fWriter = new FileWriter(file, false);
    BufferedWriter bWriter = new BufferedWriter(fWriter);
    bWriter.write(toString());
    bWriter.close();
    return file;
  }

  public File writeAsciiTo(final String outputFilePath, boolean overwrite) throws IOException {
    File outputFile = new File(outputFilePath);
    int cnt = 1;
    while (outputFile.exists() && !overwrite)
      outputFile = new File(new StringBuffer(outputFilePath.split("\\.aag")[0]).append(cnt++)
                                                                               .append(".aag")
                                                                               .toString());
    FileWriter fWriter = new FileWriter(outputFile, false);
    BufferedWriter bWriter = new BufferedWriter(fWriter);
    bWriter.write(toString());
    bWriter.close();
    return outputFile;
  }

  public File writeBinaryToTmp() {
    throw new UnsupportedOperationException();
  }

  public File writeBinaryTo(final String outputFilePath, boolean overwrite) {
    throw new UnsupportedOperationException();
  }

  /**
   * @return
   */
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    /* AIGER Header */
    int M = numVars();
    int I = numInputs();
    int L = numLatches();
    int O = numOutputs();
    int A = numAnds();
    sb.append("aag ").append(M).append(" ").append(I).append(" ").append(L).append(" ").append(O)
      .append(" ").append(A);
    /* AIGER 1.9 Header Extensions */
    int B = numBads();
    int C = numInvs();
    if (B > 0 || C > 0)
      sb.append(" ").append(B);
    if (C > 0)
      sb.append(" ").append(C);
    sb.append("\n");
    /* Inputs */
    appendTo(sb, inputs);
    /* Latches */
    appendTo(sb, latches);
    /* Outputs */
    appendTo(sb, outputs);
    /* Bads */
    appendTo(sb, bads);
    /* Invs */
    appendTo(sb, invs);
    /* Ands */
    appendTo(sb, ands);
    return sb.toString();
  }

  /**
   * @param sb
   * @param exprs
   */
  private void appendTo(StringBuffer sb, List<AigExpr> exprs) {
    for (AigExpr in : exprs) {
      sb.append(in.toString());
      sb.append("\n");
    }
  }

  /**
   * Number of variables
   */
  private int numVars() {
    return numInputs() + numLatches() + numOutputs() + numAnds() + numBads() + numInvs();
  };

  /**
   * Number of inputs
   */
  private int numInputs() {
    return inputs.size();
  }

  /**
   * Number of latches. inv: num_latches >= num_bads
   */
  private int numLatches() {
    return latches.size();
  }

  /**
   * Number of Outputs
   */
  private int numOutputs() {
    return outputs.size();
  }

  /**
   * Number of AND-Gates
   */
  private int numAnds() {
    return ands.size();
  }

  /**
   * Number of Bad Properties
   */
  private int numBads() {
    return bads.size();
  }

  /**
   * Number of Invariants
   */
  private int numInvs() {
    return invs.size();
  }
}

// TODO: Delete the following comments
// The following code is deprecated and only retained for documentation
// purposes.
// public boolean add(AigExpr expression) {
// return add(expression);
// }

// /* (non-Javadoc)
// * @see kodkodmod.engine.Translation#options()
// */
// @Override
// public Options options() {
// return options;
// }

// /* (non-Javadoc)
// * @see kodkodmod.engine.Translation#bounds()
// */
// @Override
// public Bounds bounds() {
// return bounds.unmodifiableView();
// }

// /* (non-Javadoc)
// * @see kodkodmod.engine.Translation#log()
// */
// @Override
// public TranslationLog log() {
// return null;
// }

// /* (non-Javadoc)
// * @see kodkodmod.engine.Translation#translated()
// */
// @Override
// public Map<Formula, AigExpr> translated() {
// return null;
// }
