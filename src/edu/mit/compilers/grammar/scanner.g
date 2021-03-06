header {
package edu.mit.compilers.grammar;
}

options {
  language = "Java";
}

{@SuppressWarnings("unchecked")}
class DecafScanner extends Lexer;
options {
  k = 2;
}

tokens {
  // Imaginary tokens for tree nodes.
  FIELD_DECL;
  FIELDS;
  METHODS;
  METHOD_RETURN;
  PARAM_DECL;
  METHOD_DECL;
  BLOCK;
  VAR_DECL;
  METHOD_CALL;
  METHOD_ID;
  CALLOUT_NAME;
  CALLOUT_ARGS;
  IF_CLAUSE;
  ELSE_BLOCK;
  FOR_INITIALIZE;
  FOR_TERMINATE;
  WHILE_TERMINATE;
  
  BOOLEAN_TYPE="boolean";
  BREAK="break";
  CALLOUT="callout";
  CLASS="class";
  CONTINUE="continue";
  ELSE="else";
  FOR="for";
  IF="if";
  INT_TYPE="int";
  RETURN="return";
  VOID="void";
  WHILE="while";
  TRUE="true";
  FALSE="false";
  // Used by rules below.
  MINUS_ASSIGN;
  PLUS_ASSIGN;
}


// Selectively turns on debug tracing mode.
// You can insert arbitrary Java code into your parser/lexer this way.
{
  /** Whether to display debug information. */
  private boolean trace = false;

  public void setTrace(boolean shouldTrace) {
    trace = shouldTrace;
  } 
  @Override
  public void traceIn(String rname) throws CharStreamException {
    if (trace) {
      super.traceIn(rname);
    }
  }
  @Override
  public void traceOut(String rname) throws CharStreamException {
    if (trace) {
      super.traceOut(rname);
    }
  }
}

LCURLY
  : "{"
  ;
RCURLY
  : "}"
  ;

COMMA : ",";
SEMICOLON : ";";
LBRACKET : "[";
RBRACKET : "]";
LPAREN : "(";
RPAREN : ")";

// Note that here, the {} syntax allows you to literally command the lexer
// to skip mark this token as skipped, or to advance to the next line
// by directly adding Java commands.
WS_
  : (' ' | '\t' | '\n' {newline();}) {_ttype = Token.SKIP; }
  ;
SL_COMMENT
  : "//" (~'\n')* '\n' {_ttype = Token.SKIP; newline (); }
  ;

protected
CHAR_EL
  : ESC
  | ' '
  | '!'
  // Skip "
  | '#'..'&'
  // Skip '
  | '('..'['
  // Skip backslash
  | ']'..'~'
  ;
CHAR_LITERAL
  options { paraphrase = "a char"; }
  : "'" CHAR_EL "'"
  ;
STRING_LITERAL
  options { paraphrase = "a string"; }
  : '"' (CHAR_EL)* '"'
  ;
INT_LITERAL
  options { paraphrase = "an int"; }
  : DECIMAL
  | HEX
  | BIN
  ;

protected
DECIMAL
  : DIGIT (DIGIT)*
  ;
protected
HEX
  : "0x" HEX_DIGIT (HEX_DIGIT)*
  ;
protected
BIN
  : "0b" ('0' | '1') ('0' | '1')*
  ;
protected
DIGIT
  : '0'..'9'
  ;
protected
HEX_DIGIT
  : DIGIT | ('a'..'f') | ('A'..'F')
  ;

ASSIGN : "=";
BANG : "!";

MINUS : "-";
MINUS_ASSIGN : "-=";
PLUS : "+";
PLUS_ASSIGN : "+=";
TIMES : "*";
DIVIDE : "/";
MOD : "%";
GT : ">";
LT : "<";
GTE : ">=";
LTE : "<=";
EQ : "==";
NEQ : "!=";
AND : "&&";
OR : "||";

protected
ESC
  : '\\' ('n'|'"'|'t'|'\\'|'\'')
  ;

ID
  options { paraphrase = "an identifier"; }
  : ALPHA (ALPHA_NUM)*
  ;

protected
ALPHA
  : 'a'..'z'
  | 'A'..'Z'
  | '_'
  ;
protected
ALPHA_NUM
  : ALPHA
  | DIGIT
  ;
