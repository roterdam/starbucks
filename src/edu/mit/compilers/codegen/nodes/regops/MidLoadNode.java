package edu.mit.compilers.codegen.nodes.regops;

import edu.mit.compilers.codegen.nodes.MidMemoryNode;

/**
 * Loads value from memory.
 */
public class MidLoadNode extends MidRegisterNode {

	private MidMemoryNode saveNode;

	public MidLoadNode(MidMemoryNode saveNode) {
		this.saveNode = saveNode;
	}

	public MidMemoryNode getSaveNode() {
		return saveNode;
	}

	public String toString() {
		String className = getClass().getName();
		int mid = className.lastIndexOf('.') + 1;
		return "<" + className.substring(mid) + ": " + saveNode.toString()
				+ ">";
	}

}
