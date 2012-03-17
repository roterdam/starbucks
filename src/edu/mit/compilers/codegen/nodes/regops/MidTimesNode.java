package edu.mit.compilers.codegen.nodes.regops;

import java.util.List;

import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpCode;


public class MidTimesNode extends MidBinaryRegNode {


	public MidTimesNode(MidLoadNode leftOperand, MidLoadNode rightOperand) {
		super(leftOperand, rightOperand);
	}

	public List<ASM> toASM() {
		return super.toASM(OpCode.IMUL);		
	}
	
}
