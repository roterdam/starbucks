package edu.mit.compilers.codegen.nodes.regops;

import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;

/**
 * Loads value from memory.
 */
public interface MidUseNode {
	
	public MidMemoryNode getMemoryNode();
	
}
