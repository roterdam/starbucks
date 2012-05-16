package edu.mit.compilers.codegen.nodes.memory;

import edu.mit.compilers.grammar.tokens.FORNode;

public class MidLocalVarDeclNode extends MidLocalMemoryNode {
	FORNode forNode;
	public MidLocalVarDeclNode(String name) {
		super(name);
	}
	
	public void setForNode(FORNode forNode){
		this.forNode = forNode;
	}
	
	public boolean isInLoop(){
		return forNode != null;
	}
	
	public FORNode getForNode(){
		assert isInLoop() : this + " is not in a for loop";
		return forNode;
	}
	
}