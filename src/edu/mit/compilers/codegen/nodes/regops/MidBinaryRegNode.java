package edu.mit.compilers.codegen.nodes.regops;

public abstract class MidBinaryRegNode extends MidRegisterNode {

	private MidRegisterNode leftOperand;
	private MidRegisterNode rightOperand;

	public MidBinaryRegNode(MidRegisterNode leftOperand,
			MidRegisterNode rightOperand) {
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
	}

	public MidRegisterNode getLeftOperand() {
		return leftOperand;
	}

	public MidRegisterNode getRightOperand() {
		return rightOperand;
	}

	public String toString() {
		String className = getClass().getName();
		int mid = className.lastIndexOf('.') + 1;
		return "<" + className.substring(mid) + ": " + leftOperand.toString()
				+ "," + rightOperand.toString() + ">";
	}

}
