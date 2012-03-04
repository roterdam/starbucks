package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.Scope.BlockType;
import edu.mit.compilers.grammar.DecafNode;

@SuppressWarnings("serial")
public class BLOCKNode extends DecafNode {

	
	public void validate(Scope scope, BlockType blockType) {
		scope = new Scope(scope, blockType);
		super.validate(scope);
		scope = scope.getParent();
	}
	@Override
	public void validate(Scope scope) {
		scope = new Scope(scope, BlockType.ANON);
		super.validate(scope);
		scope = scope.getParent();
	}	

}