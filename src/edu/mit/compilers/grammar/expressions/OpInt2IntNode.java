package edu.mit.compilers.grammar.expressions;

import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.SemanticRules;
import edu.mit.compilers.crawler.VarType;

@SuppressWarnings("serial")
public abstract class OpInt2IntNode extends SingleOperandNode {

	@Override
	public VarType getReturnType(Scope scope) {
		return VarType.INT;
	}
	
	@Override
	public void applyRules(Scope scope) {
		SemanticRules.apply(this, scope);
	}
	
	@Override
	public VarType getMidVarType(MidSymbolTable symbolTable){
		return VarType.INT;
	}
	
}