package edu.mit.compilers.codegen.nodes.regops;

import edu.mit.compilers.codegen.nodes.MidSaveNode;

/**
 * Loads value from memory.
 */
public class MidLoadNode extends MidRegisterNode {

	private MidSaveNode saveNode;

	public MidLoadNode(MidSaveNode saveNode) {
		this.saveNode = saveNode;
	}

	public MidSaveNode getSaveNode() {
		return saveNode;
	}

	public String toString() {
		String className = getClass().getName();
		int mid = className.lastIndexOf('.') + 1;
		return "<" + className.substring(mid) + ": " + saveNode.toString()
				+ ">";
	}

}
