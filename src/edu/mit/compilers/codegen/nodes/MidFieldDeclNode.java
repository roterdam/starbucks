package edu.mit.compilers.codegen.nodes;

public class MidFieldDeclNode extends MidVarDeclNode {
	private String name;
	private MidVarType type;
	
	public MidFieldDeclNode(String name, MidVarType type){
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
