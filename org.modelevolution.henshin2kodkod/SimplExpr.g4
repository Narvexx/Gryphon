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

grammar SimplExpr;

// init : (stmt WS)* ;
         
expr : logic ('?' expr ':' expr)? // result: Formula
     ;
     
unary : BNOT+atom # BitwiseNot // result: IntExpression, undef: bool, type: int
      | MINUS+atom # NegativeInt// result: IntExpression, undef: bool, type: int
      | LNOT+atom # LogicalNot // result: IntExpression, undef: int, type: bool
      | FUNC '(' arith ')' # FuncExpr // result: IntExpression, undef: bool, type: int
      | '(' expr ')' # ParenExpr// result: ??
      | atom # AtomExpr
      ;
          
atom : INT  # Int // result: IntExpression, undef: -, type: int
     | BOOL # Bool // result: IntExpression, undef: -, type: bool
     | ID   # Id // result: IntExpression?, undef: -, type bool/int/null 
     ;

logic : eq  ((LAND | LOR) logic)?; // result: FORMULA, undef: int, type: bool
eq : rel ((EQ | NEQ) eq)?;  // result: FORMULA, undef: -, type: bool
rel : arith ((GT | LT | GTE | LTE) arith)? ;  // result: FORMULA, undef: Bool

arith : bit ;
bit : shift ((BAND | BXOR | BOR) bit)? ; // result: IntExpression, undef: -, type: bool OR int
shift : add ((SHL | SHA | SHR) shift)? ; // result: IntExpression, undef: Bool, type: int
add: multi (( PLUS | MINUS ) add)? ; // result: IntExpression, undef: Bool, type: int
multi : unary (( MULT | DIV | MOD ) multi)? ; // result: IntExpression, undef: Bool, type: int
       
//binaryArithExpr : (MULT arithExpr)
                //| (( MULT | DIV | MOD ) arithExpr)*
                //| (( PLUS | MINUS ) arithExpr)
                //| (( SHL | SHA | SHR ) arithExpr)
                //| (( B_AND | B_XOR | B_OR ) arithExpr)
                //;
          
//relExpr : ( LT | GT | LTE | GTE ) expr ;
//          
//eqExpr : ( EQ | NEQ ) expr ;
//
//bitExpr : ( B_AND | B_XOR | B_OR ) expr ;
//
//logicExpr : ( L_AND | L_OR ) expr ;
//
//ternary : '?' expr ':' expr ;

WS : [ \t]+ -> skip ;
NL : ('\r'? '\n')+ -> skip;
MINUS : '-' ;
BNOT : '~' ;
LNOT : '!' ;

INT : (ZERO|[1-9][0-9]*) ;
ZERO : '0' ;

BOOL : 'true'
     | 'false'
     ;

ID: [a-zA-Z][a-zA-Z0-9]* ;

LPAR : '(' ;
RPAR : ')' ;

GTE: '>=' ;
LTE: '<=' ;
EQ : '==' ;
NEQ : '!=';
GT : '>' ;
LT : '<' ;

MULT : '*' ;
DIV : '/';
MOD : '%' ;
PLUS : '+' ;
//MINUS : '-' ;
SHR : '>>>' ;
SHA : '>>' ;
SHL : '<<' ;
BOR : '|' ;
BAND : '&' ;
BXOR : '^' ;


FUNC : ABS
     | SGN
     ;

ABS : 'Math.abs' ;
SGN : 'Math.signum' ;

LAND : '&&' ;
LOR : '||' ;
