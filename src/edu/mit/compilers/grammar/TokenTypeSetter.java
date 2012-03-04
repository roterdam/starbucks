package edu.mit.compilers.grammar;

import java.util.Hashtable;

import antlr.ASTFactory;

public class TokenTypeSetter {

	static void setTokenTypeMap(DecafParser parser) {
		ASTFactory factory = new ASTFactory(new Hashtable<Integer,Class<? extends DecafNode>>() {
			private static final long serialVersionUID = 1L;

		{
			put(DecafParserTokenTypes.AND, ANDNode.class);
			put(DecafParserTokenTypes.ASSIGN, ASSIGNNode.class);
			put(DecafParserTokenTypes.BANG, BANGNode.class);
			put(DecafParserTokenTypes.BLOCK, BLOCKNode.class);
			put(DecafParserTokenTypes.BOOLEAN_TYPE, BOOLEAN_TYPENode.class);
			put(DecafParserTokenTypes.BREAK, BREAKNode.class);
			put(DecafParserTokenTypes.CALLOUT, CALLOUTNode.class);
			put(DecafParserTokenTypes.CALLOUT_ARGS, CALLOUT_ARGSNode.class);
			put(DecafParserTokenTypes.CALLOUT_NAME, CALLOUT_NAMENode.class);
			put(DecafParserTokenTypes.CHAR_LITERAL, CHAR_LITERALNode.class);
			put(DecafParserTokenTypes.CLASS, CLASSNode.class);
			put(DecafParserTokenTypes.CONTINUE, CONTINUENode.class);
			put(DecafParserTokenTypes.DIVIDE, DIVIDENode.class);
			put(DecafParserTokenTypes.ELSE, ELSENode.class);
			put(DecafParserTokenTypes.ELSE_BLOCK, ELSE_BLOCKNode.class);
			put(DecafParserTokenTypes.EQ, EQNode.class);
			put(DecafParserTokenTypes.FALSE, FALSENode.class);
			put(DecafParserTokenTypes.FIELDS, FIELDSNode.class);
			put(DecafParserTokenTypes.FIELD_DECL, FIELD_DECLNode.class);
			put(DecafParserTokenTypes.FOR, FORNode.class);
			put(DecafParserTokenTypes.FOR_INITIALIZE, FOR_INITIALIZENode.class);
			put(DecafParserTokenTypes.FOR_TERMINATE, FOR_TERMINATENode.class);
			put(DecafParserTokenTypes.GT, GTNode.class);
			put(DecafParserTokenTypes.GTE, GTENode.class);
			put(DecafParserTokenTypes.ID, IDNode.class);
			put(DecafParserTokenTypes.IF, IFNode.class);
			put(DecafParserTokenTypes.IF_CLAUSE, IF_CLAUSENode.class);
			put(DecafParserTokenTypes.INT_ARRAY_TYPE, INT_ARRAY_TYPENode.class);
			put(DecafParserTokenTypes.INT_LITERAL, INT_LITERALNode.class);
			put(DecafParserTokenTypes.INT_TYPE, INT_TYPENode.class);
			put(DecafParserTokenTypes.LT, LTNode.class);
			put(DecafParserTokenTypes.LTE, LTENode.class);
			put(DecafParserTokenTypes.METHODS, METHODSNode.class);
			put(DecafParserTokenTypes.METHOD_CALL, METHOD_CALLNode.class);
			put(DecafParserTokenTypes.METHOD_DECL, METHOD_DECLNode.class);
			put(DecafParserTokenTypes.METHOD_RETURN, METHOD_RETURNNode.class);
			put(DecafParserTokenTypes.MINUS, MINUSNode.class);
			put(DecafParserTokenTypes.MINUS_ASSIGN, MINUS_ASSIGNNode.class);
			put(DecafParserTokenTypes.MOD, MODNode.class);
			put(DecafParserTokenTypes.NEQ, NEQNode.class);
			put(DecafParserTokenTypes.OR, ORNode.class);
			put(DecafParserTokenTypes.PARAM_DECL, PARAM_DECLNode.class);
			put(DecafParserTokenTypes.PLUS, PLUSNode.class);
			put(DecafParserTokenTypes.PLUS_ASSIGN, PLUS_ASSIGNNode.class);
			put(DecafParserTokenTypes.RETURN, RETURNNode.class);
			put(DecafParserTokenTypes.STRING_LITERAL, STRING_LITERALNode.class);
			put(DecafParserTokenTypes.TIMES, TIMESNode.class);
			put(DecafParserTokenTypes.TRUE, TRUENode.class);
			put(DecafParserTokenTypes.VAR_DECL, VAR_DECLNode.class);
			put(DecafParserTokenTypes.VOID, VOIDNode.class);
			put(DecafParserTokenTypes.WHILE, WHILENode.class);
			put(DecafParserTokenTypes.WHILE_TERMINATE, WHILE_TERMINATENode.class);
		}});
		factory.setASTNodeClass(DecafNode.class);
		parser.setASTFactory(factory);
	}

}
