package edu.mit.compilers.codegen.nodes;

public class MidParamDeclNode extends MidMemoryNode {
	
	public MidParamDeclNode(String name) {
		super(name);
	}

	@Override
	public String toString() {
		return "<param: " + this.getName() + ">";
	}

}
