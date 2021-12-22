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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.modelevolution.commons.modifications;

/**
 * @author Sebatian Gabmeyer
 * 
 */
public enum ModType {
	/**
	 * Indicates that the element has been newly added.
	 */
	NEW,
	/**
	 * Indicates that the visibility of the annotated element changed. In
	 * combination with {@linkplain ModType#NEW} indicates that the newly
	 * created element changes the visibility of the base type or an element of
	 * that base type.
	 * 
	 * @see BasedOn
	 */
	VISIBILITY,
	/**
	 * Indicates that some part of the body, i.e., the implementation, of the
	 * annotated element has changed. In combination with
	 * {@linkplain ModType#NEW} indicates that the newly created element changes
	 * some part of the body of the base type or an element of that base type.
	 * 
	 * @see BasedOn
	 */
	BODY,
	/**
	 * Indicates that the annotated method's signature has been modified. In
	 * combination with {@linkplain ModType#NEW} indicates that the newly
	 * created method changes some part of the method that this newly created
	 * one is based on.
	 * 
	 * @see BasedOn
	 */
	SIGNATURE,
	// DEFINALIZED,
	// INTERFACED
}
