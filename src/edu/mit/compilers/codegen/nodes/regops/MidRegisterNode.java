package edu.mit.compilers.codegen.nodes.regops;

import edu.mit.compilers.codegen.MidLabelManager;
import edu.mit.compilers.codegen.nodes.MidNode;

/**
 * Represents an instruction that stores something on a register.
 */
abstract public class MidRegisterNode extends MidNode {
	
	String name;
	
	public MidRegisterNode() {
		name = "reg" + MidLabelManager.getNodeCount();
	}
	
	public String getName() {
		return name;
	}

}
