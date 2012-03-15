package edu.mit.compilers.codegen.nodes;

import edu.mit.compilers.crawler.VarType;

public class MidParamDeclNode extends MidMemoryNode {
	
	private String name;
	
	public MidParamDeclNode(String name, VarType type) {
		super(type);
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		return "<param: " + name + ">";
	}

}
