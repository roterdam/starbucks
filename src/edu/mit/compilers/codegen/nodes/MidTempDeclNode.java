package edu.mit.compilers.codegen.nodes;

import edu.mit.compilers.codegen.MidLabelManager;

public class MidTempDeclNode extends MidMemoryNode {
	
	static int nodeNum = 0;

	public MidTempDeclNode() {
		super("t" + MidLabelManager.getNewId());
		// generate a random temp id
		
	}
}