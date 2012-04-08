package edu.mit.compilers.codegen.nodes.memory;

import edu.mit.compilers.codegen.MidLabelManager;

public class MidTempDeclNode extends MidLocalMemoryNode {
	
	static int nodeNum = 0;
	private MidTempDeclNode linkedTempDecl;

	public MidTempDeclNode() {
		// generate a random temp id
		super("t" + MidLabelManager.getNewId());
	}
	
	public void linkTempDecl(MidTempDeclNode node) {
		registerTempDecl(node);
		node.registerTempDecl(this);
	}
	
	public void registerTempDecl(MidTempDeclNode node) {
		linkedTempDecl = node;
	}
	
	@Override
	public void setRawLocationReference(String rawLocationReference) {
		super.setRawLocationReference(rawLocationReference);
		assert linkedTempDecl.getRawLocationReference() == null;
		linkedTempDecl.setRawLocationReference(rawLocationReference);
	}

}