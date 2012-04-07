package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.MidShortCircuitVisitor;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.nodes.MidLabelNode;
import edu.mit.compilers.grammar.ExpressionNode;
import edu.mit.compilers.grammar.expressions.OpBool2BoolNode;
import edu.mit.compilers.opt.AlgebraicSimplifier;

@SuppressWarnings("serial")
public class BANGNode extends OpBool2BoolNode {

	@Override
	public MidNodeList shortCircuit(MidSymbolTable symbolTable, MidLabelNode trueLabel, MidLabelNode falseLabel){
		return MidShortCircuitVisitor.shortCircuit(this, symbolTable, trueLabel, falseLabel);
	}
	
	@Override
	public ExpressionNode simplify() {
		return AlgebraicSimplifier.simplifyExpression(this);
	}
}