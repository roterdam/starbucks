header {
package edu.mit.compilers.grammar;
}

options
{
  language = "Java";
}

class DecafParser extends Parser;
options
{
  importVocab = DecafScanner;
  // k=3 is required to fix ambiguities
  k = 3;
  buildAST = true;
//  ASTLabelType = "DecafAST";
}

// Java glue code that makes error reporting easier.
// You can insert arbitrary Java code into your parser/lexer this way.
{
  // Do our own reporting of errors so the parser can return a non-zero status
  // if any errors are detected.
  /** Reports if any errors were reported during parse. */
  private boolean error;

  @Override
  public void reportError (RecognitionException ex) {
    // Print the error via some kind of error reporting mechanism.
    error = true;
    try {
      traceIn("RecognitionException at [" + ex.getLine() + "," + ex.getLine() + "]: " + ex.toString());
      ex.printStackTrace();
    } catch (TokenStreamException e) {
      e.printStackTrace();
    }
  }
  @Override
  public void reportError (String s) {
    // Print the error via some kind of error reporting mechanism.
    error = true;
  }
  
  // Returns true if at least one error has occurred.
  public boolean hasError () {
    return error;
  }

  // Selectively turns on debug mode.

  /** Whether to display debug information. */
  private boolean trace = false;

  public void setTrace(boolean shouldTrace) {
    trace = shouldTrace;
  }
  @Override
  public void traceIn(String rname) throws TokenStreamException {
    if (trace) {
      super.traceIn(rname);
    }
  }
  @Override
  public void traceOut(String rname) throws TokenStreamException {
    if (trace) {
      super.traceOut(rname);
    }
  }
}

program!
	:
	{
	  AST fields = #([FIELDS, "fields"]);
	  AST methods = #([METHODS, "methods"]);
	}
	CLASS PROGRAM! LCURLY!
  	(f:field_decl { fields.addChild(#f); })*
  	(m:method_decl { methods.addChild(#m); })*
	RCURLY! EOF!
	{
	  #program = #(CLASS, fields, methods);
	}
	;

field_decl
	: (INT_TYPE^ | BOOLEAN_TYPE^) field_decl_id (COMMA! field_decl_id)* SEMICOLON!
	;

field_decl_id
  : ID^ (LBRACKET! INT_LITERAL RBRACKET!)?
  ;

method_decl!
	: (i:INT_TYPE | b:BOOLEAN_TYPE | v:VOID) ID LPAREN! (p:method_decl_params)? RPAREN! bl:block
	{ #method_decl = #(ID,
	    #([METHOD_RETURN, "return"], i, b, v),
	    p,
	    #([METHOD_BLOCK, "block"], bl)
	  );}
	;

method_decl_params
  : method_decl_param (COMMA! method_decl_param)*
  { #method_decl_params = #([METHOD_PARAMS, "params"], #method_decl_params); }
  ;

method_decl_param
  : (INT_TYPE^ | BOOLEAN_TYPE^) ID
  ;

block
	: LCURLY! (var_decl)* (statement)* RCURLY!
	;

var_decl
	: (INT_TYPE^ | BOOLEAN_TYPE^) ID (COMMA! ID)* SEMICOLON!
	;

statement
	: location (ASSIGN^ | MINUS_ASSIGN^ | PLUS_ASSIGN^) expr SEMICOLON!
	| method_call SEMICOLON!
	| IF LPAREN! expr RPAREN! block (ELSE block)?
	| FOR LPAREN! ID ASSIGN expr SEMICOLON! expr RPAREN! block
	| WHILE LPAREN! expr RPAREN! block
	| RETURN (expr)? SEMICOLON!
	| BREAK SEMICOLON!
	| CONTINUE SEMICOLON!
	| block
	;

method_call
	: ID LPAREN! (expr (COMMA! expr)*)? RPAREN!
	| CALLOUT LPAREN! STRING_LITERAL (COMMA! callout_arg)* RPAREN!
	;

location
	: ID (LBRACKET! expr RBRACKET!)?
	;

expr
  : and_expr (OR^ expr)?
  ;

and_expr
  : eq_expr (AND^ and_expr)?
  ;

eq_expr
  : rel_expr ((EQ^ | NEQ^) eq_expr)?
  ;

rel_expr
  : arith_expr_2 ((GT^ | LT^ | GTE^ | LTE^) arith_expr_2)?
  ;

arith_expr_2
  : arith_expr_1 ((PLUS^ | MINUS^) arith_expr_2)?
  ;

arith_expr_1
  : not_expr ((TIMES^ | DIVIDE^ | MOD^) arith_expr_1)?
  ;

not_expr
  : BANG^ not_expr
  | unary_minus_expr
  ;

unary_minus_expr
  : MINUS^ unary_minus_expr
  | sub_expr
  ;

sub_expr
	: location
	| method_call
	| literal
  | LPAREN! expr RPAREN!
	;

callout_arg
	: expr
	| STRING_LITERAL
	;

bin_op
  : MINUS | PLUS | TIMES | DIVIDES | MOD
  | GT | LT | GTE | LTE
	| EQ | NEQ
  | AND | OR
	;

literal
	: INT_LITERAL
  | CHAR_LITERAL
	| TRUE
	| FALSE
	;
