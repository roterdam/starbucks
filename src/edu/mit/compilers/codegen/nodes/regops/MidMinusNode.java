package edu.mit.compilers.codegen.nodes.regops;

import java.util.List;

import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpCode;

public class MidMinusNode extends MidArithmeticNode {

	public MidMinusNode(MidLoadNode leftOperand, MidLoadNode rightOperand) {
		super(leftOperand, rightOperand);
	}

	@Override
	public List<ASM> toASM() {
		return super.toASM(OpCode.SUB);
	}

	@Override
	public boolean isCommutative() {
		return false;
	}
	
	@Override
	public long applyOperation(long left, long right) {
		return left - right;
	}

}
