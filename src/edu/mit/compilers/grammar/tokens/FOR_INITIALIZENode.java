package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.grammar.DecafNode;


@SuppressWarnings("serial")
public class FOR_INITIALIZENode extends DecafNode {

	@Override
	public void validate(Scope scope) {
		assert false : "For loop initializiation is treated differently and should never be validated";
	}
	
}