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
package org.modelevolution.gryphon.input.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class ConfigLoader {
  private static final String WARMUP_SECTION = "Warmup";
  private static final String BENCHMARK_SECTION = "Benchmark";
  private static final Object VERIFICATION_SECTION = "Verification";
  private WarmupConfig warmup;
  private final Collection<VerificationConfig> verifications;
  private final Collection<BenchmarkConfig> benchmarks;

  /**
   * 
   */
  public ConfigLoader() {
    this.verifications = new ArrayList<>();
    this.benchmarks = new ArrayList<>();
  }

  public static ConfigLoader load(final String configFile) throws InvalidFileFormatException,
      IOException {
    final Ini ini = new Ini();

    final Config iniConfig = new Config();
    iniConfig.setMultiOption(true);
    iniConfig.setMultiSection(true);
    ini.setConfig(iniConfig);

    ini.load(new File(configFile));

    final ConfigLoader batchConfig = new ConfigLoader();
    final Ini.Section warmupSection = ini.get(WARMUP_SECTION);
    if (warmupSection != null)
      batchConfig.warmup = warmupSection.as(WarmupConfig.class);

    final Collection<Ini.Section> benchmarkSections = ini.getAll(BENCHMARK_SECTION);
    if (benchmarkSections != null) {
      for (final Ini.Section section : benchmarkSections) {
        batchConfig.benchmarks.add(section.as(BenchmarkConfig.class));
      }
    }

    final Collection<Ini.Section> runSections = ini.getAll(VERIFICATION_SECTION);
    if (runSections != null) {
      for (final Ini.Section section : runSections) {
        batchConfig.verifications.add(section.as(VerificationConfig.class));
      }
    }

    return batchConfig;
  }

  public Collection<BenchmarkConfig> benchmarkConfigs() {
    return Collections.unmodifiableCollection(benchmarks);
  }

  public Collection<VerificationConfig> verificationConfigs() {
    return Collections.unmodifiableCollection(verifications);
  }

  public WarmupConfig warmupConfig() {
    return warmup;
  }

}
