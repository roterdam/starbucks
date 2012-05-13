package edu.mit.compilers.codegen.nodes.regops;

import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;

/**
 * Loads in a parameter for a MidCallNode. Special in the sense that it doesn't
 * actually output any ASM code, leaving the MidCallNode to handle the calling
 * convention. It's important to be included in the MidNodeList, though, so that
 * liveness analysis realizes when a memory node is needed by a call.
 */
public class MidParamLoadNode extends MidLoadNode {

	public MidParamLoadNode(MidMemoryNode memoryNode) {
		super(memoryNode);
	}

}
