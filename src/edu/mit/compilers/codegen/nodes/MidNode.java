package edu.mit.compilers.codegen.nodes;


// TODO: make abstract
public class MidNode {
	MidNode nextNode;

	public void setNextNode(MidNode node) {
		nextNode = node;
	}

	public MidNode getNextNode() {
		return nextNode;
	}
	
	public String toString() {
		return "<node>";
	}

}
