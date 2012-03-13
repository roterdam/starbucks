package edu.mit.compilers.grammar.expressions;

import edu.mit.compilers.grammar.ExpressionNode;

@SuppressWarnings("serial")
public abstract class DoubleOperandNode extends ExpressionNode {

	public ExpressionNode getLeftOperand() {
		assert getChild(0) instanceof ExpressionNode;
		return (ExpressionNode) getChild(0);
	}
	
	public ExpressionNode getRightOperand() {
		assert getChild(1) instanceof ExpressionNode;
		return (ExpressionNode) getChild(1);
	}

}
