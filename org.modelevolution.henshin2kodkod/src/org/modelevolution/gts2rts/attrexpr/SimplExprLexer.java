// Generated from SimplExpr.g4 by ANTLR 4.4
package org.modelevolution.gts2rts.attrexpr;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SimplExprLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__1=1, T__0=2, WS=3, NL=4, MINUS=5, BNOT=6, LNOT=7, INT=8, ZERO=9, BOOL=10, 
		ID=11, LPAR=12, RPAR=13, GTE=14, LTE=15, EQ=16, NEQ=17, GT=18, LT=19, 
		MULT=20, DIV=21, MOD=22, PLUS=23, SHR=24, SHA=25, SHL=26, BOR=27, BAND=28, 
		BXOR=29, FUNC=30, ABS=31, SGN=32, LAND=33, LOR=34;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"'\\u0000'", "'\\u0001'", "'\\u0002'", "'\\u0003'", "'\\u0004'", "'\\u0005'", 
		"'\\u0006'", "'\\u0007'", "'\b'", "'\t'", "'\n'", "'\\u000B'", "'\f'", 
		"'\r'", "'\\u000E'", "'\\u000F'", "'\\u0010'", "'\\u0011'", "'\\u0012'", 
		"'\\u0013'", "'\\u0014'", "'\\u0015'", "'\\u0016'", "'\\u0017'", "'\\u0018'", 
		"'\\u0019'", "'\\u001A'", "'\\u001B'", "'\\u001C'", "'\\u001D'", "'\\u001E'", 
		"'\\u001F'", "' '", "'!'", "'\"'"
	};
	public static final String[] ruleNames = {
		"T__1", "T__0", "WS", "NL", "MINUS", "BNOT", "LNOT", "INT", "ZERO", "BOOL", 
		"ID", "LPAR", "RPAR", "GTE", "LTE", "EQ", "NEQ", "GT", "LT", "MULT", "DIV", 
		"MOD", "PLUS", "SHR", "SHA", "SHL", "BOR", "BAND", "BXOR", "FUNC", "ABS", 
		"SGN", "LAND", "LOR"
	};


	public SimplExprLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "SimplExpr.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2$\u00cb\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\3\2\3\2\3\3\3\3\3\4\6\4M\n\4\r\4\16\4N\3\4\3\4\3\5\5"+
		"\5T\n\5\3\5\6\5W\n\5\r\5\16\5X\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t"+
		"\3\t\7\tf\n\t\f\t\16\ti\13\t\5\tk\n\t\3\n\3\n\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\5\13x\n\13\3\f\3\f\7\f|\n\f\f\f\16\f\177\13\f\3\r"+
		"\3\r\3\16\3\16\3\17\3\17\3\17\3\20\3\20\3\20\3\21\3\21\3\21\3\22\3\22"+
		"\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3\31"+
		"\3\31\3\31\3\31\3\32\3\32\3\32\3\33\3\33\3\33\3\34\3\34\3\35\3\35\3\36"+
		"\3\36\3\37\3\37\5\37\u00af\n\37\3 \3 \3 \3 \3 \3 \3 \3 \3 \3!\3!\3!\3"+
		"!\3!\3!\3!\3!\3!\3!\3!\3!\3\"\3\"\3\"\3#\3#\3#\2\2$\3\3\5\4\7\5\t\6\13"+
		"\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'"+
		"\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$\3\2\7\4"+
		"\2\13\13\"\"\3\2\63;\3\2\62;\4\2C\\c|\5\2\62;C\\c|\u00d2\2\3\3\2\2\2\2"+
		"\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2"+
		"\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2"+
		"\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2"+
		"\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2"+
		"\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2"+
		"\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\3G\3\2\2\2\5I\3\2\2\2\7"+
		"L\3\2\2\2\tV\3\2\2\2\13\\\3\2\2\2\r^\3\2\2\2\17`\3\2\2\2\21j\3\2\2\2\23"+
		"l\3\2\2\2\25w\3\2\2\2\27y\3\2\2\2\31\u0080\3\2\2\2\33\u0082\3\2\2\2\35"+
		"\u0084\3\2\2\2\37\u0087\3\2\2\2!\u008a\3\2\2\2#\u008d\3\2\2\2%\u0090\3"+
		"\2\2\2\'\u0092\3\2\2\2)\u0094\3\2\2\2+\u0096\3\2\2\2-\u0098\3\2\2\2/\u009a"+
		"\3\2\2\2\61\u009c\3\2\2\2\63\u00a0\3\2\2\2\65\u00a3\3\2\2\2\67\u00a6\3"+
		"\2\2\29\u00a8\3\2\2\2;\u00aa\3\2\2\2=\u00ae\3\2\2\2?\u00b0\3\2\2\2A\u00b9"+
		"\3\2\2\2C\u00c5\3\2\2\2E\u00c8\3\2\2\2GH\7<\2\2H\4\3\2\2\2IJ\7A\2\2J\6"+
		"\3\2\2\2KM\t\2\2\2LK\3\2\2\2MN\3\2\2\2NL\3\2\2\2NO\3\2\2\2OP\3\2\2\2P"+
		"Q\b\4\2\2Q\b\3\2\2\2RT\7\17\2\2SR\3\2\2\2ST\3\2\2\2TU\3\2\2\2UW\7\f\2"+
		"\2VS\3\2\2\2WX\3\2\2\2XV\3\2\2\2XY\3\2\2\2YZ\3\2\2\2Z[\b\5\2\2[\n\3\2"+
		"\2\2\\]\7/\2\2]\f\3\2\2\2^_\7\u0080\2\2_\16\3\2\2\2`a\7#\2\2a\20\3\2\2"+
		"\2bk\5\23\n\2cg\t\3\2\2df\t\4\2\2ed\3\2\2\2fi\3\2\2\2ge\3\2\2\2gh\3\2"+
		"\2\2hk\3\2\2\2ig\3\2\2\2jb\3\2\2\2jc\3\2\2\2k\22\3\2\2\2lm\7\62\2\2m\24"+
		"\3\2\2\2no\7v\2\2op\7t\2\2pq\7w\2\2qx\7g\2\2rs\7h\2\2st\7c\2\2tu\7n\2"+
		"\2uv\7u\2\2vx\7g\2\2wn\3\2\2\2wr\3\2\2\2x\26\3\2\2\2y}\t\5\2\2z|\t\6\2"+
		"\2{z\3\2\2\2|\177\3\2\2\2}{\3\2\2\2}~\3\2\2\2~\30\3\2\2\2\177}\3\2\2\2"+
		"\u0080\u0081\7*\2\2\u0081\32\3\2\2\2\u0082\u0083\7+\2\2\u0083\34\3\2\2"+
		"\2\u0084\u0085\7@\2\2\u0085\u0086\7?\2\2\u0086\36\3\2\2\2\u0087\u0088"+
		"\7>\2\2\u0088\u0089\7?\2\2\u0089 \3\2\2\2\u008a\u008b\7?\2\2\u008b\u008c"+
		"\7?\2\2\u008c\"\3\2\2\2\u008d\u008e\7#\2\2\u008e\u008f\7?\2\2\u008f$\3"+
		"\2\2\2\u0090\u0091\7@\2\2\u0091&\3\2\2\2\u0092\u0093\7>\2\2\u0093(\3\2"+
		"\2\2\u0094\u0095\7,\2\2\u0095*\3\2\2\2\u0096\u0097\7\61\2\2\u0097,\3\2"+
		"\2\2\u0098\u0099\7\'\2\2\u0099.\3\2\2\2\u009a\u009b\7-\2\2\u009b\60\3"+
		"\2\2\2\u009c\u009d\7@\2\2\u009d\u009e\7@\2\2\u009e\u009f\7@\2\2\u009f"+
		"\62\3\2\2\2\u00a0\u00a1\7@\2\2\u00a1\u00a2\7@\2\2\u00a2\64\3\2\2\2\u00a3"+
		"\u00a4\7>\2\2\u00a4\u00a5\7>\2\2\u00a5\66\3\2\2\2\u00a6\u00a7\7~\2\2\u00a7"+
		"8\3\2\2\2\u00a8\u00a9\7(\2\2\u00a9:\3\2\2\2\u00aa\u00ab\7`\2\2\u00ab<"+
		"\3\2\2\2\u00ac\u00af\5? \2\u00ad\u00af\5A!\2\u00ae\u00ac\3\2\2\2\u00ae"+
		"\u00ad\3\2\2\2\u00af>\3\2\2\2\u00b0\u00b1\7O\2\2\u00b1\u00b2\7c\2\2\u00b2"+
		"\u00b3\7v\2\2\u00b3\u00b4\7j\2\2\u00b4\u00b5\7\60\2\2\u00b5\u00b6\7c\2"+
		"\2\u00b6\u00b7\7d\2\2\u00b7\u00b8\7u\2\2\u00b8@\3\2\2\2\u00b9\u00ba\7"+
		"O\2\2\u00ba\u00bb\7c\2\2\u00bb\u00bc\7v\2\2\u00bc\u00bd\7j\2\2\u00bd\u00be"+
		"\7\60\2\2\u00be\u00bf\7u\2\2\u00bf\u00c0\7k\2\2\u00c0\u00c1\7i\2\2\u00c1"+
		"\u00c2\7p\2\2\u00c2\u00c3\7w\2\2\u00c3\u00c4\7o\2\2\u00c4B\3\2\2\2\u00c5"+
		"\u00c6\7(\2\2\u00c6\u00c7\7(\2\2\u00c7D\3\2\2\2\u00c8\u00c9\7~\2\2\u00c9"+
		"\u00ca\7~\2\2\u00caF\3\2\2\2\13\2NSXgjw}\u00ae\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}