package edu.mit.compilers.codegen.nodes.memory;

/**
 * Represents memory on the stack that would be deallocated after a method.
 */
abstract public class MidLocalMemoryNode extends MidMemoryNode {
	
	public MidLocalMemoryNode(String name) {
		super(name);
	}
	
	@Override
	public String getFormattedLocationReference() {
		return "[ rbp - " + rawLocationReference + " ]";
	}

}
