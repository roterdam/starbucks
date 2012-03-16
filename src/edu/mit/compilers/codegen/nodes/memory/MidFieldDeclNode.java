package edu.mit.compilers.codegen.nodes.memory;

public class MidFieldDeclNode extends MidMemoryNode {
	private String name;

	// TODO: When we get to implementing space allocation, array subclass knows
	// to do it differently.

	public MidFieldDeclNode(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public String toString() {
		String className = getClass().getName();
		int mid = className.lastIndexOf('.') + 1;
		return "<" + className.substring(mid) + ": " + name + ">";
	}

	@Override
	public String getFormattedLocationReference() {
		return rawLocationReference;
	}
	
}