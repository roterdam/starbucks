package edu.mit.compilers.codegen.nodes;

import edu.mit.compilers.codegen.nodes.regops.MidRegisterNode;

/**
 * Saves referenced register node to memory (the stack?).
 */
public class MidSaveNode extends MidNode {
	
	private MidRegisterNode registerNode;
	
	public MidSaveNode(MidRegisterNode refNode) {
		this.registerNode = refNode;
	}
	
	public MidRegisterNode getRefNode() {
		return registerNode;
	}

}
