package edu.mit.compilers.codegen.nodes.regops;


public class MidDivideNode extends MidRegisterNode {

	private MidRegisterNode leftOperand;
	private MidRegisterNode rightOperand;

	public MidDivideNode(MidRegisterNode leftOperand, MidRegisterNode rightOperand) {
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
	}

	public MidRegisterNode getLeftOperand() {
		return leftOperand;
	}

	public MidRegisterNode getRightOperand() {
		return rightOperand;
	}

}
