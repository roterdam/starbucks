package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.MidVisitor;
import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.SemanticRules;
import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.grammar.ExpressionNode;
import edu.mit.compilers.opt.algebra.AlgebraicSimplifier;
import edu.mit.compilers.opt.forunroll.Unroller;

@SuppressWarnings("serial")
public class ASSIGNNode extends DecafNode {

	@Override
	public void applyRules(Scope scope) {
		SemanticRules.apply(this, scope);
	}

	/**
	 * Returns the left side of the assign.
	 */
	public IDNode getLocation() {
		assert getChild(0) instanceof IDNode;
		return (IDNode) getChild(0);
	}

	/**
	 * Returns the right side of the assign.
	 */
	public ExpressionNode getExpression() {
		assert getChild(1) instanceof ExpressionNode;
		return (ExpressionNode) getChild(1);
	}

	public void setExpression(ExpressionNode x) {
		replaceChild(1, x);
	}

	@Override
	public MidNodeList convertToMidLevel(MidSymbolTable symbolTable) {
		return MidVisitor.visit(this, symbolTable);
	}
	
	@Override
	public void simplifyExpressions(){
		AlgebraicSimplifier.visit(this);
	}
	
	@Override
	public boolean isUnrollable(String var, boolean hasLoopScope){
		return Unroller.isUnrollable(this, var, hasLoopScope);
	}

}