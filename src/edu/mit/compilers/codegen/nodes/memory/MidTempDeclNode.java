package edu.mit.compilers.codegen.nodes.memory;

import edu.mit.compilers.codegen.MidLabelManager;

public class MidTempDeclNode extends MidLocalMemoryNode {
	static int nodeNum = 0;

	public MidTempDeclNode() {
		// generate a random temp id
		super("t" + MidLabelManager.getNewId());
	}

}