package edu.mit.compilers.codegen.nodes.regops;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;

public abstract class MidBinaryRegNode extends MidRegisterNode implements RegisterOpNode {

	private MidLoadNode leftOperand;
	private MidLoadNode rightOperand;

	public MidBinaryRegNode(MidLoadNode leftOperand,
			MidLoadNode rightOperand) {
		super();
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
		leftOperand.recordRegisterOp(this);
		rightOperand.recordRegisterOp(this);
	}

	public MidLoadNode getLeftOperand() {
		return leftOperand;
	}

	public MidLoadNode getRightOperand() {
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
		out += leftOperand.hashCode() + " -> " + hashCode()
				+ " [style=dotted,color=maroon];\n";
		out += rightOperand.hashCode() + " -> " + hashCode()
				+ " [style=dotted,color=maroon];\n";
		return out;
	}
	
	public List<ASM> toASM(OpCode op) {
		List<ASM> out = new ArrayList<ASM>();
		out.add(new OpASM(toString(), op, this.getLeftOperand().getRegister().name(),
				this.getRightOperand().getRegister().name()));
		return out;
	}
		
	@Override
	public List<Reg> getOperandRegisters() {
		List<Reg> out = new ArrayList<Reg>();
		out.add(leftOperand.getRegister());
		out.add(rightOperand.getRegister());
		return out;
	}
	
	@Override
	public void updateLoadNode(MidLoadNode oldNode,
			MidLoadNode newNode) {
		if (oldNode == leftOperand) {
			leftOperand = newNode;
		}
		if (oldNode == rightOperand) {
			rightOperand = newNode;
		}
	}
	
	public abstract long applyOperation(long left, long right);
	
}
