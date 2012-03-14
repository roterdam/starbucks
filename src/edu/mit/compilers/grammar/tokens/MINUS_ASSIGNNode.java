package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.MidVisitor;
import edu.mit.compilers.grammar.ModifyAssignNode;

@SuppressWarnings("serial")
public class MINUS_ASSIGNNode extends ModifyAssignNode {
	@Override
	public MidNodeList convertToMidLevel(MidSymbolTable symbolTable) {
		return MidVisitor.visit(this, symbolTable);
	}
}