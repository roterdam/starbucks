package edu.mit.compilers.grammar;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.SemanticRules;
import edu.mit.compilers.opt.forunroll.Unroller;

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
	
	@Override
	public boolean isUnrollable(String var, boolean hasLoopScope){
		return Unroller.isUnrollable(this, var, hasLoopScope);
	}
}
