package edu.mit.compilers.codegen.nodes.regops;

public abstract class MidArithmeticNode extends MidBinaryRegNode {

	public MidArithmeticNode(MidLoadNode leftOperand, MidLoadNode rightOperand) {
		super(leftOperand, rightOperand);
		// TODO Auto-generated constructor stub
	}
	
	public abstract boolean isCommutative();

}
