package edu.mit.compilers.grammar;

import edu.mit.compilers.crawler.Scope;

@SuppressWarnings("serial")
public class BLOCKNode extends DecafNode {

	@Override
	public void validate(Scope scope) {
		scope = new Scope(scope);
		checkRule(scope);
		validateChildren(scope);
		scope = scope.getParent();
	}

}