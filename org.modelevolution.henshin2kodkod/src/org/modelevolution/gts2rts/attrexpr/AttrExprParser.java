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
import static org.eclipse.emf.ecore.EcorePackage.Literals.EINT;
import static org.modelevolution.gts2rts.util.EcoreUtil.isEBoolean;
import static org.modelevolution.gts2rts.util.EcoreUtil.isEEnum;
import static org.modelevolution.gts2rts.util.EcoreUtil.isEInt;
import static org.modelevolution.gts2rts.util.IntUtil.isFalse;
import static org.modelevolution.gts2rts.util.IntUtil.isTrue;
import kodkod.ast.Expression;
import kodkod.ast.Formula;
import kodkod.ast.IfExpression;
import kodkod.ast.IntConstant;
import kodkod.ast.IntExpression;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.henshin.model.Rule;
import org.modelevolution.emf2rel.Enums;
import org.modelevolution.emf2rel.Signature;
import org.modelevolution.gts2rts.ParamDataset;
import org.modelevolution.gts2rts.attrexpr.SimplExprParser.ExprContext;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public class AttrExprParser extends ExprVisitor {
  private static final int TRUE = -1;
  private static final IntExpression TRUE_INT = IntConstant.constant(TRUE);
  // private static final Expression TRUE_EXPR = TRUE_INT.toExpression();
  private static final int FALSE = 0;
  private static final IntExpression FALSE_INT = IntConstant.constant(FALSE);
  private final EClassifier attrType;

  // private static final Expression FALSE_EXPR = FALSE_INT.toExpression();
  // /**
  // * @param paramDataset
  // */
  // protected ExprParser(ParamDataset paramDataset) {
  // super(paramDataset);
  // }

  /**
   * @param rule 
   * @param attrType
   * @param paramDataset
   */
  public AttrExprParser(Rule rule, EClassifier attrType, ParamDataset paramDataset, Enums enums) {
    super(rule, paramDataset, enums);
    this.attrType = attrType;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.henshin2kodkod.attrexpr.SimplExprVisitor#visitExpr(org
   * .modelevolution.henshin2kodkod.attrexpr.SimplExprParser.ExprContext)
   */
  @Override
  public TypedNode visitExpr(ExprContext ctx) {
    TypedNode node = ctx.logic().accept(this);

    if (ctx.expr().isEmpty()) {
      node = typechecker.typematch(node, attrType);
      return buildExprNode(node);
    } else if (ctx.expr().size() == 2) {
      TypedNode thenNode = buildExprNode(ctx.expr(1).accept(this));
      TypedNode elseNode = buildExprNode(ctx.expr(2).accept(this));
      typechecker.typecheck(thenNode, elseNode);

      assert node.value() instanceof Formula;
      final Formula cond = (Formula) node.value();
      final Expression ifThenElse = cond.thenElse((Expression) thenNode.value(),
          (Expression) elseNode.value());
      return new TypedNode(ifThenElse, thenNode.type());
    } else {
      throw new InternalError();
    }
  }

  /**
   * Converts an {@link IntExpression} into an {@link Expression expression} and
   * a {@link Formula} <code>f</code> into an {@link IfExpression} of the form
   * <code>if f then -1 else 0</code> where <code>-1</code> corresponds to
   * <i>true</i> and <code>0</code> to <i>false</i>. The type of the returned
   * node is equivalent to the type of parameter <code>node</code>.
   * 
   * @param node
   * @return
   * @throws InternalError
   */
  private TypedNode buildExprNode(TypedNode node) throws InternalError {
    final TypedNode resultNode;
    if (isEBoolean(node.type())) {
      if (isTrue(node.value()) || isFalse(node.value())) {
        assert node.value() instanceof IntExpression;
        resultNode = new TypedNode(((IntExpression) node.value()).toExpression(),
            EBOOLEAN);
      } else {
        assert node.value() instanceof Formula;
        Expression expr = ((Formula) node.value()).thenElse(TRUE_INT, FALSE_INT)
            .toExpression();
        resultNode = new TypedNode(expr, EBOOLEAN);
      }
    } else if (isEInt(node.type())) {
      assert node.value() instanceof IntExpression;
      resultNode = new TypedNode(((IntExpression) node.value()).toExpression(), EINT);
    } else if (isEEnum(node.type())) {
      assert node.value() instanceof IntExpression;
      resultNode = new TypedNode(((IntExpression) node.value()).toExpression(),
          node.type());
    } else {
      throw new InternalError();
    }
    return resultNode;
  }

  public Expression parse(final String sentence) {
    final CharStream input = new ANTLRInputStream(sentence);
    final SimplExprLexer lexer = new SimplExprLexer(input);
    final CommonTokenStream tokens = new CommonTokenStream(lexer);
    final SimplExprParser parser = new SimplExprParser(tokens);
    final TypedNode result = parser.expr().accept(this);

    typechecker.typematch(result, this.attrType);

    if (!(result.value() instanceof Expression))
      throw new InternalError();

    return (Expression) result.value();
  }
  // public static Expression parse(
  // final String sentence, final EClassifier attrType,
  // final ParamDataset paramDataset) {

  // }

}
