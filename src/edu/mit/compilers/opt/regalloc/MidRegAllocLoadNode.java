package edu.mit.compilers.opt.regalloc;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;

public class MidRegAllocLoadNode extends MidLoadNode {

	public MidRegAllocLoadNode(MidMemoryNode memoryNode) {
		super(memoryNode);
	}
	
	@Override
	public List<ASM> toASM() {
		return new ArrayList<ASM>();
	}

}
