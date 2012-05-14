package edu.mit.compilers.codegen.nodes;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.regops.MidUseNode;

public class MidReturnNode extends MidNode implements MidUseNode {
	
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
		if (returnValue != null){
			out.add(new OpASM("Setting return value", OpCode.MOV, Reg.RAX.name(), returnValue.getFormattedLocationReference()));
		} else {
			out.add(new OpASM("Setting 0 return value (void)", OpCode.MOV, Reg.RAX.name(), "0"));
		}
		out.add(new OpASM(OpCode.LEAVE));
		out.add(new OpASM(OpCode.RET));
		return out;
	}

	@Override
	public MidMemoryNode getMemoryNode() {
		return returnValue;
	}
}
