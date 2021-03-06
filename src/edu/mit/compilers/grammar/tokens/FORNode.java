package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.MidVisitor;
import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.Scope.BlockType;
import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.opt.algebra.AlgebraicSimplifier;
import edu.mit.compilers.opt.forunroll.Unroller;

@SuppressWarnings("serial")
public class FORNode extends DecafNode {
	
	public FOR_INITIALIZENode getForInitializeNode(){
		assert getChild(0) instanceof FOR_INITIALIZENode;
		return (FOR_INITIALIZENode) getFirstChild();
	}
	
	public ASSIGNNode getAssignNode(){
		return getForInitializeNode().getAssignNode();
	}
	
	public FOR_TERMINATENode getForTerminateNode(){
		assert getChild(1) instanceof FOR_TERMINATENode;
		return (FOR_TERMINATENode) getChild(1);
	}
	
	public BLOCKNode getBlockNode(){
		assert getChild(2) instanceof BLOCKNode;
		return (BLOCKNode) getChild(2);
	}
	@Override
	public MidNodeList convertToMidLevel(MidSymbolTable symbolTable) {
		return MidVisitor.visit(this, symbolTable);
	}
	
	@Override
	public void validateChildren(Scope scope) {
		assert getNumberOfChildren() == 3;
		
		getForInitializeNode().validate(scope);
		getForTerminateNode().validate(scope);
		getBlockNode().validate(scope, BlockType.FOR, getForInitializeNode() );
	}

	@Override
	public void simplifyExpressions(){
		AlgebraicSimplifier.visit(this);
	}
	
	@Override
	public DecafNode unroll(){
		return Unroller.unroll(this);
	}
	
	@Override
	public boolean isUnrollable(String var, boolean hasLoopScope){
		return Unroller.isUnrollable(this, var, hasLoopScope);
	}
}