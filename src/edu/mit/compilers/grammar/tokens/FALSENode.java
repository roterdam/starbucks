package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.MidVisitor;
import edu.mit.compilers.grammar.BooleanNode;

@SuppressWarnings("serial")
public class FALSENode extends BooleanNode {

	@Override
	public MidNodeList convertToMidLevel(MidSymbolTable symbolTable) {
		return MidVisitor.visit(this, symbolTable);
	}
	
}