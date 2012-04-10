package edu.mit.compilers.codegen.nodes.regops;

import java.util.List;

import edu.mit.compilers.codegen.Reg;

/**
 * Indicates that the Node takes Registers as input.
 */
public interface RegisterOpNode {
	
	public List<Reg> getOperandRegisters();
	public void updateRegisterNode(MidLoadNode oldNode, MidLoadNode newNode);

}
