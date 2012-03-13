package edu.mit.compilers.codegen.nodes;

public class MidFieldArrayDeclNode extends MidFieldDeclNode {
	private long length;

	public MidFieldArrayDeclNode(String name, MidVarType type, long length) {
		super(name, type);
		this.length = length;
	}

	public long getLength() {
		return length;
	}
}