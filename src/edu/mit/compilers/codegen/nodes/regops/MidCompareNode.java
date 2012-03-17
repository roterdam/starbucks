package edu.mit.compilers.codegen.nodes.regops;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;


public class MidCompareNode extends MidBinaryRegNode {

	public MidCompareNode(MidLoadNode leftOperand, MidLoadNode rightOperand) {
		super(leftOperand, rightOperand);
	}
	
	
	public List<ASM> toASM() {
		return super.toASM(OpCode.CMP);	
	}
	
}
