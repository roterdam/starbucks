package edu.mit.compilers.codegen.nodes;

import edu.mit.compilers.crawler.VarType;

public class MidMemoryNode extends MidNode {
	
	VarType type;

	public MidMemoryNode(VarType type) {
		super();
		this.type = type;
	}
	
	public VarType getType() {
		return type;
	}

}