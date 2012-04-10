package edu.mit.compilers.codegen.nodes.jumpops;

import java.util.List;

import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpCode;
import edu.mit.compilers.codegen.nodes.MidLabelNode;

public class MidJumpNENode extends MidJumpNode {

	public MidJumpNENode(MidLabelNode labelNode) {
		super(labelNode);
	}
	
	@Override
	public List<ASM> toASM() {
		return super.toASM(OpCode.JNE);
	}

	@Override
	public boolean isConditional() {
		return true;
	}
}
