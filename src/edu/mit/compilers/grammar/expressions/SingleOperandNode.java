package edu.mit.compilers.grammar.expressions;

import edu.mit.compilers.grammar.ExpressionNode;

@SuppressWarnings("serial")
public abstract class SingleOperandNode extends ExpressionNode {
	
	public ExpressionNode getOperand() {
		assert getChild(0) instanceof ExpressionNode;
		return (ExpressionNode) getChild(0);
	}
	
	public void setOperand(ExpressionNode x) {
		replaceChild(0, x);
	}
	
	@Override
	public boolean hasMethodCalls() {
		return getOperand().hasMethodCalls();
	}

}
