package edu.mit.compilers.codegen.nodes.regops;

public abstract class MidBinaryRegNode extends MidRegisterNode {

	private MidRegisterNode leftOperand;
	private MidRegisterNode rightOperand;

	public MidBinaryRegNode(MidRegisterNode leftOperand,
			MidRegisterNode rightOperand) {
		super();
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
		return "<" + className.substring(mid) + ">";
	}
	
	@Override
	public String toDotSyntax() {
		String out = super.toDotSyntax();
		out += leftOperand.hashCode() + " -> " + hashCode() + " [style=dotted,color=maroon];\n";
		out += rightOperand.hashCode() + " -> " + hashCode() + " [style=dotted,color=maroon];\n";
		return out;
	}

}
