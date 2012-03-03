package edu.mit.compilers.crawler;

import edu.mit.compilers.grammar.DecafNode;

public class ExitScopeNode extends DecafNode {

	public ExitScopeNode() {
		
	}
	
	@Override
	public boolean exitScope() {
		return true;
	}
}
