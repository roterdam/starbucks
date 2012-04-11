package edu.mit.compilers.opt.cse;

import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.regops.MidRegisterNode;

/**
 * Used to denote the saving of a temp variable through optimizations.
 */
public class OptSaveNode extends MidSaveNode {

	public OptSaveNode(MidRegisterNode refNode, MidMemoryNode dest) {
		super(refNode, dest);
	}

}
