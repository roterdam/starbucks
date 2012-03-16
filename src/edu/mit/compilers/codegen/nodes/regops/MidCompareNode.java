package edu.mit.compilers.codegen.nodes.regops;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;

public class MidCompareNode extends MidBinaryRegNode {

	public MidCompareNode(MidLoadNode leftOperand, MidLoadNode rightOperand) {
		super(leftOperand, rightOperand);
	}

	public List<ASM> toASM() {
		List<ASM> out = new ArrayList<ASM>();
		out.add(new OpASM(OpASM.OpCode.CMP, 
						new String[] {}, 
							"Error: Needs to do more in " + this.getClass()));
		return out;
	}
	
}
