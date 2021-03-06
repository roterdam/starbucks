package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.MidVisitor;
import edu.mit.compilers.grammar.DeclNode;

@SuppressWarnings("serial")
public class PARAM_DECLNode extends DeclNode {

	@Override
	public MidNodeList convertToMidLevel(MidSymbolTable symbolTable) {
		assert false : "Don't call this, need to pass in paramOffset.";
		return null;
	}
	
	public MidNodeList convertToMidLevel(MidSymbolTable symbolTable, int paramOffset) {
		return MidVisitor.visitParam(this, symbolTable, paramOffset);
	}

}
