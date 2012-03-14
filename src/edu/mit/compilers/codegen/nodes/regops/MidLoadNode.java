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

}
