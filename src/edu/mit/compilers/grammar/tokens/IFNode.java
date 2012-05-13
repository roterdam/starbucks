package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.MidVisitor;
import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.Scope.BlockType;
import edu.mit.compilers.crawler.ValidReturnChecker;
import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.opt.algebra.AlgebraicSimplifier;

@SuppressWarnings("serial")
public class IFNode extends DecafNode {
	
	public IF_CLAUSENode getIfClauseNode(){
		assert getFirstChild() instanceof IF_CLAUSENode;
		return (IF_CLAUSENode) this.getFirstChild();
	}
	
	public void setBlockNode(BLOCKNode node){
		replaceChild(1, node);
	}
	
	public BLOCKNode getBlockNode(){
		assert getChild(1) instanceof BLOCKNode;
		return (BLOCKNode) this.getChild(1);
	}
	
	public Boolean hasElseBlockNode(){
		return getNumberOfChildren() == 3;
	}
	
	/**
	 * Returns the else block. null if there is no else block
	 */
	
	public ELSENode getElseBlock(){
		if (hasElseBlockNode()){
			assert getChild(2) instanceof ELSENode;	
			return (ELSENode) getChild(2);
		}
		return null;
	}
	@Override
	public MidNodeList convertToMidLevel(MidSymbolTable symbolTable) {
		return MidVisitor.visit(this, symbolTable);
	}
	@Override
	public void validateChildren(Scope scope) {
		assert getNumberOfChildren() == 2 || getNumberOfChildren() == 3;
		assert getChild(0) instanceof IF_CLAUSENode;
		assert getChild(1) instanceof BLOCKNode;
		
		getIfClauseNode().validate(scope);
		getBlockNode().validate(scope, BlockType.IF);
		if (hasElseBlockNode()) {
			getElseBlock().validate(scope);
		}
	}
	
	@Override
	public boolean hasValidReturn(ValidReturnChecker returnChecker) {
		return returnChecker.visit(this);
	}
	
	@Override
	public void simplifyExpressions(){
		AlgebraicSimplifier.visit(this);
	}
	
}