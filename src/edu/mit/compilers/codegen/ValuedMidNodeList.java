package edu.mit.compilers.codegen;

import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;

public class ValuedMidNodeList {
	MidNodeList nodeList;
	MidMemoryNode memoryNode;
	public ValuedMidNodeList(MidNodeList nodeList, MidMemoryNode memoryNode){
		this.nodeList = nodeList;
		this.memoryNode = memoryNode;
	}
	public MidNodeList getList(){
		return nodeList;
	}
	public MidMemoryNode getReturnNode(){
		return memoryNode;
	}
}
