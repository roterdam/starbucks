package edu.mit.compilers.codegen;

import java.util.LinkedList;

public class MidLevelNode {
	
	MidLevelNode prev;
	MidLevelNode next;
	LinkedList<MidLevelNode> children;
	
	public MidLevelNode(){
		this(null);
	}
	public MidLevelNode(MidLevelNode prev) {
		this.prev = prev;
		this.prev.setNext(this);
	}
	
	public void setNext(MidLevelNode next) {
		this.next = next;
	}

}
