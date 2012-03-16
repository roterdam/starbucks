package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.MidVisitor;
import edu.mit.compilers.codegen.nodes.MidLabelNode;
import edu.mit.compilers.grammar.expressions.OpSameSame2BoolNode;

@SuppressWarnings("serial")
public class NEQNode extends OpSameSame2BoolNode {
	
	@Override
	public MidNodeList shortCircuit(MidSymbolTable symbolTable, MidLabelNode trueLabel, MidLabelNode falseLabel){
		return MidVisitor.shortCircuit(this, symbolTable, trueLabel, falseLabel);
	}
}