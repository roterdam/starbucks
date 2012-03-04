package edu.mit.compilers.grammar;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.Scope.BlockType;

@SuppressWarnings("serial")
public class FORNode extends DecafNode {

	@Override
	public void validateChildren(Scope scope) {
		assert getNumberOfChildren() == 3;
		assert getChild(0) instanceof FOR_INITIALIZENode;
		assert getChild(1) instanceof FOR_TERMINATENode;
		assert getChild(2) instanceof BLOCKNode;
		getChild(0).validate(scope);
		getChild(1).validate(scope);
		((BLOCKNode) getChild(2)).validate(scope, BlockType.FOR);
	}

}