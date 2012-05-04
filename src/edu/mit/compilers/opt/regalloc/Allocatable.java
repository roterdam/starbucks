package edu.mit.compilers.opt.regalloc;

import edu.mit.compilers.codegen.Reg;

public interface Allocatable {
	
	public void allocateRegister(Reg allocatedReg);
	public Reg getAllocatedRegister();

}
