package edu.mit.compilers.codegen.nodes;


public class MidCalloutNode extends MidCallNode {

	public MidCalloutNode(String name, int paramCount) {
		super(name, paramCount);
	}

	@Override
	public boolean isStarbucksCall() {
		return false;
	}
	
}