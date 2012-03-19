package edu.mit.compilers.codegen.nodes.memory;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.AsmVisitor;
import edu.mit.compilers.codegen.MemoryManager;
import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;

public class MidParamDeclNode extends MidLocalMemoryNode {
	
	private int paramOffset;

	public MidParamDeclNode(String name, int paramOffset) {
		super(name);
		this.paramOffset = paramOffset;
	}

	/*
	@Override
	public String getFormattedLocationReference() {
		return AsmVisitor.paramAccess(paramOffset);
	}
	*/
	
	@Override
	public List<ASM> toASM() {
		//Move things into the stack.
		ArrayList<ASM> out = new ArrayList<ASM>();
		if (paramOffset < AsmVisitor.paramRegisters.length) {
			//return paramRegisters[paramOffset].name();
			out.add(new OpASM("Load param into stack here", OpCode.MOV, getFormattedLocationReference(), AsmVisitor.paramRegisters[paramOffset].name()));
		} else {
			//NOTE: We assume that this is done at top -> r10, r11 empty
			out.add(new OpASM("Load stack param into register here", OpCode.MOV, Reg.R10.name(), String.format("[ %s + %d*%d + %d]", Reg.RBP,
					(paramOffset - AsmVisitor.paramRegisters.length),
					MemoryManager.ADDRESS_SIZE, MemoryManager.ADDRESS_SIZE * 2)));
			out.add(new OpASM("Load temp reg into stack here", OpCode.MOV, getFormattedLocationReference(), Reg.R10.name()));
		}
		
		return out;
	}
	
}
