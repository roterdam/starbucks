package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.Scope.BlockType;
import edu.mit.compilers.crawler.ValidReturnChecker;
import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.opt.algebra.AlgebraicSimplifier;


@SuppressWarnings("serial")
public class ELSENode extends DecafNode {
	
	@Override
	public void validateChildren(Scope scope) {
		assert getNumberOfChildren() == 1;
		assert getChild(0) instanceof BLOCKNode;
		((BLOCKNode) getChild(0)).validate(scope, BlockType.ELSE);
	}
	
	@Override
	public boolean hasValidReturn(ValidReturnChecker returnChecker) {
		return returnChecker.visit(this);
	}
	
	public void setBlockNode(BLOCKNode node) {
		replaceChild(0, node);
	}
	
	public BLOCKNode getBlockNode(){
		assert getChild(0) instanceof BLOCKNode;
		return (BLOCKNode) this.getChild(0);
	}
	
	@Override
	public void simplifyExpressions(){
		AlgebraicSimplifier.visit(this);
	}


	
}