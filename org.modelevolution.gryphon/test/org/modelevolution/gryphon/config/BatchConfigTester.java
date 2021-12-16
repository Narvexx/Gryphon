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
package org.modelevolution.gryphon.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;
import org.modelevolution.gryphon.input.config.BenchmarkConfig;
import org.modelevolution.gryphon.input.config.ConfigLoader;
import org.modelevolution.gryphon.input.config.VerificationConfig;
import org.modelevolution.gryphon.input.config.WarmupConfig;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class BatchConfigTester {

  @Test
  public void testLoad() {
    final ConfigLoader batchConfig;
    try {
      batchConfig = ConfigLoader.load("benchmark.cfg");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return;
    }
    assertEquals(2, batchConfig.benchmarkConfigs().size());
    assertEquals(1, batchConfig.verificationConfigs().size());
    assertNotNull(batchConfig.warmupConfig());
    final WarmupConfig warmupConfig = batchConfig.warmupConfig();
    assertEquals(10, warmupConfig.getRuns());
    assertEquals("/home", warmupConfig.getWorkdir());
    String[] initials = warmupConfig.getInitial();
    assertEquals(2, initials.length);
    assertEquals("init1.xmi", initials[0]);
    assertEquals("init2.xmi", initials[1]);
    // assertEquals("metamodel.ecore", warmupConfig.getMetamodel());
    assertEquals("transformation.henshin", warmupConfig.getTransformation());
    assertEquals("propName1,propName2", warmupConfig.getProperties());
    assertEquals("output.txt", warmupConfig.getOutput());

    for (final BenchmarkConfig config : batchConfig.benchmarkConfigs()) {
      if (config.getName() != null)
        assertEquals("name1", config.getName());
      assertEquals("/home", config.getWorkdir());
      assertEquals(10, config.getRuns());
      assertEquals(5, config.getAvgruns());
      initials = config.getInitial();
      assertNotNull(initials);
      assertEquals("init1.xmi", initials[0]);
      if (initials.length > 1)
        assertEquals("init2.xmi", initials[1]);
      // assertEquals("metamodel.ecore", config.getMetamodel());
      assertEquals("transformation.henshin", config.getTransformation());
      if (config.getProperties() != null)
        assertEquals("propName1,propName2", config.getProperties());
      if (config.getRules() != null)
        assertEquals("rule1,rule2", config.getRules());
      assertEquals("output.txt", config.getOutput());
    }

    for (final VerificationConfig config : batchConfig.verificationConfigs()) {
      if (config.getName() != null)
        assertEquals("name1", config.getName());
      assertEquals("/home", config.getWorkdir());
      initials = config.getInitial();
      assertNotNull(initials);
      assertEquals("init1.xmi", initials[0]);
      if (initials.length > 1)
        assertEquals("init2.xmi", initials[1]);
      // assertEquals("metamodel.ecore", config.getMetamodel());
      assertEquals("transformation.henshin", config.getTransformation());
      if (config.getProperties() != null)
        assertEquals("propName1,propName2", config.getProperties());
      if (config.getRules() != null)
        assertEquals("rule1,rule2", config.getRules());
      assertEquals("output.txt", config.getOutput());
    }

  }

}
