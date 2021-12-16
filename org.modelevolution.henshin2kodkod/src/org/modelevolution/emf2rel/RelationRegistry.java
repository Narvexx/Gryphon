/*
 * henshin2kodkod -- Copyright (c) 2014-present, Sebastian Gabmeyer
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.modelevolution.emf2rel;

import java.util.Set;

import kodkod.ast.Relation;

import org.eclipse.emf.ecore.ENamedElement;

/**
 * @author Sebastian Gabmeyer
 *
 */
public interface RelationRegistry {

//  public abstract Relation addInts();
//
//  public abstract Relation addBools();

  public abstract Relation bools();

  public abstract Relation ints();

//  public abstract boolean hasInts();
//
//  public abstract boolean hasBools();

  public abstract Relation relation(ENamedElement type);

  public abstract Relation addBinary(String name, ENamedElement type);

  public abstract Relation addNary(String name, ENamedElement type, int arity);

  public abstract Relation addUnary(String name, ENamedElement type);

  public abstract Set<Relation> relations();

  public abstract int size();

}