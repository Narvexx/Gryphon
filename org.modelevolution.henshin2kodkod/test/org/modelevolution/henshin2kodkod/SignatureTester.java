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
package org.modelevolution.henshin2kodkod;

import static org.modelevolution.henshin2kodkod.TestHelpers.getPacmanMerger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.Assert;
import org.junit.Test;
import org.modelevolution.emf2rel.FeatureMerger;
import org.modelevolution.emf2rel.Signature;
import org.modelevolution.gts2rts.util.IntUtil;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public final class SignatureTester {

  /**
   * 
   */
  private static final int UPPER_BOUND = 0;
  /**
   * 
   */
  private static final String MODEL = "model/pacman/pacman.ecore";
  private static final String ARRAY_MODEL = "model/array_pacman.ecore";
  private static final String COLORED_MODEL = "model/pacman_colored.ecore";
  private static final String GAME = "model/pacman/Game.xmi";
  private static final String ARRAY_GAME = "model/Game_array.xmi";
  private static final String COLORED_GAME = "model/Game_colored.xmi";
  private static final String SINGLE_FIELD_GAME = "model/Game_single.xmi";
  private static final int BITWIDTH = 4;
  private static final int ARRAYWIDTH = 3;

  @Test
  public void testTranslate() throws IOException {
    final EPackage mm = TestHelpers.loadModel(MODEL);
    final Resource model = TestHelpers.loadInstance(mm, GAME);
    final Map<EClass, Integer> objBounds = new IdentityHashMap<>();

    final FeatureMerger fm = getPacmanMerger(mm);

    final Signature sig = Signature.init(mm, null, model, objBounds, fm, null, BITWIDTH);
    assert sig.bounds() != null;
    assert sig.univ() != null;
  }

  @Test
  public void testNegativeShift() {
    int shift = -1;
    int i = 1 << shift;
    Assert.assertEquals(Integer.MIN_VALUE, i);
  }

  @Test
  public void testLeftShift() {

    final int neg128 = -128;
    final int pos128 = 127;
    final int numOfInts3 = IntUtil.numOfInts(3);
    final int numOfInts2 = IntUtil.numOfInts(2);
    final int numOfInts1 = IntUtil.numOfInts(1);
    final int reqNumOfInts127 = IntUtil.requiredNumOfBits(127);
    final int reqNumOfInts128 = IntUtil.requiredNumOfBits(128);
    final int reqNumOfInts129 = IntUtil.requiredNumOfBits(129);
    final int highNeg128 = Integer.highestOneBit(neg128);
    final int highPos128 = Integer.highestOneBit(pos128);
    final int leadZerosNeg128 = Integer.numberOfLeadingZeros(highNeg128);
    final int trailZerosNeg128 = Integer.numberOfTrailingZeros(highNeg128);
    final int bitwidth = 31;
    final int realNumOfInts = ((2 << bitwidth - 1) - 1) + (2 << bitwidth - 1);
    // assert (2 << 31 - 1) - 1 == Integer.MAX_VALUE;
    final int numOfInts = 1 << bitwidth;

    final int lowInt = 0 - (2 << (2 - 1));
    final int maxInt = ~lowInt;

    final int expLowInt = -8;
    Assert.assertEquals(expLowInt, lowInt);

    final int expMaxInt = 7;
    Assert.assertEquals(expMaxInt, maxInt);

    List<Integer> ints = new ArrayList<>();
    for (int i = lowInt; i <= maxInt; ++i) {
      ints.add(i);
    }

    final int expSize = 16;
    Assert.assertEquals(expSize, numOfInts);
  }
}
