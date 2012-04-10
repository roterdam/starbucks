package edu.mit.compilers.codegen.nodes.jumpops;

import java.util.List;

import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpCode;
import edu.mit.compilers.codegen.nodes.MidLabelNode;

public class MidJumpLNode extends MidJumpNode {

	public MidJumpLNode(MidLabelNode labelNode) {
		super(labelNode);
	}
	
	@Override
	public List<ASM> toASM() {
		return super.toASM(OpCode.JL);
	}

	@Override
	public boolean isConditional() {
		return true;
	}

}
