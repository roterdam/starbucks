package edu.mit.compilers.grammar.expressions;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.SemanticRules;
import edu.mit.compilers.crawler.VarType;

@SuppressWarnings("serial")
public class OpIntInt2IntNode extends DoubleOperandNode {

	@Override
	public VarType getReturnType(Scope scope) {
		return VarType.INT;
	}
	
	@Override
	public void applyRules(Scope scope) {
		SemanticRules.apply(this, scope);
	}

}
