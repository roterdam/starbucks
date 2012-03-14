package edu.mit.compilers.codegen.nodes;

/**
 * Saves referenced register node or literal to the stack.
 */
public class MidStackNode extends MidMemoryNode {
		
	private String name;
	
	public MidStackNode(String name) {
		this.name = name;
	}
	
	public String toString() {
		String className = getClass().getName();
		int mid = className.lastIndexOf('.') + 1;
		return "<" + className.substring(mid) + ": " + name + ">";
	}

}
