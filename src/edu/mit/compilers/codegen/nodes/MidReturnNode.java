package edu.mit.compilers.codegen.nodes;

public class MidReturnNode extends MidNode {
	
	MidMemoryNode returnValue;
	
	/**
	 * Takes a return memoryNode to push onto stack. NULL if returns void.
	 */
	public MidReturnNode(MidMemoryNode returnValue) {
		this.returnValue = returnValue;
	}

}
