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
package org.modelevolution.aig;

/**
 * @author Sebastian Gabmeyer
 *
 */
public interface AigVisitor<R, A> {
  
  public R visit(AigAnd aigAnd, A arg);
  
  public R visit(AigBad aigBad, A arg);
  
  public R visit(AigInput aigInput, A arg);
  
  public R visit(AigInv aigInv, A arg);
  
  public R visit(AigLatch aigLatch, A arg);
  
  public R visit(AigLit aigLit, A arg);
  
  public R visit(AigNot aigNot, A arg);
  
  public R visit(AigOutput aigOutput, A arg);
}
