package edu.mit.compilers.opt;

import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;

public class Value {
	
	private MidMemoryNode node;

	public Value(MidMemoryNode node) {
		this.node = node;
	}
}
