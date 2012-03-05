package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.Scope.BlockType;
import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.grammar.ExpressionNode;

@SuppressWarnings("serial")
public class FORNode extends DecafNode {

	@Override
	public void validateChildren(Scope scope) {
		assert getNumberOfChildren() == 3;
		assert getChild(0) instanceof FOR_INITIALIZENode;
		assert getChild(0).getNumberOfChildren() == 1;
		assert getChild(0).getFirstChild() instanceof ASSIGNNode;
		assert getChild(1) instanceof FOR_TERMINATENode;
		assert getChild(2) instanceof BLOCKNode;
		
		//No need to validate the scope of the initialize.
		getChild(0).validate(scope);
		
		//TODO: Make sure it's an int.
		getChild(1).validate(scope);
		((BLOCKNode) getChild(2)).validate(scope, BlockType.FOR, (FOR_INITIALIZENode) getChild(0) );
	}

}