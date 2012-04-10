package edu.mit.compilers.codegen.nodes.regops;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;

public abstract class MidUnaryRegNode extends MidRegisterNode implements
		RegisterOpNode {

	private MidLoadNode operand;

	public MidUnaryRegNode(MidLoadNode operand) {
		super();
		this.operand = operand;
	}

	public MidLoadNode getOperand() {
		return operand;
	}

	public String toString() {
		String className = getClass().getName();
		int mid = className.lastIndexOf('.') + 1;
		return "<" + className.substring(mid) + ": " + operand.toString() + ">";
	}

	@Override
	public List<Reg> getOperandRegisters() {
		List<Reg> out = new ArrayList<Reg>();
		out.add(operand.getRegister());
		return out;
	}

	public List<ASM> toASM(OpCode op) {
		List<ASM> out = new ArrayList<ASM>();
		out.add(new OpASM(toString(), op, this.getOperand().getRegister()
				.name()));
		return out;
	}

	@Override
	public void updateLoadNode(MidLoadNode oldNode, MidLoadNode newNode) {
		operand = newNode;
	}

}
