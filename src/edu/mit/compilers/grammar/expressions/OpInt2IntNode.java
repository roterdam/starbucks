package edu.mit.compilers.grammar.expressions;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.VarType;
import edu.mit.compilers.grammar.ExpressionNode;

@SuppressWarnings("serial")
public class OpInt2IntNode extends ExpressionNode {

	@Override
	public VarType getReturnType(Scope scope) {
		return VarType.INT;
	}

}