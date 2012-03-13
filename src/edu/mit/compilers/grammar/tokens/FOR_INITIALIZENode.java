package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.SemanticRules;
import edu.mit.compilers.grammar.DecafNode;


@SuppressWarnings("serial")
public class FOR_INITIALIZENode extends DecafNode {

	public ASSIGNNode getAssignNode(){
		assert getNumberOfChildren() == 1;
		assert getFirstChild() instanceof ASSIGNNode;
		return (ASSIGNNode) getFirstChild();
	}
	
	@Override
	public void validateChildren(Scope scope) {
		assert getNumberOfChildren() == 1;
		assert getAssignNode().getNumberOfChildren() == 2;
		
		getAssignNode().getExpression().validate(scope);
	}
	
	@Override
	public void applyRules(Scope scope) {
		SemanticRules.apply(this, scope);
	}
	
}