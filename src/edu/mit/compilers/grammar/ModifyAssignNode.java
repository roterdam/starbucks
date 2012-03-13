package edu.mit.compilers.grammar;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.SemanticRules;
import edu.mit.compilers.grammar.tokens.ASSIGNNode;

@SuppressWarnings("serial")
public class ModifyAssignNode extends ASSIGNNode {
	
	@Override
	public void applyRules(Scope scope) {
		SemanticRules.apply(this, scope);
	}

}
