package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.SemanticRules;
import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.grammar.ExpressionNode;

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
	
}