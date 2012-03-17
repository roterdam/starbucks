package edu.mit.compilers.codegen.nodes.regops;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;
import edu.mit.compilers.codegen.nodes.MidNode;

public class MidCompareNode extends MidNode implements RegisterOpNode {
	
	private MidRegisterNode leftOperand;
	private MidRegisterNode rightOperand;

	public MidCompareNode(MidLoadNode leftOperand, MidLoadNode rightOperand) {
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
	}

	public List<ASM> toASM() {
		List<ASM> out = new ArrayList<ASM>();
		out.add(new OpASM(toString(), OpCode.CMP, leftOperand.getRegister().name(),
				rightOperand.getRegister().name()));
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