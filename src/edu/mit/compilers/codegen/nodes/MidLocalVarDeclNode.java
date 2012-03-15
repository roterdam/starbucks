package edu.mit.compilers.codegen.nodes;

import edu.mit.compilers.crawler.VarType;

public class MidLocalVarDeclNode extends MidMemoryNode {
	private String name;
	
	public MidLocalVarDeclNode(String name, VarType type) {
		super(type);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		String className = getClass().getName();
		int mid = className.lastIndexOf('.') + 1;
		return "<" + className.substring(mid) + ": " + getName() + ">";
	}
}