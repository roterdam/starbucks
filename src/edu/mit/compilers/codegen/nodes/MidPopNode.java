package edu.mit.compilers.codegen.nodes;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;

public class MidPopNode extends MidNode {
	Reg r;
	public MidPopNode(Reg r){
		this.r = r;
	}
	
	@Override
	public List<ASM> toASM() {
		List<ASM> out = new ArrayList<ASM>();
		out.add(new OpASM("pop param onto stack", OpCode.POP, r.name()));
		return out;
	}
}
