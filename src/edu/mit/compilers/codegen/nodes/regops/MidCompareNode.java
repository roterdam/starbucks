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
		assert leftOperand != null : "Left operand is null.";
		assert rightOperand != null : "Left operand is null.";
		this.leftOperand = leftOperand;
		this.rightOperand = rightOperand;
		leftOperand.recordRegisterOp(this);
		rightOperand.recordRegisterOp(this);
	}

	public List<ASM> toASM() {
		List<ASM> out = new ArrayList<ASM>();
		out.add(new OpASM(toString(), OpCode.CMP, leftOperand.getRegister()
				.name(), rightOperand.getRegister().name()));
		return out;
	}

	public List<Reg> getOperandRegisters() {
		List<Reg> out = new ArrayList<Reg>();
		assert leftOperand.getRegister() != null : "Left operand register is null: "
				+ leftOperand;
		assert rightOperand.getRegister() != null : "Right operand register is null: "
				+ rightOperand;
		out.add(leftOperand.getRegister());
		out.add(rightOperand.getRegister());
		return out;
	}

	public String toDotSyntax() {
		String out = super.toDotSyntax();
		out += leftOperand.hashCode() + " -> " + hashCode()
				+ " [style=dotted,color=maroon];\n";
		out += rightOperand.hashCode() + " -> " + hashCode()
				+ " [style=dotted,color=maroon];\n";
		return out;
	}

	public void updateLoadNode(MidLoadNode oldNode, MidLoadNode newNode) {
		if (oldNode == leftOperand) {
			leftOperand = newNode;
		}
		if (oldNode == rightOperand) {
			rightOperand = newNode;
		}
	}

}