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
package org.modelevolution.gts2rts.attrexpr;

import static org.eclipse.emf.ecore.EcorePackage.Literals.EBOOLEAN;
import static org.modelevolution.gts2rts.util.IntUtil.isFalse;
import static org.modelevolution.gts2rts.util.IntUtil.isTrue;
import kodkod.ast.Formula;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.henshin.model.AttributeCondition;
import org.eclipse.emf.henshin.model.Rule;
import org.modelevolution.emf2rel.Enums;
import org.modelevolution.emf2rel.Signature;
import org.modelevolution.gts2rts.ParamDataset;
import org.modelevolution.gts2rts.attrexpr.SimplExprParser.ExprContext;

/**
 * A parser for {@link AttributeCondition attribute conditions}.
 * 
 * @author Sebastian Gabmeyer
 * 
 */
public class AttrCondParser extends ExprVisitor {

  // /**
  // * @param paramDataset
  // */
  // private AttrCondParser(final ParamDataset paramDataset) {
  // super(paramDataset);
  // }

  /**
   * @param rule
   * @param params
   * @param enums
   */
  public AttrCondParser(Rule rule, ParamDataset params, Enums enums) {
    super(rule, params, enums);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.henshin2kodkod.attrexpr.ExprVisitor#visitExpr(org.
   * modelevolution.henshin2kodkod.attrexpr.SimplExprParser.ExprContext)
   */
  @Override
  public TypedNode visitExpr(ExprContext ctx) {
    TypedNode node = buildFormulaNode(ctx.logic().accept(this));

    if (ctx.expr().isEmpty())
      return node;

    final TypedNode thenBranch = buildFormulaNode(ctx.expr(1).accept(this));
    final TypedNode elseBranch = buildFormulaNode(ctx.expr(2).accept(this));
    // if (isTrue(thenBranch.value()))
    // thenBranch = new TypedNode(Formula.TRUE, EBOOLEAN);
    // else if (isFalse(thenBranch.value()))
    // thenBranch = new TypedNode(Formula.FALSE, EBOOLEAN);
    // typechecker.typematch(thenBranch, EBOOLEAN);
    // if (isTrue(elseBranch.value()))
    // elseBranch = new TypedNode(Formula.TRUE, EBOOLEAN);
    // else if (isFalse(elseBranch.value()))
    // elseBranch = new TypedNode(Formula.FALSE, EBOOLEAN);
    // typechecker.typematch(elseBranch, EBOOLEAN);
    // if (!(thenBranch.value() instanceof Formula)) throw new
    // IllegalArgumentException();
    // if (!(elseBranch.value() instanceof Formula)) throw new
    // IllegalArgumentException();
    final Formula ifThenElse = ((Formula) node.value()).implies(
        (Formula) thenBranch.value()).and(
        ((Formula) node.value()).not().implies((Formula) elseBranch.value()));
    return new TypedNode(ifThenElse, EBOOLEAN);
  }

  private TypedNode buildFormulaNode(final TypedNode node) {
    typechecker.typematch(node, EBOOLEAN);
    if (!(node.value() instanceof Formula))
      throw new IllegalArgumentException();

    TypedNode result;

    if (isTrue(node.value()))
      result = new TypedNode(Formula.TRUE, EBOOLEAN);
    else if (isFalse(node.value()))
      result = new TypedNode(Formula.FALSE, EBOOLEAN);
    else
      result = node;

    return result;
  }

  /**
   * @param conditionText
   * @return
   */
  public Formula parse(final String sentence) {
    if (sentence == null)
      throw new NullPointerException();
    final CharStream input = new ANTLRInputStream(sentence);
    final SimplExprLexer lexer = new SimplExprLexer(input);
    final CommonTokenStream tokens = new CommonTokenStream(lexer);
    final SimplExprParser parser = new SimplExprParser(tokens);
    // final AttrCondParser condParser = new AttrCondParser(params, enums);
    final TypedNode result = parser.expr().accept(this);

    if (!(result.value() instanceof Formula))
      throw new InternalError();

    return (Formula) result.value();
  }

}
