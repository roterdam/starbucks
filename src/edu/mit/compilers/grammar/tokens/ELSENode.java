package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.Scope.BlockType;
import edu.mit.compilers.grammar.DecafNode;


@SuppressWarnings("serial")
public class ELSENode extends DecafNode {
	
	@Override
	public void validateChildren(Scope scope) {
		assert getNumberOfChildren() == 1;
		assert getChild(0) instanceof BLOCKNode;
		((BLOCKNode) getChild(0)).validate(scope, BlockType.ELSE);
	}
	
}