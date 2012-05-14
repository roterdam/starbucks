package edu.mit.compilers.codegen.nodes;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.regops.MidUseNode;
import edu.mit.compilers.opt.regalloc.nodes.Allocatable;

public class MidReturnNode extends MidNode implements MidUseNode, Allocatable {

	MidMemoryNode returnValue;
	private Reg allocatedReg;

	/**
	 * Takes a return memoryNode to push onto stack. NULL if returns void.
	 */
	public MidReturnNode(MidMemoryNode returnValue) {
		this.returnValue = returnValue;
	}

	@Override
	public List<ASM> toASM() {
		List<ASM> out = new ArrayList<ASM>();
		String returnValueLocation;
		if (allocatedReg == null) {
			if (returnValue == null) {
				returnValueLocation = "0";
			} else {
				returnValueLocation = returnValue
						.getFormattedLocationReference();
			}
		} else {
			returnValueLocation = allocatedReg.name();
		}
		out.add(new OpASM("Setting return value", OpCode.MOV, Reg.RAX.name(),
				returnValueLocation));
		out.add(new OpASM(OpCode.LEAVE));
		out.add(new OpASM(OpCode.RET));
		return out;
	}

	@Override
	public MidMemoryNode getMemoryNode() {
		return returnValue;
	}

	@Override
	public void allocateRegister(Reg allocatedReg) {
		this.allocatedReg = allocatedReg;
	}

	@Override
	public Reg getAllocatedRegister() {
		return allocatedReg;
	}

}
