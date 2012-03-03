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
  FIELD_DECL<AST=FIELD_DECLNode>;
  FIELDS<AST=FIELDSNode>;
  METHODS<AST=METHODSNode>;
  METHOD_RETURN<AST=METHOD_RETURNNode>;
  PARAM_DECL<AST=PARAM_DECLNode>;
  METHOD_PARAMS<AST=METHOD_PARAMSNode>;
  METHOD_DECL<AST=METHODS_DECLNods>;
  BLOCK<AST=BLOCKNode>;
  VAR_DECL<AST=VAR_DECLNode>;
  METHOD_CALL<AST=METHOD_CALLNode>;
  CALLOUT_NAME<AST=CALLOUT_NAMENode>;
  CALLOUT_ARGS<AST=CALLOUT_ARGSNode>;
  IF_CLAUSE<AST=IF_CLAUSENode>;
  ELSE_BLOCK<AST=ELSE_BLOCKNode>;
  FOR_INITIALIZE;
  FOR_TERMINATE;
  WHILE_TERMINATE;
  
  BOOLEAN_TYPE="boolean";
  BREAK="break";
  CALLOUT="callout";
  CLASS="class";
  PROGRAM="Program";
  CONTINUE="continue";
  ELSE="else";
  FOR="for";
  IF="if";
  INT_TYPE="int";
  INT_ARRAY_TYPE;
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
OR : "||" <AST=ORNode>;

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
