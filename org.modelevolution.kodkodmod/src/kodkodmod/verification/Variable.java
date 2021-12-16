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
package kodkodmod.verification;

import kodkod.ast.LeafExpression;
import kodkod.util.ints.IntSet;

/**
 * @author Sebastian Gabmeyer
 *
 */
@Deprecated
public abstract class Variable {

	private final LeafExpression variable;
	private final IntSet boolVars;

	/**
	 * 
	 */
	protected Variable(final LeafExpression variable, final IntSet boolVars) {
		super();
		if(variable == null) throw new IllegalArgumentException("variable must not be null.");		
		this.variable = variable;
		this.boolVars = boolVars;
	}

	public LeafExpression variable() {
		return variable;
	}

	public IntSet boolVars() {
		return boolVars;
	}

	/**
	 * @return
	 */
	public String name() {
		String name = variable.name();
		if(name.startsWith("$")) 
			return name.substring(1);
		return name;
	}

}