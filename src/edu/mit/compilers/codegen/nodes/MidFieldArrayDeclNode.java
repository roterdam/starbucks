package edu.mit.compilers.codegen.nodes;

import edu.mit.compilers.codegen.nodes.memory.MidFieldDeclNode;

public class MidFieldArrayDeclNode extends MidFieldDeclNode {
	private long length;

	public MidFieldArrayDeclNode(String name, long length) {
		super(name);
		this.length = length;
	}

	public long getLength() {
		return length;
	}
}