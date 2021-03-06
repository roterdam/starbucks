package edu.mit.compilers.codegen.nodes.memory;

import edu.mit.compilers.codegen.Reg;

/**
 * Indicates it can possibly access an array element, in which case it needs to
 * deallocate the register for array index calculation.
 */
public interface ArrayReferenceNode {
	
	public boolean usesArrayRegister();
	public MidArrayElementNode getMidArrayElementNode();
	public Reg getArrayRegister();

}
