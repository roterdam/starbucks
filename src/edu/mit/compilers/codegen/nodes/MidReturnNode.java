package edu.mit.compilers.codegen.nodes;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;

public class MidReturnNode extends MidNode {
	
	MidMemoryNode returnValue;
	
	/**
	 * Takes a return memoryNode to push onto stack. NULL if returns void.
	 */
	public MidReturnNode(MidMemoryNode returnValue) {
		this.returnValue = returnValue;
	}
	
	@Override
	public List<ASM> toASM() {
		List<ASM> out = new ArrayList<ASM>();
		out.add(new OpASM(OpASM.OpCode.LEAVE));
		out.add(new OpASM(OpASM.OpCode.RET));
		return out;
	}
}
