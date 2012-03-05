package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.SemanticRules;
import edu.mit.compilers.grammar.DecafNode;


@SuppressWarnings("serial")
public class FOR_INITIALIZENode extends DecafNode {

	@Override
	public void validateChildren(Scope scope) {
		
		DecafNode child = this.getFirstChild();
		//Don't check the first child. THE ID doesn't exist.
		child = child.getNextSibling();
		while (child != null) {
			child.validate(scope);
			child = child.getNextSibling();
		}
	}
	
	@Override
	public void applyRules(Scope scope) {
		SemanticRules.apply(this, scope);
	}
	
}