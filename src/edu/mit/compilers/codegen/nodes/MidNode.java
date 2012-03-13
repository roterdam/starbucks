package edu.mit.compilers.codegen.nodes;

import java.util.LinkedList;

// TODO: make abstract
public class MidNode {
	
	MidNode prev;
	MidNode next;
	LinkedList<MidNode> children;
	
	public MidNode(){
		this(null);
	}
	public MidNode(MidNode prev) {
		this.prev = prev;
		this.prev.setNext(this);
	}
	
	public void setNext(MidNode next) {
		this.next = next;
	}

}
