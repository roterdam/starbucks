package edu.mit.compilers.codegen.nodes;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.MemoryManager;
import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;

public class MidMoveSPNode extends MidNode {
	int params;
	public MidMoveSPNode(int params){
		this.params = params;
	}
	
	@Override
	public List<ASM> toASM() {
		List<ASM> out = new ArrayList<ASM>();
		out.add(new OpASM("Clean up params", OpCode.ADD, Reg.RSP.name(),
				Integer.toString(params * MemoryManager.ADDRESS_SIZE)));
		return out;
	}
}