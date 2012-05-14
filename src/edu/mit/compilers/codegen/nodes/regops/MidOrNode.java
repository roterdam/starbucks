package edu.mit.compilers.codegen.nodes.regops;

import java.util.List;

import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpCode;

public class MidOrNode extends MidBinaryRegNode {

	public MidOrNode(MidLoadNode leftOperand, MidLoadNode rightOperand) {
		super(leftOperand, rightOperand);
	}

	public List<ASM> toASM() {
		return super.toASM(OpCode.OR);
	}

	@Override
	public long applyOperation(long left, long right) {
		return left | right;
	}

}
