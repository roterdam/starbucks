package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.MidVisitor;
import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.Scope.BlockType;
import edu.mit.compilers.grammar.DecafNode;

@SuppressWarnings("serial")
public class FORNode extends DecafNode {
	
	public FOR_INITIALIZENode getForInitializeNode(){
		assert getChild(0) instanceof FOR_INITIALIZENode;
		return (FOR_INITIALIZENode) getFirstChild();
	}
	
	public ASSIGNNode getAssignNode(){
		return (ASSIGNNode) getForInitializeNode().getAssignNode();
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

}