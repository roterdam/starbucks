header {
  package edu.mit.compilers.grammar;
}

options
{
  language = "Java";
}

class DecafTreeParser extends TreeParser;

/*
program
  : #(CLASS field_decl)
  ;
  
  
  
program
	: CLASS PROGRAM! LCURLY! (field_decl)* (method_decl)* RCURLY! EOF!
	;

field_decl
	: type field_decl_id (COMMA! field_decl_id)* SEMICOLON!
	;
field_decl
	: type field_decl_id (COMMA! field_decl_id)* SEMICOLON!
	;

field_decl_id
  : ID (LBRACKET! INT_LITERAL RBRACKET!)?
  ;

method_decl
	: (type | VOID) ID LPAREN! (method_decl_params)? RPAREN! block
	;

method_decl_params
  : type ID (COMMA! type ID)*
  ;

block
	: LCURLY! (var_decl)* (statement)* RCURLY!
	;

var_decl
	: type ID (COMMA! ID)* SEMICOLON!
	;

type
	: INT_TYPE
	| BOOLEAN_TYPE
	;

statement
	: location (ASSIGN | MINUS_ASSIGN | PLUS_ASSIGN) expr SEMICOLON!
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
  : and_expr (OR expr)?
  ;

and_expr
  : eq_expr (AND and_expr)?
  ;

eq_expr
  : rel_expr ((EQ | NEQ) eq_expr)?
  ;

rel_expr
  : arith_expr_2 ((GT | LT | GTE | LTE) arith_expr_2)?
  ;

arith_expr_2
  : arith_expr_1 ((PLUS | MINUS) arith_expr_2)?
  ;

arith_expr_1
  : not_expr ((TIMES | DIVIDE | MOD) arith_expr_1)?
  ;

not_expr
  : BANG not_expr
  | unary_minus_expr
  ;

unary_minus_expr
  : MINUS unary_minus_expr
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
*/