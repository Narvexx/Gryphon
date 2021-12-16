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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.modelevolution.emf2rel;

import java.util.Set;

import kodkod.ast.Relation;

import org.eclipse.emf.ecore.ENamedElement;

/**
 * @author Sebastian Gabmeyer
 *
 */
public class SimpleRelationRegistry implements RelationRegistry {

  /* (non-Javadoc)
   * @see org.modelevolution.ecore2kodkod.RelationRegistry#bools()
   */
  @Override
  public Relation bools() {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see org.modelevolution.ecore2kodkod.RelationRegistry#ints()
   */
  @Override
  public Relation ints() {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see org.modelevolution.ecore2kodkod.RelationRegistry#relation(org.eclipse.emf.ecore.ENamedElement)
   */
  @Override
  public Relation relation(ENamedElement type) {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see org.modelevolution.ecore2kodkod.RelationRegistry#addBinary(java.lang.String, org.eclipse.emf.ecore.ENamedElement)
   */
  @Override
  public Relation addBinary(String name, ENamedElement type) {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see org.modelevolution.ecore2kodkod.RelationRegistry#addNary(java.lang.String, org.eclipse.emf.ecore.ENamedElement, int)
   */
  @Override
  public Relation addNary(String name, ENamedElement type, int arity) {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see org.modelevolution.ecore2kodkod.RelationRegistry#addUnary(java.lang.String, org.eclipse.emf.ecore.ENamedElement)
   */
  @Override
  public Relation addUnary(String name, ENamedElement type) {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see org.modelevolution.ecore2kodkod.RelationRegistry#relations()
   */
  @Override
  public Set<Relation> relations() {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see org.modelevolution.ecore2kodkod.RelationRegistry#size()
   */
  @Override
  public int size() {
    // TODO Auto-generated method stub
    return 0;
  }

}
