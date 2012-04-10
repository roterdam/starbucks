package edu.mit.compilers.grammar;

import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.VarType;
import edu.mit.compilers.opt.algebra.AlgebraicSimplifier;

@SuppressWarnings("serial")
public abstract class BooleanNode extends ExpressionNode {

	@Override
	public VarType getReturnType(Scope scope) {
		return VarType.BOOLEAN;
	}
	@Override
	public VarType getMidVarType(MidSymbolTable symbolTable){
		return VarType.BOOLEAN;
	}
	
	@Override
	public ExpressionNode simplify(MidSymbolTable symbolTable) {
		return AlgebraicSimplifier.simplifyExpression(this, symbolTable);
	}
	
	@Override
	public boolean hasMethodCalls() {
		return false;
	}
}
