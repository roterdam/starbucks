package edu.mit.compilers.codegen.nodes;

public class MidTempDeclNode extends MidMemoryNode {
	private String name;
	
	static int nodeNum = 0;
	
	public MidTempDeclNode() {
		super();
		//generate a random temp id
		this.name = "temp"+Integer.toString(nodeNum++);
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