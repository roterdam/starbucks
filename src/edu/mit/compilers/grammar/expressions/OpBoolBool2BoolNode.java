package edu.mit.compilers.grammar.expressions;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.VarType;
import edu.mit.compilers.grammar.ExpressionNode;

@SuppressWarnings("serial")
public class OpBoolBool2BoolNode extends ExpressionNode {

	@Override
	public VarType getReturnType(Scope scope) {
		return VarType.BOOLEAN;
	}

}
