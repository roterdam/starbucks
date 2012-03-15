package edu.mit.compilers.codegen.nodes;

public class MidLabelNode extends MidNode {
	
	private final String name;
	
	public MidLabelNode(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return "<" + getNodeClass() + ":" + getName()+">";
	}
}
