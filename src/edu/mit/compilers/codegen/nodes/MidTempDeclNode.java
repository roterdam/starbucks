package edu.mit.compilers.codegen.nodes;

public class MidTempDeclNode extends MidMemoryNode {
	private String name;
	
	public MidTempDeclNode() {
		super();
		//generate a random temp id
		this.name = Integer.toString(this.hashCode());
	}

	public String getName() {
		return name;
	}

	public String toString() {
		String className = getClass().getName();
		int mid = className.lastIndexOf('.') + 1;
		return "<" + className.substring(mid) + ": " + name + " >";
	}
}