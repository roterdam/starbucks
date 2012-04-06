package edu.mit.compilers.opt;

import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;

public class Temp extends MidNode {
	
	private MidMemoryNode node;

	public Temp(MidMemoryNode node) {
		this.node = node;
	}
	
}
