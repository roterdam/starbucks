package edu.mit.compilers.codegen;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.grammar.ExpressionNode;

@SuppressWarnings("serial")
public class PotentialCheckDivideByZeroNode extends DecafNode {

	boolean active;
	private ExpressionNode expr;
	public PotentialCheckDivideByZeroNode(ExpressionNode expr, boolean active){
		this.expr = expr;
		this.active = active;
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

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public boolean isActive(){
		return active;
	}

	
}