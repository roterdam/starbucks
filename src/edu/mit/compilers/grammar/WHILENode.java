package edu.mit.compilers.grammar;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.SemanticRules;
import edu.mit.compilers.crawler.Scope.BlockType;

@SuppressWarnings("serial")
public class WHILENode extends DecafNode {
	@Override
	public void validate(Scope scope) {
		SemanticRules.apply(this, scope);
		
		assert getNumberOfChildren() == 2;
		assert getChild(0) instanceof WHILE_TERMINATENode;
		assert getChild(1) instanceof BLOCKNode;
		getChild(0).validate(scope);
		getChild(1).validate(scope);
		((BLOCKNode) getChild(2)).validate(scope, BlockType.WHILE);
	}
}