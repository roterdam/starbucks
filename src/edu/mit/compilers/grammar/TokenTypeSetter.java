package edu.mit.compilers.grammar;

import java.util.Hashtable;

import antlr.ASTFactory;

public class TokenTypeSetter {

	static void setTokenTypeMap(DecafParser parser) {
		ASTFactory factory = new ASTFactory(new Hashtable<Integer,Class<? extends DecafNode>>() {
			private static final long serialVersionUID = 1L;

		{
			put(DecafParserTokenTypes.FIELD_DECL, FIELD_DECLNode.class);
			put(DecafParserTokenTypes.FIELDS, FIELDSNode.class);
			put(DecafParserTokenTypes.METHODS, METHODSNode.class);
			put(DecafParserTokenTypes.METHOD_RETURN, METHOD_RETURNNode.class);
			put(DecafParserTokenTypes.METHOD_PARAMS, METHOD_PARAMSNode.class);
			put(DecafParserTokenTypes.METHOD_DECL, METHOD_DECLNode.class);
			put(DecafParserTokenTypes.BLOCK, BLOCKNode.class);
			put(DecafParserTokenTypes.VAR_DECL, VAR_DECLNode.class);
			put(DecafParserTokenTypes.METHOD_CALL, METHOD_CALLNode.class);
			put(DecafParserTokenTypes.CALLOUT_NAME, CALLOUT_NAMENode.class);
			put(DecafParserTokenTypes.CALLOUT_ARGS, CALLOUT_ARGSNode.class);
			put(DecafParserTokenTypes.IF_CLAUSE, IF_CLAUSENode.class);
			put(DecafParserTokenTypes.ELSE_BLOCK, ELSE_BLOCKNode.class);
			put(DecafParserTokenTypes.FOR_INITIALIZE, FOR_INITIALIZENode.class);
			put(DecafParserTokenTypes.FOR_TERMINATE, FOR_TERMINATENode.class);
			put(DecafParserTokenTypes.WHILE_TERMINATE, WHILE_TERMINATENode.class);
			put(DecafParserTokenTypes.BOOLEAN_TYPE, BOOLEAN_TYPENode.class);
			put(DecafParserTokenTypes.BREAK, BREAKNode.class);
			put(DecafParserTokenTypes.CALLOUT, CALLOUTNode.class);
			put(DecafParserTokenTypes.CLASS, CLASSNode.class);
			put(DecafParserTokenTypes.PROGRAM, PROGRAMNode.class);
			put(DecafParserTokenTypes.CONTINUE, CONTINUENode.class);
			put(DecafParserTokenTypes.ELSE, ELSENode.class);
			put(DecafParserTokenTypes.FOR, FORNode.class);
			put(DecafParserTokenTypes.IF, IFNode.class);
			put(DecafParserTokenTypes.INT_TYPE, INT_TYPENode.class);
			put(DecafParserTokenTypes.INT_ARRAY_TYPE, INT_ARRAY_TYPENode.class);
			put(DecafParserTokenTypes.RETURN, RETURNNode.class);
			put(DecafParserTokenTypes.VOID, VOIDNode.class);
			put(DecafParserTokenTypes.WHILE, WHILENode.class);
			put(DecafParserTokenTypes.TRUE, TRUENode.class);
			put(DecafParserTokenTypes.FALSE, FALSENode.class);
			put(DecafParserTokenTypes.MINUS_ASSIGN, MINUS_ASSIGNNode.class);
			put(DecafParserTokenTypes.PLUS_ASSIGN, PLUS_ASSIGNNode.class);

		}});
		factory.setASTNodeClass(DecafNode.class);
		parser.setASTFactory(factory);
	}

}
