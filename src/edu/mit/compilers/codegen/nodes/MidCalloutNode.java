package edu.mit.compilers.codegen.nodes;

import java.util.List;

import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;

public class MidCalloutNode extends MidCallNode {

	//FIXME: get rid of params
	public MidCalloutNode(String name, List<MidMemoryNode> params) {
		super(name, params);
	}
	
	public MidCalloutNode(String name) {
		super(name, null);
	}

}