package edu.mit.compilers.codegen.nodes;

import java.util.LinkedList;

import edu.mit.compilers.codegen.MidNodeList.MidNode;

// TODO: make abstract
public class MidNode {	
	MidNode nextNode;
	public void setNextNode(MidNode node){
		nextNode = node;
	}
	public MidNode getNextNode(){
		return nextNode;
	}
	
}
