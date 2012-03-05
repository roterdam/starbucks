package edu.mit.compilers.grammar;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.SemanticRules;

@SuppressWarnings("serial")
public class ModifyAssignNode extends DecafNode {
	
	@Override
	public void applyRules(Scope scope) {
		SemanticRules.apply(this, scope);
	}

}
