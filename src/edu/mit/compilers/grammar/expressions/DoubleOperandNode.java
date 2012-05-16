package edu.mit.compilers.grammar.expressions;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.ErrorCenter;
import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.grammar.ExpressionNode;

@SuppressWarnings("serial")
public abstract class DoubleOperandNode extends ExpressionNode {

	public ExpressionNode getLeftOperand() {
		assert getChild(0) instanceof ExpressionNode : toString() + " has left op = "+getChild(0);
		return (ExpressionNode) getChild(0);
	}

	public ExpressionNode getRightOperand() {
		if(getChild(1) == null){
			ErrorCenter.reportError(this.getLine(), this.getColumn(), "Null child");
			return null;
		}
		assert getChild(1) instanceof ExpressionNode : "I am a "+this.getClass()+" and I have a problem. My right node is "+getChild(1);
		
		return (ExpressionNode) getChild(1);
	}

	public void setLeftOperand(ExpressionNode x) {
		replaceChild(0, x);
	}

	public void setRightOperand(ExpressionNode x) {
		assert x != null;
		replaceChild(1, x);
	}

	@Override
	public boolean hasMethodCalls() {
		return getLeftOperand().hasMethodCalls()
				|| getRightOperand().hasMethodCalls();
	}

	@Override
	public List<DecafNode> getCallsDuringExecution() {
		List<DecafNode> list = new ArrayList<DecafNode>();
		list.addAll(getLeftOperand().getAllCallsDuringExecution());
		list.addAll(getRightOperand().getAllCallsDuringExecution());
		return list;
	}

}
