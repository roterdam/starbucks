package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.ValidReturnChecker;
import edu.mit.compilers.crawler.Scope.BlockType;
import edu.mit.compilers.grammar.DecafNode;

@SuppressWarnings("serial")
public class IFNode extends DecafNode {
	
	@Override
	public void validateChildren(Scope scope) {
		assert getNumberOfChildren() == 2 || getNumberOfChildren() == 3;
		assert getChild(0) instanceof IF_CLAUSENode;
		assert getChild(1) instanceof BLOCKNode;
		getChild(0).validate(scope);
		((BLOCKNode) getChild(1)).validate(scope, BlockType.IF);
		if (getNumberOfChildren() == 3) {
			getChild(2).validate(scope);
		}
	}
	
	@Override
	public boolean hasValidReturn(ValidReturnChecker returnChecker) {
		return returnChecker.visit(this);
	}
}