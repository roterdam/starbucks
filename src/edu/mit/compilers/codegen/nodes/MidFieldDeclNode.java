package edu.mit.compilers.codegen.nodes;

public class MidFieldDeclNode extends MidVarDeclNode {
	private String name;
	private MidVarType type;

	// TODO: When we get to implementing space allocation, array subclass knows
	// to do it differently. Think Differently¨.

	public MidFieldDeclNode(String name, MidVarType type) {
		super();
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public MidVarType getType() {
		return type;
	}
}
