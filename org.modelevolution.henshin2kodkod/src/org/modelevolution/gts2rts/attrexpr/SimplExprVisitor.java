// Generated from SimplExpr.g4 by ANTLR 4.4
package org.modelevolution.gts2rts.attrexpr;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link SimplExprParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface SimplExprVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link SimplExprParser#arith}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArith(@NotNull SimplExprParser.ArithContext ctx);
	/**
	 * Visit a parse tree produced by the {@code LogicalNot}
	 * labeled alternative in {@link SimplExprParser#unary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogicalNot(@NotNull SimplExprParser.LogicalNotContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Bool}
	 * labeled alternative in {@link SimplExprParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBool(@NotNull SimplExprParser.BoolContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BitwiseNot}
	 * labeled alternative in {@link SimplExprParser#unary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBitwiseNot(@NotNull SimplExprParser.BitwiseNotContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ParenExpr}
	 * labeled alternative in {@link SimplExprParser#unary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenExpr(@NotNull SimplExprParser.ParenExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimplExprParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(@NotNull SimplExprParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimplExprParser#add}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdd(@NotNull SimplExprParser.AddContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimplExprParser#logic}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLogic(@NotNull SimplExprParser.LogicContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimplExprParser#bit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBit(@NotNull SimplExprParser.BitContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AtomExpr}
	 * labeled alternative in {@link SimplExprParser#unary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtomExpr(@NotNull SimplExprParser.AtomExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code FuncExpr}
	 * labeled alternative in {@link SimplExprParser#unary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFuncExpr(@NotNull SimplExprParser.FuncExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimplExprParser#multi}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMulti(@NotNull SimplExprParser.MultiContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Id}
	 * labeled alternative in {@link SimplExprParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitId(@NotNull SimplExprParser.IdContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimplExprParser#rel}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRel(@NotNull SimplExprParser.RelContext ctx);
	/**
	 * Visit a parse tree produced by the {@code NegativeInt}
	 * labeled alternative in {@link SimplExprParser#unary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNegativeInt(@NotNull SimplExprParser.NegativeIntContext ctx);
	/**
	 * Visit a parse tree produced by the {@code Int}
	 * labeled alternative in {@link SimplExprParser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInt(@NotNull SimplExprParser.IntContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimplExprParser#shift}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitShift(@NotNull SimplExprParser.ShiftContext ctx);
	/**
	 * Visit a parse tree produced by {@link SimplExprParser#eq}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEq(@NotNull SimplExprParser.EqContext ctx);
}