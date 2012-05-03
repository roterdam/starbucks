package edu.mit.compilers.opt.regalloc;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;

public class MidRegAllocLoadNode extends MidLoadNode {

	private Reg register;

	public MidRegAllocLoadNode(Reg register) {
		super(null);
		this.register = register;
	}
	
	@Override
	public List<ASM> toASM() {
		return new ArrayList<ASM>();
	}
	
	@Override
	public Reg getRegister() {
		return register;
	}

}
