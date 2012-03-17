package edu.mit.compilers.codegen.nodes.regops;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.Reg;

public abstract class MidUnaryRegNode extends MidRegisterNode {

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
	
}
