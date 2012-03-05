package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.ErrorCenter;
import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.SemanticRules;
import edu.mit.compilers.crawler.VarDecl;
import edu.mit.compilers.crawler.VarType;
import edu.mit.compilers.crawler.Scope.BlockType;
import edu.mit.compilers.grammar.DecafNode;

@SuppressWarnings("serial")
public class BLOCKNode extends DecafNode {

	
	public void validate(Scope scope, BlockType blockType, FOR_INITIALIZENode node){
		assert (blockType == BlockType.FOR) : "Only should be used for FOR loops";
		scope = new Scope(scope, blockType);
		
		ASSIGNNode assign_node = (ASSIGNNode) node.getChild(0);
		IDNode id = (IDNode) assign_node.getChild(0);
		VarDecl decl_node = new VarDecl(VarType.INT, id.getText(), id.getLine(), id.getColumn());
		
		//TODO: verify that expression is an int.
		scope.addVar(id.getText(), decl_node);
		super.validate(scope);
		scope = scope.getParent();
	}
	                                                                           
	public void validate(Scope scope, BlockType blockType, VarType returnType) {
		scope = new Scope(scope, blockType, returnType);
		super.validate(scope);
		scope = scope.getParent();
	}
	
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