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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kodkod.engine.Statistics;

/**
 * Beware that this implementation of {@link Statistics} is potentially unsafe
 * due to numeric overflows that might occur in the
 * {@link Statistics#elapsedTime()} method due to the internal use of a
 * <code>long</code> variable to store intermediate results. Assertions have
 * been added to the code to check for potential numeric overflows. Thus, you
 * should enable assertion checking in the VM (switch: -ea) if you are unsure
 * whether numeric overflows occur in your verification runs.
 * 
 * @author Sebastian Gabmeyer
 * 
 */
public class BaseStats extends AbstractStats {
  private final String name;
  // private long startTime;
  private Stats parent;
  protected final List<StatRecord> records;

  /**
   * 
   */
  public BaseStats(final String name) {
    this.name = name;
    records = new ArrayList<>();
    parent = null;
  }

  /**
   * @param name
   * @param parent
   */
  public BaseStats(String name, Stats parent) {
    this(name);
    this.parent = parent;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.gryphon.runners.StatRecord#name()
   */
  @Override
  public String name() {
    return name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.gryphon.runners.Statistics#createStatGroup(org.
   * modelevolution.gryphon.runners.Statistics, java.lang.String)
   */
  @Override
  public Stats addStatGroup(String name) {
    final Stats statGroup = new BaseStats(name, this);
    records.add(statGroup);
    return statGroup;
  }

  @Override
  public double elapsedTime() {
    double totalTime = 0;
    for (final StatRecord r : records) {
      assert (Double.MAX_VALUE - totalTime) > r.elapsedTime() : "Potential numeric overflow detected!";
      totalTime += r.elapsedTime();
    }
    return totalTime;
  }

  @Override
  public void record(final double elapsedTime) {
    if (elapsedTime < 0)
      throw new IllegalArgumentException("time < 0.");
    final String name = new StringBuffer("Task ").append(records.size() + 1).toString();
    assert name != null;
    assert !name.isEmpty();
    record(elapsedTime, name);
  }

  @Override
  public void record(final double elapsedTime, final String name) {
    if (elapsedTime < 0)
      throw new IllegalArgumentException("time < 0.");
    if (name == null || name.isEmpty())
      record(elapsedTime);
    else
      records.add(new BaseStatRecord(elapsedTime, name));
  }

  @Override
  public List<StatRecord> records() {
    return Collections.unmodifiableList(records);
  }
  
  @Override
  protected void printStats(final PrintStreamOrWriter p, int indentLevel, int[] columnWidths) {
    final String indent = indent(indentLevel);
    final double total = elapsedTime();
    // final StringBuffer title = new StringBuffer("Stats: ").append(name);
    // if (indentLevel > 0 && parent != null)
    // title.append(" (").append(total).append("ms = ")
    // .append(formatter().format(total / parent.elapsedTime())).append(")");

    /* doing some length calculations to format the output nicely */
    int nameWidth = columnWidths == null
                                        ? Math.max(name.length(),
                                                   calcRequiredNameWidth(records, indentLevel + 1))
                                        : columnWidths[0];
    int timeWidth = columnWidths == null ? String.format("%.0f", total).length() : columnWidths[1];

    if (indentLevel == 0) {
      /* print top level header */
      final String formatString = new StringBuffer("%-").append(nameWidth).append("s  %")
                                                        .append(timeWidth).append(".0f  %4s")
                                                        .toString();
      p.print(indent);
      p.println(String.format(formatString, name, total, formatter().format(elapsedTime() / total)));
    }

    final String childIndent = indent(indentLevel + 1);
    final int childNameWidth = nameWidth - childIndent.length();
    assert childIndent.length() + childNameWidth == nameWidth : (childIndent.length() + childNameWidth)
        + " =/= " + nameWidth;
    final String childFormatString = new StringBuffer("%-").append(childNameWidth).append("s  %")
                                                           .append(timeWidth).append(".0f  %4s")
                                                           .toString();
    
    String input_model = "";
    String total_runtime = "";
    String solving_time = "";
    
    for (final StatRecord r : records) {
      p.print(childIndent);
      
      if (r.name().contains("-philsInit.xmi")) {
    	 input_model = r.name();
    	 total_runtime = Double.toString(r.elapsedTime());
      } else if (r.name().contains("eating")) {
     	 solving_time = Double.toString(r.elapsedTime());
      }
      
      
      
      p.println(String.format(childFormatString, r.name(), r.elapsedTime(),
                              formatter().format(r.elapsedTime() / total)));
      if (r instanceof AbstractStats && r.name().contains("Solver")) {
          ((AbstractStats) r).printStats(p, indentLevel + 1, new int[] { nameWidth, timeWidth });
      }

    }
    try(FileWriter fw = new FileWriter("D:\\Software Science Master\\Research Internship\\evaluation\\var_9-phil.txt", true);
		    BufferedWriter bw = new BufferedWriter(fw);
		    PrintWriter out = new PrintWriter(bw))
		{
    	
    	if (!solving_time.isEmpty()) {
    		out.print(solving_time + ",");
    	}
    	if (!input_model.isEmpty()) {
    		out.print(total_runtime + "," + input_model + '\n');
    		
    	}
    	
  		
		} catch (IOException e) {
		    
	}
    
    // if (parent != null) {
    // p.print(childIndent);
    // p.println(String.format(formatString, "TOTAL", total,
    // formatter().format(1)));
    // }
  }

  /**
   * @param childRecords
   * @param indentLevel
   * @param indent
   * @return
   */
  private int calcRequiredNameWidth(List<StatRecord> childRecords, int indentLevel) {
    final String indent = indent(indentLevel);
    int nameWidth = 0;
    for (final StatRecord r : childRecords) {
      if (r instanceof Stats)
        nameWidth = Math.max(nameWidth,
                             calcRequiredNameWidth(((Stats) r).records(), indentLevel + 1));
      nameWidth = Math.max(nameWidth, r.name().length() + indent.length());
    }
    return nameWidth;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.gryphon.output.Stats#parentStats()
   */
  @Override
  public Stats parentStats() {
    return this.parent;
  }
}
