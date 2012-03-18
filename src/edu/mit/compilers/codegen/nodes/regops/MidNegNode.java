package edu.mit.compilers.codegen.nodes.regops;

import java.util.List;

import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpCode;


public class MidNegNode extends MidUnaryRegNode {


	public MidNegNode(MidLoadNode operand) {
		super(operand);
	}

	public List<ASM> toASM() {
		return super.toASM(OpCode.NEG);	
	}
}
