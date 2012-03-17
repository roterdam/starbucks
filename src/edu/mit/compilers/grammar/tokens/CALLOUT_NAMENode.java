package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.grammar.DecafNode;

@SuppressWarnings("serial")
public class CALLOUT_NAMENode extends DecafNode {
	
	public String getName() {
		assert getNumberOfChildren() == 1;
		return getChild(0).getText();
	}

}