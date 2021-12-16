/* 
 * org.modelevolution.gryphon -- Copyright (c) 2015-present, Sebastian Gabmeyer
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
package org.modelevolution.gryphon.output;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public abstract class AbstractStats implements Stats {
  protected static abstract class PrintStreamOrWriter {
    abstract void print(Object o);

    abstract void println();

    abstract void println(Object o);
  }

  protected static class WrappedPrintStream extends PrintStreamOrWriter {
    private final PrintStream stream;

    public WrappedPrintStream(final PrintStream s) {
      this.stream = s;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.modelevolution.gryphon.output.AbstractPrintableStats.PrintStreamOrWriter
     * #print(java.lang.Object)
     */
    @Override
    void print(final Object o) {
      stream.print(o);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.modelevolution.gryphon.output.AbstractPrintableStats.PrintStreamOrWriter
     * #println()
     */
    @Override
    void println() {
      stream.println();
    }

    @Override
    void println(final Object o) {
      stream.println(o);
    }
  }

  protected static class WrappedPrintWriter extends PrintStreamOrWriter {
    private final PrintWriter writer;

    public WrappedPrintWriter(final PrintWriter w) {
      this.writer = w;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.modelevolution.gryphon.output.AbstractPrintableStats.PrintStreamOrWriter
     * #print(java.lang.Object)
     */
    @Override
    void print(final Object o) {
      writer.print(o);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.modelevolution.gryphon.output.AbstractPrintableStats.PrintStreamOrWriter
     * #println()
     */
    @Override
    void println() {
      writer.println();
    }

    @Override
    void println(final Object o) {
      writer.println(o);
    }
  }

  /** Indentation is set to 2 spaces */
  private static String INDENT = "  ";

  private final NumberFormat formatter;

  protected AbstractStats() {
    formatter = NumberFormat.getPercentInstance();
    formatter.setRoundingMode(RoundingMode.HALF_EVEN);
  }

  protected AbstractStats(final NumberFormat formatter) {
    if (formatter == null)
      throw new NullPointerException("formatter == null.");
    this.formatter = formatter;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.gryphon.output.PrintableStats#printStats()
   */
  @Override
  public void printStats() {
    printStats(System.out);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.gryphon.output.PrintableStats#printStats(java.io.PrintStream
   * )
   */
  @Override
  public void printStats(final PrintStream stream) {
    printStats(new WrappedPrintStream(stream), 0, null);
    // stream.flush();
    // stream.close();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.gryphon.output.PrintableStats#printStats(java.io.PrintWriter
   * )
   */
  @Override
  public void printStats(final PrintWriter writer) {
    printStats(new WrappedPrintWriter(writer), 0, null);
    writer.flush();
    writer.close();
  }

  protected NumberFormat formatter() {
    return formatter;
  }

  protected String indent(int level) {
    final StringBuffer indentation = new StringBuffer(INDENT.length() * level);
    for (int i = 0; i < level; ++i) {
      indentation.append(INDENT);
    }
    return indentation.toString();
  }

  protected abstract void printStats(final PrintStreamOrWriter p, int indentLevel,
      int[] columnWidths);

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return new StringBuffer(name()).append(": ").append(elapsedTime()).toString();
  }
}
