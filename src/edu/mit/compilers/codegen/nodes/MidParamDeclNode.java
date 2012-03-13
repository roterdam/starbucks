package edu.mit.compilers.codegen.nodes;

public class MidParamDeclNode extends MidVarDeclNode {
	
	private String name;
	
	public MidParamDeclNode(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public String toString() {
		return "<param: " + name + ">";
	}

}
