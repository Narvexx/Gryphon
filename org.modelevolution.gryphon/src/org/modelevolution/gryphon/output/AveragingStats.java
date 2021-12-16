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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class AveragingStats extends AbstractStats {

  private final String name;
  private final Map<String, List<Stats>> records;
  private final int numAveragedRuns;

  /**
   * 
   */
  public AveragingStats(String name, int numAveragedRuns) {
    super();
    if (name == null)
      throw new NullPointerException();
    if (numAveragedRuns < 1)
      throw new IllegalArgumentException("numAveragedRuns < 1.");

    this.name = name;
    this.records = new LinkedHashMap<>();
    this.numAveragedRuns = numAveragedRuns;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.gryphon.output.StatRecord#name()
   */
  @Override
  public String name() {
    return name;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.gryphon.output.StatRecord#elapsedTime()
   */
  @Override
  public double elapsedTime() {
    double totalTime = 0;
    // int numBenchmarks = 0;
    for (final List<Stats> statsList : records.values()) {
      for (final Stats stats : statsList) {
        assert Double.MAX_VALUE - totalTime < stats.elapsedTime() : "Potential numeric overflow detected.";
        totalTime += stats.elapsedTime();
        // numBenchmarks++;
      }
    }
    return totalTime;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.gryphon.output.Stats#addStatGroup(java.lang.String)
   */
  @Override
  public Stats addStatGroup(String name) {
    final Stats statGroup;
    if (records.containsKey(name)) {
      statGroup = new BaseStats(name);
      records.get(name).add(statGroup);
    } else {
      final List<Stats> statsList = new ArrayList<>();
      records.put(name, statsList);
      statGroup = new BaseStats(name, this);
      statsList.add(statGroup);
    }
    return statGroup;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.gryphon.output.Stats#record(long)
   */
  @Override
  public void record(double time) {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc) If the two activities are recorded that both have the same
   * name, then these two activities are treated as being the same and their
   * recorded times are summed.
   * 
   * @see org.modelevolution.gryphon.output.Stats#record(long, java.lang.String)
   */
  /**
   * Note that this method is not supported by {@link AveragingStats} class as
   * it acts as a container for {@link BaseStats} only.
   * 
   * @throws UnsupportedOperationException
   */
  @Override
  public void record(double time, String name) {
    throw new UnsupportedOperationException();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.gryphon.output.Stats#records()
   */
  @Override
  public List<StatRecord> records() {
    final List<StatRecord> averagedStats = new ArrayList<>(records.values().size());

    for (final Entry<String, List<Stats>> statsEntry : records.entrySet()) {
      final String statsName = statsEntry.getKey();
      final List<Stats> statsList = statsEntry.getValue();
      BigDecimal sum = BigDecimal.ZERO;
      int cntDown = statsList.size();
      for (final Stats s : statsList) {
        /* Start averaging after (stats.size() - numAveragedRuns) runs */
        if (cntDown-- > numAveragedRuns)
          continue;
        // assert (Double.MAX_VALUE - sum) < s.elapsedTime();
        sum = sum.add(BigDecimal.valueOf(s.elapsedTime()));
      }
      double avg = sum.divide(BigDecimal.valueOf(numAveragedRuns)).doubleValue();
      averagedStats.add(new BaseStatRecord(avg, statsName));
    }
    return averagedStats;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.gryphon.output.Stats#parentStats()
   */
  @Override
  public Stats parentStats() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.gryphon.output.AbstractStats#printStats(org.modelevolution
   * .gryphon.output.AbstractStats.PrintStreamOrWriter, int)
   */
  @Override
  protected void printStats(PrintStreamOrWriter p, int indentLevel, int[] columnWidths) {
    final List<StatRecord> avgStatsList = records();
    final StringBuffer header = new StringBuffer("Averaged Statistics for '");
    header.append(name).append("' (").append("Runs: ")
          .append(records.values().iterator().next().size()).append("/Counted: ")
          .append(numAveragedRuns).append(")");
    p.println(header.toString());
    p.println("[format: Name/Avg. Time/Std. Deviation (in ms)]");

    int minNameWidth = 0;
    int minTimeWidth = 0;
    for (StatRecord s : avgStatsList) {
      if (minNameWidth < s.name().length())
        minNameWidth = s.name().length();
      if (minTimeWidth < String.valueOf(s.elapsedTime()).length())
        minTimeWidth = String.valueOf(s.elapsedTime()).length();
    }
    final String formatString = "%" + minNameWidth + "s %" + minTimeWidth + ".0f %" + minTimeWidth
        + ".2f";
    for (int i = 0; i < avgStatsList.size(); ++i) {
      final StatRecord avgStats = avgStatsList.get(i);
      final double sigma = calculateStdDeviation(avgStats);
      p.println(String.format(formatString, avgStats.name(), avgStats.elapsedTime(), sigma));
    }

  }

  /**
   * @param avgStats
   * @return
   */
  private double calculateStdDeviation(final StatRecord avgStats) {
    final List<Stats> statsList = records.get(avgStats.name());
    final double avg = avgStats.elapsedTime();
    int cntDown = statsList.size();
    double varianceNominator = 0;
    for (final Stats stats : statsList) {
      if (cntDown-- > numAveragedRuns)
        continue;
      varianceNominator += Math.pow(avg - stats.elapsedTime(), 2);
    }
    final int varianceDenominator;
    if (numAveragedRuns == statsList.size()) {
      varianceDenominator = numAveragedRuns;
    } else {
      /* Bessel's correction */
      varianceDenominator = numAveragedRuns - 1;
    }
    /* Standard Deviation */
    return (Math.sqrt(varianceNominator / varianceDenominator));
  }

}
