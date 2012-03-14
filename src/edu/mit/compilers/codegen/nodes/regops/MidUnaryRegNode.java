package edu.mit.compilers.codegen.nodes.regops;

public abstract class MidUnaryRegNode extends MidRegisterNode {

	private MidLoadNode operand;

	public MidUnaryRegNode(MidLoadNode operand) {
		this.operand = operand;
	}

	public MidLoadNode getoperand() {
		return operand;
	}
	
	public String toString() {
		String className = getClass().getName();
		int mid = className.lastIndexOf('.') + 1;
		return "<" + className.substring(mid) + ": " + operand.toString() + ">";
	}

	
}
