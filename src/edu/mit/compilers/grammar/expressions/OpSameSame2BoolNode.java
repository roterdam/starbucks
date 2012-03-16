package edu.mit.compilers.grammar.expressions;

import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.SemanticRules;
import edu.mit.compilers.crawler.VarType;

@SuppressWarnings("serial")
public class OpSameSame2BoolNode extends DoubleOperandNode {

	@Override
	public VarType getReturnType(Scope scope) {
		return VarType.BOOLEAN;
	}

	@Override
	public void applyRules(Scope scope) {
		SemanticRules.apply(this, scope);
	}
	
	@Override
	public VarType getMidVarType(MidSymbolTable symbolTable){
		return VarType.BOOLEAN;
	}
}
