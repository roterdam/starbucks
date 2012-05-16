package edu.mit.compilers.codegen.nodes.regops;

import java.util.List;

import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpCode;


public class MidNotNode extends MidUnaryRegNode {


	public MidNotNode(MidLoadNode operand) {
		super(operand);
	}

	@Override
	public List<ASM> toASM() {
		return super.toASM(OpCode.NOT);		
	}

	@Override
	public long applyOperation(long value) {
		return value^0;
	}

}
