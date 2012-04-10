package edu.mit.compilers.codegen;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.grammar.ExpressionNode;

@SuppressWarnings("serial")
public class CheckDivideByZeroNode extends DecafNode {
	// TODO IMPLEMENT
	private ExpressionNode expr;
	public CheckDivideByZeroNode(ExpressionNode expr){
		this.expr = expr;
	}
	
	public ExpressionNode getExpression(){
		return expr;
	}
	
	@Override
	public void applyRules(Scope scope) {
		assert false : "StarbucksMethodCalls do not need semantic checking.";
	}
	
	@Override
	public MidNodeList convertToMidLevel(MidSymbolTable symbolTable) {
		return MidVisitor.visit(this, symbolTable);
	}
	
	
	@Override
	public void simplifyExpressions(){
		assert false : "StarbucksMethodCalls do not have parameters to simplify.";
	}

	
}