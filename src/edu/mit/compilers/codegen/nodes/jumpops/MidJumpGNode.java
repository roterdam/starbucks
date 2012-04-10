package edu.mit.compilers.codegen.nodes.jumpops;

import java.util.List;

import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpCode;
import edu.mit.compilers.codegen.nodes.MidLabelNode;

public class MidJumpGNode extends MidJumpNode {

	public MidJumpGNode(MidLabelNode labelNode) {
		super(labelNode);
	}

	@Override
	public List<ASM> toASM() {
		return super.toASM(OpCode.JG);
	}
	
	@Override
	public boolean isConditional() {
		return true;
	}

}
