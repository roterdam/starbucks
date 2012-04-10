package edu.mit.compilers.codegen.nodes.regops;

public abstract class MidArithmeticNode extends MidBinaryRegNode {

	public MidArithmeticNode(MidLoadNode leftOperand, MidLoadNode rightOperand) {
		super(leftOperand, rightOperand);
	}
	
	public abstract boolean isCommutative();
	
}
