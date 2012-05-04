package edu.mit.compilers.codegen.nodes.memory;

import java.util.List;

public interface MemoryUser {
	
	public List<MidMemoryNode> getUsedMemoryNodes();

}
