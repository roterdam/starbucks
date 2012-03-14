package edu.mit.compilers.codegen.nodes.regops;

public abstract class MidUnaryRegNode extends MidRegisterNode {

	private MidRegisterNode operand;

	public MidUnaryRegNode(MidRegisterNode operand) {
		this.operand = operand;
	}

	public MidRegisterNode getoperand() {
		return operand;
	}
	
}
