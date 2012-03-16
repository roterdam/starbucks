package edu.mit.compilers.grammar;

import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.MidVisitor;
import edu.mit.compilers.grammar.expressions.OpIntInt2IntNode;

@SuppressWarnings("serial")
public class SubtractNode extends OpIntInt2IntNode {

	@Override
	public MidNodeList convertToMidLevel(MidSymbolTable symbolTable) {
		return MidVisitor.visit(this, symbolTable);
	}
}
