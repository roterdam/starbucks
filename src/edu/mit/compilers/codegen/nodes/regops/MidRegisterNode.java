package edu.mit.compilers.codegen.nodes.regops;

import edu.mit.compilers.codegen.MidLabelManager;
import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.nodes.MidNode;

/**
 * Represents an instruction that stores something on a register.
 */
abstract public class MidRegisterNode extends MidNode {
	
	String name;
	private Reg register;
	
	public MidRegisterNode() {
		name = "reg" + MidLabelManager.getNewId();
	}
	
	public String getName() {
		return name;
	}
	
	public void setRegister(Reg r) {
		register = r;
	}
	
	public Reg getRegister() {
		return register;
	}
	
}
