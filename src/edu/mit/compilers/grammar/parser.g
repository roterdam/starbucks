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
  ASTLabelType = "DecafNode";
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
	: {
	  TokenTypeSetter.setTokenTypeMap(this);
      DecafNode fields = #([FIELDS, "fields"]);
      DecafNode methods = #([METHODS, "methods"]);
    }
    CLASS PROGRAM! LCURLY!
  	(f:field_decl { fields.addChild(#f); })*
  	(m:method_decl { methods.addChild(#m); })*
    RCURLY! EOF!
    {
      #program = #(CLASS, fields, methods);
    }
	;

field_decl !
    : t:type id1:field_decl_id 
    {
        DecafNode id1_array = id1_AST.getNextSibling();
        id1_AST.setNextSibling(null);
        DecafNode field = #([FIELD_DECL, "field decl"], #(t, id1_array), id1); 
        DecafNode next = field;  
    } 
    (COMMA! id2:field_decl_id 
    {
        next.setNextSibling(#([FIELD_DECL, "field decl"], astFactory.create(t), id2)); 
        next = next.getNextSibling(); 
    }
    )* SEMICOLON!
	{
	   #field_decl = field;
	}
	;

type: INT_TYPE | BOOLEAN_TYPE;

field_decl_id
  : ID (LBRACKET! INT_LITERAL RBRACKET!)?
  ;

method_decl!
	: (i:INT_TYPE | b:BOOLEAN_TYPE | v:VOID) ID
    LPAREN! (p:method_decl_params)? RPAREN! bl:block
    {
      DecafNode replace = #bl;
      if (#p != null) {
        DecafNode first = replace.getFirstChild();
        replace.setFirstChild(#p);
        #p.setNextSibling(first);
      }
      #method_decl = #(ID,
        #([METHOD_RETURN, "return"], i, b, v), replace
      );
    }
	;

method_decl_params
  : method_decl_param (COMMA! method_decl_param)*
  ;

method_decl_param !
  : t:type i:ID
  { #method_decl_param = #([PARAM_DECL, "param decl"], t, i); }
  ;

block
	: LCURLY! (var_decl)* (statement)* RCURLY!
    { #block = #([BLOCK, "block"], #block); }
	;

var_decl !
	: t:type id1:ID 
	{
	   DecafNode decl = #([VAR_DECL, "var decl"], t, id1);
	   DecafNode next = decl;  
	} 
	(COMMA! id2:ID 
	{
	   next.setNextSibling(#([VAR_DECL, "var decl"], astFactory.create(t), id2)); 
	   next = next.getNextSibling(); 
	}
	)* SEMICOLON!
	{
       #var_decl = decl;
    }
	;

statement
	: location (ASSIGN^ | MINUS_ASSIGN^ | PLUS_ASSIGN^) expr SEMICOLON!
	| method_call SEMICOLON!
	|! IF LPAREN! e:expr RPAREN! if_block:block
    {
      #statement = #(IF,
        #([IF_CLAUSE, "clause"], e), if_block
      );
    }
    (ELSE else:block { #statement.addChild(#([ELSE_BLOCK, "else"], else)); })?
	|! FOR LPAREN! ID ASSIGN init:expr SEMICOLON! for_term:expr RPAREN! for_block:block
    {
      // Nest ASSIGN, ID, init into a statement
      #statement = #(FOR,
        #([FOR_INITIALIZE, "init"], #(ASSIGN, ID, init)),
        #([FOR_TERMINATE, "term"], for_term), for_block
      );
    }
	|! WHILE LPAREN! while_term:expr RPAREN! while_block:block
    {
      #statement = #(WHILE, #([WHILE_TERMINATE, "term"], while_term), while_block);
    }
	| RETURN^ (expr)? SEMICOLON!
	| BREAK SEMICOLON!
	| CONTINUE SEMICOLON!
	| block
	;

method_call
	: ID^ LPAREN! (expr (COMMA! expr)*)? RPAREN!
	{ #method_call = #([METHOD_CALL, "method call"], method_call); }
	|! CALLOUT LPAREN! name:STRING_LITERAL
    {
      DecafNode cargs = #([CALLOUT_ARGS, "args"]);
      #method_call = #(CALLOUT,
        #([CALLOUT_NAME, "name"], name), cargs
      );
    }
    (COMMA! arg:callout_arg { cargs.addChild(#arg); })*
    RPAREN!
	;

location
	: ID^ (LBRACKET! expr RBRACKET!)?
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
