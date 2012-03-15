package edu.mit.compilers.codegen.nodes;

import edu.mit.compilers.crawler.VarType;

public class MidFieldArrayDeclNode extends MidFieldDeclNode {
	private long length;

	public MidFieldArrayDeclNode(String name, long length, VarType type) {
		super(name, type);
		this.length = length;
	}

	public long getLength() {
		return length;
	}
}