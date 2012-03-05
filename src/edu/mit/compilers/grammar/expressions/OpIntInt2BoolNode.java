package edu.mit.compilers.grammar.expressions;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.SemanticRules;
import edu.mit.compilers.crawler.VarType;
import edu.mit.compilers.grammar.ExpressionNode;

@SuppressWarnings("serial")
public class OpIntInt2BoolNode extends ExpressionNode {

	@Override
	public VarType getReturnType(Scope scope) {
		return VarType.BOOLEAN;
	}
	
	@Override
	public void applyRules(Scope scope) {
		SemanticRules.apply(this, scope);
	}

}
