package edu.mit.compilers.codegen.nodes;

public class MidJumpNode extends MidNode {
	
	private MidLabelNode labelNode;
	
	public MidJumpNode(MidLabelNode labelNode) {
		this.labelNode = labelNode;
	}

	public MidLabelNode getLabelNode() {
		return labelNode;
	}
}
