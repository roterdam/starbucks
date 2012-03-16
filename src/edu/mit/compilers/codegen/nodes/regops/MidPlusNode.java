package edu.mit.compilers.codegen.nodes.regops;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;


public class MidPlusNode extends MidBinaryRegNode {


	public MidPlusNode(MidLoadNode leftOperand, MidLoadNode rightOperand) {
		super(leftOperand, rightOperand);
	}
	
	@Override
	public List<ASM> toASM() {

		List<ASM> out = new ArrayList<ASM>();
		
		out.add(new OpASM(toString(), OpASM.OpCode.ADD, this.getLeftOperand().getRegisterId(), this.getRightOperand().getRegisterId()));

		return out;
	}


}
