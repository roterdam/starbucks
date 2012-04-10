package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.SemanticRules;
import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.grammar.ExpressionNode;


@SuppressWarnings("serial")
public class WHILE_TERMINATENode extends DecafNode {
	
	@Override
	public void applyRules(Scope scope) {
		SemanticRules.apply(this, scope);
	}
	
	public ExpressionNode getExpressionNode() {
		assert getChild(0) instanceof ExpressionNode;
		return (ExpressionNode) getChild(0);
	}
	
	public void setExpressionNode(ExpressionNode x) {
		replaceChild(0, x);
	}

}