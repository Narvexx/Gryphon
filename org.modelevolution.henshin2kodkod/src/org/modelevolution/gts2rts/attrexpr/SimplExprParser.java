// Generated from SimplExpr.g4 by ANTLR 4.4
package org.modelevolution.gts2rts.attrexpr;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SimplExprParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__1=1, T__0=2, WS=3, NL=4, MINUS=5, BNOT=6, LNOT=7, INT=8, ZERO=9, BOOL=10, 
		ID=11, LPAR=12, RPAR=13, GTE=14, LTE=15, EQ=16, NEQ=17, GT=18, LT=19, 
		MULT=20, DIV=21, MOD=22, PLUS=23, SHR=24, SHA=25, SHL=26, BOR=27, BAND=28, 
		BXOR=29, FUNC=30, ABS=31, SGN=32, LAND=33, LOR=34;
	public static final String[] tokenNames = {
		"<INVALID>", "':'", "'?'", "WS", "NL", "'-'", "'~'", "'!'", "INT", "'0'", 
		"BOOL", "ID", "'('", "')'", "'>='", "'<='", "'=='", "'!='", "'>'", "'<'", 
		"'*'", "'/'", "'%'", "'+'", "'>>>'", "'>>'", "'<<'", "'|'", "'&'", "'^'", 
		"FUNC", "'Math.abs'", "'Math.signum'", "'&&'", "'||'"
	};
	public static final int
		RULE_expr = 0, RULE_unary = 1, RULE_atom = 2, RULE_logic = 3, RULE_eq = 4, 
		RULE_rel = 5, RULE_arith = 6, RULE_bit = 7, RULE_shift = 8, RULE_add = 9, 
		RULE_multi = 10;
	public static final String[] ruleNames = {
		"expr", "unary", "atom", "logic", "eq", "rel", "arith", "bit", "shift", 
		"add", "multi"
	};

	@Override
	public String getGrammarFileName() { return "SimplExpr.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SimplExprParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ExprContext extends ParserRuleContext {
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public LogicContext logic() {
			return getRuleContext(LogicContext.class,0);
		}
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SimplExprVisitor ) return ((SimplExprVisitor<? extends T>)visitor).visitExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_expr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(22); logic();
			setState(28);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(23); match(T__0);
				setState(24); expr();
				setState(25); match(T__1);
				setState(26); expr();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UnaryContext extends ParserRuleContext {
		public UnaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unary; }
	 
		public UnaryContext() { }
		public void copyFrom(UnaryContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class AtomExprContext extends UnaryContext {
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public AtomExprContext(UnaryContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SimplExprVisitor ) return ((SimplExprVisitor<? extends T>)visitor).visitAtomExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class LogicalNotContext extends UnaryContext {
		public List<TerminalNode> LNOT() { return getTokens(SimplExprParser.LNOT); }
		public TerminalNode LNOT(int i) {
			return getToken(SimplExprParser.LNOT, i);
		}
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public LogicalNotContext(UnaryContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SimplExprVisitor ) return ((SimplExprVisitor<? extends T>)visitor).visitLogicalNot(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class FuncExprContext extends UnaryContext {
		public ArithContext arith() {
			return getRuleContext(ArithContext.class,0);
		}
		public TerminalNode FUNC() { return getToken(SimplExprParser.FUNC, 0); }
		public FuncExprContext(UnaryContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SimplExprVisitor ) return ((SimplExprVisitor<? extends T>)visitor).visitFuncExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BitwiseNotContext extends UnaryContext {
		public TerminalNode BNOT(int i) {
			return getToken(SimplExprParser.BNOT, i);
		}
		public List<TerminalNode> BNOT() { return getTokens(SimplExprParser.BNOT); }
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public BitwiseNotContext(UnaryContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SimplExprVisitor ) return ((SimplExprVisitor<? extends T>)visitor).visitBitwiseNot(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ParenExprContext extends UnaryContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ParenExprContext(UnaryContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SimplExprVisitor ) return ((SimplExprVisitor<? extends T>)visitor).visitParenExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NegativeIntContext extends UnaryContext {
		public TerminalNode MINUS(int i) {
			return getToken(SimplExprParser.MINUS, i);
		}
		public List<TerminalNode> MINUS() { return getTokens(SimplExprParser.MINUS); }
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public NegativeIntContext(UnaryContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SimplExprVisitor ) return ((SimplExprVisitor<? extends T>)visitor).visitNegativeInt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UnaryContext unary() throws RecognitionException {
		UnaryContext _localctx = new UnaryContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_unary);
		int _la;
		try {
			setState(58);
			switch (_input.LA(1)) {
			case BNOT:
				_localctx = new BitwiseNotContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(31); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(30); match(BNOT);
					}
					}
					setState(33); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==BNOT );
				setState(35); atom();
				}
				break;
			case MINUS:
				_localctx = new NegativeIntContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(37); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(36); match(MINUS);
					}
					}
					setState(39); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==MINUS );
				setState(41); atom();
				}
				break;
			case LNOT:
				_localctx = new LogicalNotContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(43); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(42); match(LNOT);
					}
					}
					setState(45); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==LNOT );
				setState(47); atom();
				}
				break;
			case FUNC:
				_localctx = new FuncExprContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(48); match(FUNC);
				setState(49); match(LPAR);
				setState(50); arith();
				setState(51); match(RPAR);
				}
				break;
			case LPAR:
				_localctx = new ParenExprContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(53); match(LPAR);
				setState(54); expr();
				setState(55); match(RPAR);
				}
				break;
			case INT:
			case BOOL:
			case ID:
				_localctx = new AtomExprContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(57); atom();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AtomContext extends ParserRuleContext {
		public AtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atom; }
	 
		public AtomContext() { }
		public void copyFrom(AtomContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class BoolContext extends AtomContext {
		public TerminalNode BOOL() { return getToken(SimplExprParser.BOOL, 0); }
		public BoolContext(AtomContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SimplExprVisitor ) return ((SimplExprVisitor<? extends T>)visitor).visitBool(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class IdContext extends AtomContext {
		public TerminalNode ID() { return getToken(SimplExprParser.ID, 0); }
		public IdContext(AtomContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SimplExprVisitor ) return ((SimplExprVisitor<? extends T>)visitor).visitId(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class IntContext extends AtomContext {
		public TerminalNode INT() { return getToken(SimplExprParser.INT, 0); }
		public IntContext(AtomContext ctx) { copyFrom(ctx); }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SimplExprVisitor ) return ((SimplExprVisitor<? extends T>)visitor).visitInt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AtomContext atom() throws RecognitionException {
		AtomContext _localctx = new AtomContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_atom);
		try {
			setState(63);
			switch (_input.LA(1)) {
			case INT:
				_localctx = new IntContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(60); match(INT);
				}
				break;
			case BOOL:
				_localctx = new BoolContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(61); match(BOOL);
				}
				break;
			case ID:
				_localctx = new IdContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(62); match(ID);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LogicContext extends ParserRuleContext {
		public TerminalNode LOR() { return getToken(SimplExprParser.LOR, 0); }
		public LogicContext logic() {
			return getRuleContext(LogicContext.class,0);
		}
		public TerminalNode LAND() { return getToken(SimplExprParser.LAND, 0); }
		public EqContext eq() {
			return getRuleContext(EqContext.class,0);
		}
		public LogicContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_logic; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SimplExprVisitor ) return ((SimplExprVisitor<? extends T>)visitor).visitLogic(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LogicContext logic() throws RecognitionException {
		LogicContext _localctx = new LogicContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_logic);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(65); eq();
			setState(68);
			_la = _input.LA(1);
			if (_la==LAND || _la==LOR) {
				{
				setState(66);
				_la = _input.LA(1);
				if ( !(_la==LAND || _la==LOR) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				setState(67); logic();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EqContext extends ParserRuleContext {
		public TerminalNode NEQ() { return getToken(SimplExprParser.NEQ, 0); }
		public RelContext rel() {
			return getRuleContext(RelContext.class,0);
		}
		public EqContext eq() {
			return getRuleContext(EqContext.class,0);
		}
		public TerminalNode EQ() { return getToken(SimplExprParser.EQ, 0); }
		public EqContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eq; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SimplExprVisitor ) return ((SimplExprVisitor<? extends T>)visitor).visitEq(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EqContext eq() throws RecognitionException {
		EqContext _localctx = new EqContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_eq);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(70); rel();
			setState(73);
			_la = _input.LA(1);
			if (_la==EQ || _la==NEQ) {
				{
				setState(71);
				_la = _input.LA(1);
				if ( !(_la==EQ || _la==NEQ) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				setState(72); eq();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RelContext extends ParserRuleContext {
		public TerminalNode GTE() { return getToken(SimplExprParser.GTE, 0); }
		public List<ArithContext> arith() {
			return getRuleContexts(ArithContext.class);
		}
		public TerminalNode LT() { return getToken(SimplExprParser.LT, 0); }
		public TerminalNode LTE() { return getToken(SimplExprParser.LTE, 0); }
		public ArithContext arith(int i) {
			return getRuleContext(ArithContext.class,i);
		}
		public TerminalNode GT() { return getToken(SimplExprParser.GT, 0); }
		public RelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rel; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SimplExprVisitor ) return ((SimplExprVisitor<? extends T>)visitor).visitRel(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RelContext rel() throws RecognitionException {
		RelContext _localctx = new RelContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_rel);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(75); arith();
			setState(78);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << GTE) | (1L << LTE) | (1L << GT) | (1L << LT))) != 0)) {
				{
				setState(76);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << GTE) | (1L << LTE) | (1L << GT) | (1L << LT))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				setState(77); arith();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArithContext extends ParserRuleContext {
		public BitContext bit() {
			return getRuleContext(BitContext.class,0);
		}
		public ArithContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arith; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SimplExprVisitor ) return ((SimplExprVisitor<? extends T>)visitor).visitArith(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArithContext arith() throws RecognitionException {
		ArithContext _localctx = new ArithContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_arith);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(80); bit();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BitContext extends ParserRuleContext {
		public TerminalNode BXOR() { return getToken(SimplExprParser.BXOR, 0); }
		public TerminalNode BAND() { return getToken(SimplExprParser.BAND, 0); }
		public TerminalNode BOR() { return getToken(SimplExprParser.BOR, 0); }
		public BitContext bit() {
			return getRuleContext(BitContext.class,0);
		}
		public ShiftContext shift() {
			return getRuleContext(ShiftContext.class,0);
		}
		public BitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bit; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SimplExprVisitor ) return ((SimplExprVisitor<? extends T>)visitor).visitBit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BitContext bit() throws RecognitionException {
		BitContext _localctx = new BitContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_bit);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(82); shift();
			setState(85);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOR) | (1L << BAND) | (1L << BXOR))) != 0)) {
				{
				setState(83);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BOR) | (1L << BAND) | (1L << BXOR))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				setState(84); bit();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ShiftContext extends ParserRuleContext {
		public AddContext add() {
			return getRuleContext(AddContext.class,0);
		}
		public TerminalNode SHL() { return getToken(SimplExprParser.SHL, 0); }
		public TerminalNode SHR() { return getToken(SimplExprParser.SHR, 0); }
		public TerminalNode SHA() { return getToken(SimplExprParser.SHA, 0); }
		public ShiftContext shift() {
			return getRuleContext(ShiftContext.class,0);
		}
		public ShiftContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_shift; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SimplExprVisitor ) return ((SimplExprVisitor<? extends T>)visitor).visitShift(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ShiftContext shift() throws RecognitionException {
		ShiftContext _localctx = new ShiftContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_shift);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(87); add();
			setState(90);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << SHR) | (1L << SHA) | (1L << SHL))) != 0)) {
				{
				setState(88);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << SHR) | (1L << SHA) | (1L << SHL))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				setState(89); shift();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AddContext extends ParserRuleContext {
		public AddContext add() {
			return getRuleContext(AddContext.class,0);
		}
		public MultiContext multi() {
			return getRuleContext(MultiContext.class,0);
		}
		public TerminalNode MINUS() { return getToken(SimplExprParser.MINUS, 0); }
		public TerminalNode PLUS() { return getToken(SimplExprParser.PLUS, 0); }
		public AddContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_add; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SimplExprVisitor ) return ((SimplExprVisitor<? extends T>)visitor).visitAdd(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AddContext add() throws RecognitionException {
		AddContext _localctx = new AddContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_add);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(92); multi();
			setState(95);
			_la = _input.LA(1);
			if (_la==MINUS || _la==PLUS) {
				{
				setState(93);
				_la = _input.LA(1);
				if ( !(_la==MINUS || _la==PLUS) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				setState(94); add();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MultiContext extends ParserRuleContext {
		public TerminalNode DIV() { return getToken(SimplExprParser.DIV, 0); }
		public MultiContext multi() {
			return getRuleContext(MultiContext.class,0);
		}
		public TerminalNode MULT() { return getToken(SimplExprParser.MULT, 0); }
		public UnaryContext unary() {
			return getRuleContext(UnaryContext.class,0);
		}
		public TerminalNode MOD() { return getToken(SimplExprParser.MOD, 0); }
		public MultiContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multi; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof SimplExprVisitor ) return ((SimplExprVisitor<? extends T>)visitor).visitMulti(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MultiContext multi() throws RecognitionException {
		MultiContext _localctx = new MultiContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_multi);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(97); unary();
			setState(100);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MULT) | (1L << DIV) | (1L << MOD))) != 0)) {
				{
				setState(98);
				_la = _input.LA(1);
				if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MULT) | (1L << DIV) | (1L << MOD))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				setState(99); multi();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3$i\4\2\t\2\4\3\t\3"+
		"\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t\13\4\f"+
		"\t\f\3\2\3\2\3\2\3\2\3\2\3\2\5\2\37\n\2\3\3\6\3\"\n\3\r\3\16\3#\3\3\3"+
		"\3\6\3(\n\3\r\3\16\3)\3\3\3\3\6\3.\n\3\r\3\16\3/\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\5\3=\n\3\3\4\3\4\3\4\5\4B\n\4\3\5\3\5\3\5\5\5G"+
		"\n\5\3\6\3\6\3\6\5\6L\n\6\3\7\3\7\3\7\5\7Q\n\7\3\b\3\b\3\t\3\t\3\t\5\t"+
		"X\n\t\3\n\3\n\3\n\5\n]\n\n\3\13\3\13\3\13\5\13b\n\13\3\f\3\f\3\f\5\fg"+
		"\n\f\3\f\2\2\r\2\4\6\b\n\f\16\20\22\24\26\2\t\3\2#$\3\2\22\23\4\2\20\21"+
		"\24\25\3\2\35\37\3\2\32\34\4\2\7\7\31\31\3\2\26\30o\2\30\3\2\2\2\4<\3"+
		"\2\2\2\6A\3\2\2\2\bC\3\2\2\2\nH\3\2\2\2\fM\3\2\2\2\16R\3\2\2\2\20T\3\2"+
		"\2\2\22Y\3\2\2\2\24^\3\2\2\2\26c\3\2\2\2\30\36\5\b\5\2\31\32\7\4\2\2\32"+
		"\33\5\2\2\2\33\34\7\3\2\2\34\35\5\2\2\2\35\37\3\2\2\2\36\31\3\2\2\2\36"+
		"\37\3\2\2\2\37\3\3\2\2\2 \"\7\b\2\2! \3\2\2\2\"#\3\2\2\2#!\3\2\2\2#$\3"+
		"\2\2\2$%\3\2\2\2%=\5\6\4\2&(\7\7\2\2\'&\3\2\2\2()\3\2\2\2)\'\3\2\2\2)"+
		"*\3\2\2\2*+\3\2\2\2+=\5\6\4\2,.\7\t\2\2-,\3\2\2\2./\3\2\2\2/-\3\2\2\2"+
		"/\60\3\2\2\2\60\61\3\2\2\2\61=\5\6\4\2\62\63\7 \2\2\63\64\7\16\2\2\64"+
		"\65\5\16\b\2\65\66\7\17\2\2\66=\3\2\2\2\678\7\16\2\289\5\2\2\29:\7\17"+
		"\2\2:=\3\2\2\2;=\5\6\4\2<!\3\2\2\2<\'\3\2\2\2<-\3\2\2\2<\62\3\2\2\2<\67"+
		"\3\2\2\2<;\3\2\2\2=\5\3\2\2\2>B\7\n\2\2?B\7\f\2\2@B\7\r\2\2A>\3\2\2\2"+
		"A?\3\2\2\2A@\3\2\2\2B\7\3\2\2\2CF\5\n\6\2DE\t\2\2\2EG\5\b\5\2FD\3\2\2"+
		"\2FG\3\2\2\2G\t\3\2\2\2HK\5\f\7\2IJ\t\3\2\2JL\5\n\6\2KI\3\2\2\2KL\3\2"+
		"\2\2L\13\3\2\2\2MP\5\16\b\2NO\t\4\2\2OQ\5\16\b\2PN\3\2\2\2PQ\3\2\2\2Q"+
		"\r\3\2\2\2RS\5\20\t\2S\17\3\2\2\2TW\5\22\n\2UV\t\5\2\2VX\5\20\t\2WU\3"+
		"\2\2\2WX\3\2\2\2X\21\3\2\2\2Y\\\5\24\13\2Z[\t\6\2\2[]\5\22\n\2\\Z\3\2"+
		"\2\2\\]\3\2\2\2]\23\3\2\2\2^a\5\26\f\2_`\t\7\2\2`b\5\24\13\2a_\3\2\2\2"+
		"ab\3\2\2\2b\25\3\2\2\2cf\5\4\3\2de\t\b\2\2eg\5\26\f\2fd\3\2\2\2fg\3\2"+
		"\2\2g\27\3\2\2\2\17\36#)/<AFKPW\\af";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}