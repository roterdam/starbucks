package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.SemanticRules;
import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.grammar.ExpressionNode;


@SuppressWarnings("serial")
public class FOR_TERMINATENode extends DecafNode {
	
	public ExpressionNode getExpressionNode(){
		assert getNumberOfChildren() == 1;
		return (ExpressionNode) getFirstChild();
	}
	
	@Override
	public void applyRules(Scope scope) {
		SemanticRules.apply(this, scope);
	}

}