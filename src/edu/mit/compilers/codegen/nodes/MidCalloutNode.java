package edu.mit.compilers.codegen.nodes;

import java.util.List;

import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.opt.regalloc.LiveWebsActivist;

public class MidCalloutNode extends MidCallNode implements LiveWebsActivist{

	public MidCalloutNode(String name, List<MidMemoryNode> params) {
		super(name, params);
	}

}
