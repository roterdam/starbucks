package edu.mit.compilers.grammar;

import edu.mit.compilers.crawler.Scope;

@SuppressWarnings("serial")
public class CLASSNode extends DecafNode {

	@Override
	public void validateChildren(Scope scope) {
		assert getNumberOfChildren() >= 1; //must have a class name
		assert getNumberOfChildren() <= 3; //no more than id, field_decls, and methods
		assert getChild(0) instanceof IDNode;
		
		//Don't check the 0th child.
		DecafNode child = this.getChild(1); 	
		while (child != null) {
			child.validate(scope);
			child = child.getNextSibling();
		}
		
	}
}