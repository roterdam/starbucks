package edu.mit.compilers.codegen.nodes.regops;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;
import edu.mit.compilers.codegen.Reg;

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

}
