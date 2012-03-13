package edu.mit.compilers.codegen.nodes;

public class MidMethodDeclNode extends MidNode {
	private String name;

	public MidMethodDeclNode(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
