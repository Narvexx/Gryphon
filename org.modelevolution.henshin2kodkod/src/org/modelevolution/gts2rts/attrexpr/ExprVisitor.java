/*
 * simplexpr -- Copyright (c) 2014-present, Sebastian Gabmeyer
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
import static org.modelevolution.gts2rts.util.EcoreUtil.isEInt;

import java.util.Collection;
import java.util.Map;

import kodkod.ast.Formula;
import kodkod.ast.IntConstant;
import kodkod.ast.IntExpression;
import kodkod.ast.Node;
import kodkod.ast.operator.ExprCastOperator;

import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.henshin.model.Rule;
import org.modelevolution.emf2rel.Enums;
import org.modelevolution.gts2rts.ParamData;
import org.modelevolution.gts2rts.ParamDataset;
import org.modelevolution.gts2rts.attrexpr.SimplExprParser.AddContext;
import org.modelevolution.gts2rts.attrexpr.SimplExprParser.ArithContext;
import org.modelevolution.gts2rts.attrexpr.SimplExprParser.AtomExprContext;
import org.modelevolution.gts2rts.attrexpr.SimplExprParser.BitContext;
import org.modelevolution.gts2rts.attrexpr.SimplExprParser.BitwiseNotContext;
import org.modelevolution.gts2rts.attrexpr.SimplExprParser.BoolContext;
import org.modelevolution.gts2rts.attrexpr.SimplExprParser.EqContext;
import org.modelevolution.gts2rts.attrexpr.SimplExprParser.ExprContext;
import org.modelevolution.gts2rts.attrexpr.SimplExprParser.FuncExprContext;
import org.modelevolution.gts2rts.attrexpr.SimplExprParser.IdContext;
import org.modelevolution.gts2rts.attrexpr.SimplExprParser.IntContext;
import org.modelevolution.gts2rts.attrexpr.SimplExprParser.LogicContext;
import org.modelevolution.gts2rts.attrexpr.SimplExprParser.LogicalNotContext;
import org.modelevolution.gts2rts.attrexpr.SimplExprParser.MultiContext;
import org.modelevolution.gts2rts.attrexpr.SimplExprParser.NegativeIntContext;
import org.modelevolution.gts2rts.attrexpr.SimplExprParser.ParenExprContext;
import org.modelevolution.gts2rts.attrexpr.SimplExprParser.RelContext;
import org.modelevolution.gts2rts.attrexpr.SimplExprParser.ShiftContext;

/**
 * @author Sebastian Gabmeyer
 * 
 */
public abstract class ExprVisitor extends SimplExprBaseVisitor<TypedNode> implements
    SimplExprVisitor<TypedNode> {
  private static final int TRUE = -1;
  private static final IntExpression TRUE_INT = IntConstant.constant(TRUE);
  private static final int FALSE = 0;
  private static final IntExpression FALSE_INT = IntConstant.constant(FALSE);
  protected final ParamDataset params;
  protected final Typechecker typechecker;
  protected final Enums enums;
  private final Rule rule;

  protected ExprVisitor(final Rule rule, final ParamDataset params, final Enums enums) {
    // this.objVar = objVar;
    // this.relation = attrRelation;
    if (rule == null || params == null || enums == null)
      throw new NullPointerException();
    this.rule = rule;
    this.params = params;
    this.typechecker = new Typechecker(params);
    this.enums = enums;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.henshin2kodkod.attrexpr.SimplExprBaseVisitor#visitLogicExpr
   * (org.modelevolution
   * .henshin2kodkod.attrexpr.SimplExprParser.LogicExprContext)
   */
  @Override
  public TypedNode visitLogic(final LogicContext ctx) {
    final TypedNode lhs = ctx.eq().accept(this);

    if (ctx.logic() == null)
      return lhs;
    // if (!isEBoolean(lhs.type())) throw new IllegalArgumentException();
    typechecker.typematch(lhs, EBOOLEAN);
    TypedNode rhs = ctx.logic().accept(this);
    // if (!isEBoolean(rhs.type())) throw new IllegalArgumentException();
    typechecker.typematch(rhs, EBOOLEAN);

    assert lhs.value() instanceof Formula && rhs.value() instanceof Formula;
    final Formula lhsFormula = (Formula) lhs.value();
    final Formula rhsFormula = (Formula) rhs.value();
    final Formula resFormula;

    if (ctx.LAND() != null)
      resFormula = lhsFormula.and(rhsFormula);
    else if (ctx.LOR() != null)
      resFormula = lhsFormula.or(rhsFormula);
    else
      throw new InternalError();

    return new TypedNode(resFormula, EBOOLEAN);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.henshin2kodkod.attrexpr.SimplExprBaseVisitor#visitEqExpr
   * (org.modelevolution .henshin2kodkod.attrexpr.SimplExprParser.EqExprContext)
   */
  @Override
  public TypedNode visitEq(final EqContext ctx) {
    final TypedNode lhs = ctx.rel().accept(this);
    if (ctx.eq() == null)
      return lhs;

    final TypedNode rhs = ctx.eq().accept(this);
    // if (lhs.type() != rhs.type())
    // throw new IllegalArgumentException("Expression '" + ctx.rel().getText() +
    // "' is of type '"
    // + lhs.type() + ", while expression '" + ctx.eq().getText() +
    // "' is of type '"
    // + rhs.type() + "'.");
    typechecker.typecheck(lhs, rhs);

    Formula resFormula;
    if (lhs.value() instanceof Formula) {
      assert rhs.value() instanceof Formula;
      final Formula lhsFormula = (Formula) lhs.value();
      final Formula rhsFormula = (Formula) rhs.value();
      resFormula = lhsFormula.iff(rhsFormula);
    } else if (lhs.value() instanceof IntExpression) {
      assert rhs.value() instanceof IntExpression;
      final IntExpression lhsExpr = (IntExpression) lhs.value();
      final IntExpression rhsExpr = (IntExpression) rhs.value();
      resFormula = lhsExpr.eq(rhsExpr);
    } else {
      throw new InternalError();
    }

    if (ctx.NEQ() != null)
      resFormula = resFormula.not();

    return new TypedNode(resFormula, EBOOLEAN);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.henshin2kodkod.attrexpr.SimplExprBaseVisitor#visitAdd
   * (org.modelevolution .henshin2kodkod.attrexpr.SimplExprParser.AddContext)
   */
  @Override
  public TypedNode visitAdd(AddContext ctx) {
    final TypedNode lhs = ctx.multi().accept(this);
    if (ctx.add() == null)
      return lhs;
    // if (!isEInt(lhs.type())) throw new IllegalArgumentException();
    typechecker.typematch(lhs, EINT);
    final TypedNode rhs = ctx.add().accept(this);
    // if (!isEInt(rhs.type())) throw new IllegalArgumentException();
    typechecker.typematch(rhs, EINT);

    assert lhs.value() instanceof IntExpression && rhs.value() instanceof IntExpression;

    final IntExpression lhsExpr = (IntExpression) lhs.value();
    final IntExpression rhsExpr = (IntExpression) rhs.value();
    final IntExpression resultExpr;
    if (ctx.PLUS() != null)
      resultExpr = lhsExpr.plus(rhsExpr);
    else if (ctx.MINUS() != null)
      resultExpr = lhsExpr.minus(lhsExpr);
    else
      throw new InternalError();

    return new TypedNode(resultExpr, EINT);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.henshin2kodkod.attrexpr.SimplExprBaseVisitor#visitArith
   * (org.modelevolution .henshin2kodkod.attrexpr.SimplExprParser.ArithContext)
   */
  @Override
  public TypedNode visitArith(ArithContext ctx) {
    return ctx.bit().accept(this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.henshin2kodkod.attrexpr.SimplExprBaseVisitor#visitBit
   * (org.modelevolution .henshin2kodkod.attrexpr.SimplExprParser.BitContext)
   */
  @Override
  public TypedNode visitBit(BitContext ctx) {
    final TypedNode lhs = ctx.shift().accept(this);
    if (ctx.bit() == null)
      return lhs;

    final TypedNode rhs = ctx.bit().accept(this);
    // if (lhs.type() != rhs.type() && !(isEInt(lhs.type()) ||
    // isEBoolean(lhs.type())))
    // throw new IllegalArgumentException();
    typechecker.typecheck(lhs, rhs);

    if (!(lhs.value() instanceof IntExpression))
      throw new InternalError();
    final IntExpression lhsExpr = (IntExpression) lhs.value();
    final IntExpression rhsExpr = (IntExpression) rhs.value();
    final IntExpression resultExpr;
    if (ctx.BAND() != null) {
      resultExpr = lhsExpr.and(rhsExpr);
    } else if (ctx.BOR() != null) {
      resultExpr = lhsExpr.or(rhsExpr);
    } else if (ctx.BXOR() != null) {
      resultExpr = lhsExpr.xor(rhsExpr);
    } else {
      throw new InternalError();
    }
    return new TypedNode(resultExpr, lhs.type());
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.henshin2kodkod.attrexpr.SimplExprBaseVisitor#visitBool
   * (org.modelevolution .henshin2kodkod.attrexpr.SimplExprParser.BoolContext)
   */
  @Override
  public TypedNode visitBool(BoolContext ctx) {
    final boolean value = Boolean.parseBoolean(ctx.getText());
    final Node expr = (value ? TRUE_INT : FALSE_INT);
    return new TypedNode(expr, EBOOLEAN);
  }

  public abstract TypedNode visitExpr(ExprContext ctx);

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.henshin2kodkod.attrexpr.SimplExprBaseVisitor#visitId
   * (org.modelevolution. henshin2kodkod.attrexpr.SimplExprParser.IdContext)
   */
  @Override
  public TypedNode visitId(IdContext ctx) {
    final String id = ctx.ID().getText();
    final ParamData param = params.paramDataByName(id);
    if (param != null) {
      param.setUsedByRule(rule);
      return new TypedNode(param.var().apply(ExprCastOperator.SUM), param.type());
    } else {
      final EEnumLiteral literal;
      if ((literal = enums.lookupLiteral(id)) != null) {
        // final EEnumLiteral literal = eenum.getEEnumLiteral(id);
        // assert literal != null;
        final EEnum eenum = literal.getEEnum();
        return new TypedNode(IntConstant.constant(enums.lookupLitValue(eenum, literal.getName())),
                             eenum);
      }
    }
    throw new IllegalArgumentException();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.henshin2kodkod.attrexpr.SimplExprBaseVisitor#visitInt
   * (org.modelevolution .henshin2kodkod.attrexpr.SimplExprParser.IntContext)
   */
  @Override
  public TypedNode visitInt(IntContext ctx) {
    final int value = Integer.parseInt(ctx.getText());
    final Node expr = IntConstant.constant(value);
    return new TypedNode(expr, EINT);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.henshin2kodkod.attrexpr.SimplExprBaseVisitor#visitMulti
   * (org.modelevolution .henshin2kodkod.attrexpr.SimplExprParser.MultiContext)
   */
  @Override
  public TypedNode visitMulti(MultiContext ctx) {
    final TypedNode lhs = ctx.unary().accept(this);
    if (ctx.multi() == null)
      return lhs;

    // if (!isEInt(lhs.type())) throw new IllegalArgumentException();
    typechecker.typematch(lhs, EINT);
    final TypedNode rhs = ctx.multi().accept(this);
    // if (!isEInt(rhs.type())) throw new IllegalArgumentException();
    typechecker.typematch(rhs, EINT);
    assert lhs.value() instanceof IntExpression && rhs.value() instanceof IntExpression;

    final IntExpression lhsExpr = (IntExpression) lhs.value();
    final IntExpression rhsExpr = (IntExpression) rhs.value();
    final IntExpression resultExpr;
    if (ctx.MULT() != null)
      resultExpr = lhsExpr.multiply(rhsExpr);
    else if (ctx.DIV() != null)
      resultExpr = lhsExpr.divide(rhsExpr);
    else if (ctx.MOD() != null)
      resultExpr = lhsExpr.modulo(rhsExpr);
    else
      throw new InternalError();

    return new TypedNode(resultExpr, EINT);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.henshin2kodkod.attrexpr.SimplExprBaseVisitor#visitRel
   * (org.modelevolution .henshin2kodkod.attrexpr.SimplExprParser.RelContext)
   */
  @Override
  public TypedNode visitRel(RelContext ctx) {
    final TypedNode lhs = ctx.arith(0).accept(this);
    if (ctx.arith().size() < 2)
      return lhs;

    assert !(ctx.arith().size() > 2);
    // if (!isEInt(lhs.type())) throw new IllegalArgumentException();
    typechecker.typematch(lhs, EINT);
    final TypedNode rhs = ctx.arith(1).accept(this);
    // if (!isEInt(rhs.type())) throw new IllegalArgumentException();
    typechecker.typematch(rhs, EINT);

    assert lhs.value() instanceof IntExpression && rhs.value() instanceof IntExpression;
    final IntExpression lhsExpr = (IntExpression) lhs.value();
    final IntExpression rhsExpr = (IntExpression) rhs.value();
    final Formula resFormula;

    if (ctx.GT() != null)
      resFormula = lhsExpr.gt(rhsExpr);
    else if (ctx.GTE() != null)
      resFormula = lhsExpr.gte(rhsExpr);
    else if (ctx.LT() != null)
      resFormula = lhsExpr.lt(rhsExpr);
    else if (ctx.LTE() != null)
      resFormula = lhsExpr.lte(rhsExpr);
    else
      throw new InternalError();

    return new TypedNode(resFormula, EBOOLEAN);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.henshin2kodkod.attrexpr.SimplExprBaseVisitor#visitShift
   * (org.modelevolution .henshin2kodkod.attrexpr.SimplExprParser.ShiftContext)
   */
  @Override
  public TypedNode visitShift(ShiftContext ctx) {
    TypedNode lhs = ctx.add().accept(this);
    if (ctx.shift() == null)
      return lhs;
    typechecker.typematch(lhs, EINT);
    TypedNode rhs = ctx.shift().accept(this);
    typechecker.typematch(rhs, EINT);
    // if (!isEInt(lhs.type()) || !isEInt(rhs.type())) throw new
    // IllegalArgumentException();
    assert lhs.value() instanceof IntExpression && rhs.value() instanceof IntExpression;
    final IntExpression lhsExpr = (IntExpression) lhs.value();
    final IntExpression rhsExpr = (IntExpression) rhs.value();
    final IntExpression resultExpr;
    if (ctx.SHA() != null)
      resultExpr = lhsExpr.sha(rhsExpr);
    else if (ctx.SHR() != null)
      resultExpr = lhsExpr.shr(rhsExpr);
    else if (ctx.SHL() != null)
      resultExpr = lhsExpr.shl(rhsExpr);
    else
      throw new InternalError();
    return new TypedNode(resultExpr, EINT);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.henshin2kodkod.attrexpr.SimplExprBaseVisitor#visitAtomExpr
   * (org.modelevolution
   * .henshin2kodkod.attrexpr.SimplExprParser.AtomExprContext)
   */
  @Override
  public TypedNode visitAtomExpr(AtomExprContext ctx) {
    assert ctx.atom() != null;
    return ctx.atom().accept(this);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.henshin2kodkod.attrexpr.SimplExprBaseVisitor#visitBitwiseNot
   * (org.modelevolution
   * .henshin2kodkod.attrexpr.SimplExprParser.BitwiseNotContext)
   */
  @Override
  public TypedNode visitBitwiseNot(BitwiseNotContext ctx) {
    final TypedNode node = ctx.atom().accept(this);
    // if (!isEInt(node.type())) throw new IllegalArgumentException();
    typechecker.typematch(node, EINT);
    assert node.value() instanceof IntExpression;
    Node bitwiseNot = ((IntExpression) node.value()).not();
    return new TypedNode(bitwiseNot, EINT);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.henshin2kodkod.attrexpr.SimplExprBaseVisitor#visitFuncExpr
   * (org.modelevolution
   * .henshin2kodkod.attrexpr.SimplExprParser.FuncExprContext)
   */
  @Override
  public TypedNode visitFuncExpr(FuncExprContext ctx) {
    TypedNode funcArg = ctx.arith().accept(this);
    // if (!isEInt(node.type())) throw new IllegalArgumentException();
    typechecker.typematch(funcArg, EINT);

    assert funcArg.value() instanceof IntExpression;

    final IntExpression funcExpr;
    if (ctx.FUNC().getSymbol().getType() == SimplExprParser.ABS)
      funcExpr = ((IntExpression) funcArg.value()).abs();
    else if (ctx.FUNC().getSymbol().getType() == SimplExprParser.SGN)
      funcExpr = ((IntExpression) funcArg.value()).signum();
    else
      throw new InternalError();

    return new TypedNode(funcExpr, EINT);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.henshin2kodkod.attrexpr.SimplExprBaseVisitor#visitLogicalNot
   * (org.modelevolution
   * .henshin2kodkod.attrexpr.SimplExprParser.LogicalNotContext)
   */
  @Override
  public TypedNode visitLogicalNot(LogicalNotContext ctx) {
    TypedNode node = ctx.atom().accept(this);
    // if (!isEBoolean(node.type())) throw new IllegalArgumentException();
    typechecker.typematch(node, EBOOLEAN);

    assert node.value() instanceof IntExpression;

    final Node logicalNot = ((IntExpression) node.value()).not();
    return new TypedNode(logicalNot, EBOOLEAN);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.modelevolution.henshin2kodkod.attrexpr.SimplExprBaseVisitor#
   * visitNegativeInt(org.modelevolution
   * .henshin2kodkod.attrexpr.SimplExprParser.NegativeIntContext)
   */
  @Override
  public TypedNode visitNegativeInt(NegativeIntContext ctx) {
    TypedNode node = ctx.atom().accept(this);
    // if (!isEInt(node.type())) throw new IllegalArgumentException();
    typechecker.typematch(node, EINT);

    assert node.value() instanceof IntExpression;
    Node negativeInt = ((IntExpression) node.value()).negate();
    return new TypedNode(negativeInt, EINT);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.modelevolution.henshin2kodkod.attrexpr.SimplExprBaseVisitor#visitParenExpr
   * (org.modelevolution
   * .henshin2kodkod.attrexpr.SimplExprParser.ParenExprContext)
   */
  @Override
  public TypedNode visitParenExpr(ParenExprContext ctx) {
    final TypedNode expr = ctx.expr().accept(this);
    return new TypedNode((expr.value()), expr.type());
  }
}
