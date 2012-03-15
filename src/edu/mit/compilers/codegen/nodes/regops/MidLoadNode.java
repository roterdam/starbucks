package edu.mit.compilers.codegen.nodes.regops;

import edu.mit.compilers.codegen.nodes.MidMemoryNode;

/**
 * Loads value from memory.
 */
public class MidLoadNode extends MidRegisterNode {

	private MidMemoryNode memoryNode;

	public MidLoadNode(MidMemoryNode memoryNode) {
		this.memoryNode = memoryNode;
	}

	public MidMemoryNode getMemoryNode() {
		return memoryNode;
	}

	public String toString() {
		String className = getClass().getName();
		int mid = className.lastIndexOf('.') + 1;
		return "<" + className.substring(mid) + ": " + memoryNode.toString() + ">";
	}

}
