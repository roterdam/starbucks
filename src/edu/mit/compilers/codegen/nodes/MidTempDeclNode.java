package edu.mit.compilers.codegen.nodes;

import edu.mit.compilers.codegen.MidLabelManager;

public class MidTempDeclNode extends MidMemoryNode {
	private String name;

	static int nodeNum = 0;

	public MidTempDeclNode() {
		super();
		// generate a random temp id
		this.name = "t" + MidLabelManager.getNewId();
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