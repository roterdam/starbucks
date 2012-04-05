package edu.mit.compilers.opt;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.jumpops.MidJumpNode;

public class Block {

	private MidNode head;
	private MidNode tail;
	private List<Block> predecessors;
	private List<Block> successors;

	public Block(MidNode h) {
		this.head = h;
		predecessors = new ArrayList<Block>();
		successors = new ArrayList<Block>();
	}

	public MidNode getHead() {
		return head;
	}

	public void setTail(MidNode t) {
		this.tail = t;
	}

	public MidNode getTail() {
		return tail;
	}

	void addPredecessor(Block b) {
		if (predecessors.contains(b)) {
			return;
		}
		predecessors.add(b);
	}

	/**
	 * Adds block as successor if it's not null.
	 */
	public void addSuccessor(Block b) {
		if (b == null || successors.contains(b)) {
			return;
		}
		successors.add(b);
		b.addPredecessor(this);
	}

	public static Block makeBlock(MidNode n) {
		if (n == null) {
			return null;
		}
		if (n instanceof MidJumpNode) {
			MidJumpNode jumpNode = (MidJumpNode) n;
			return makeBlock(jumpNode.getLabelNode());
		}
		Block b = new Block(n);
		MidNode lastNonJump = n;
		MidNode nextNode = n.getNextNode();
		while (!(nextNode == null || nextNode instanceof MidJumpNode)) {
			lastNonJump = nextNode;
			nextNode = lastNonJump.getNextNode();
		}
		b.setTail(lastNonJump);
		b.addSuccessor(makeBlock(nextNode));
		if (nextNode != null) {
			b.addSuccessor(makeBlock(nextNode.getNextNode()));
		}
		return b;
	}

}
