package edu.mit.compilers.opt.cse;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;

public class LeafGlobalExpr extends GlobalExpr {

	protected MidMemoryNode memNode;
	public LeafGlobalExpr(MidMemoryNode memNode) {
		this.memNode = memNode;
	}
	
	@Override
	public String toString(){
		return memNode.toString();
	}
	
	@Override
	public List<MidMemoryNode> getMemoryNodes() {
		List<MidMemoryNode> nodes = new ArrayList<MidMemoryNode>();
		nodes.add(memNode);
		return nodes;
	}

	// FIXME Two tempvars that point to the same memory location should be equal?
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof LeafGlobalExpr)){
			return false;
		}
		return memNode.equals(((LeafGlobalExpr)o).memNode);
	}

	@Override
	public int hashCode() {
		return memNode.hashCode();
	}
}
