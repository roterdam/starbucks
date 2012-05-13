package edu.mit.compilers.grammar;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.SemanticRules;

@SuppressWarnings("serial")
public abstract class BranchNode extends DecafNode {
	
	@Override
	public void applyRules(Scope scope) {
		SemanticRules.apply(this, scope);
	}
	
	@Override
	public boolean isBlockEnder(){
		return true;
	}
}
