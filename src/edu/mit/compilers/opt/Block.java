package edu.mit.compilers.opt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.jumpops.MidJumpNode;

public class Block {

	private MidNode head;
	private MidNode tail;
	private List<Block> predecessors;
	private List<Block> successors;
	private String s;

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

	public List<Block> getPredecessors() {
		return predecessors;
	}

	public List<Block> getSuccessors() {
		return successors;
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

	private static Map<MidNode, Block> blockCache = new HashMap<MidNode, Block>();

	public static Block makeBlock(MidNode n) {
		if (n == null) {
			return null;
		}
		if (n instanceof MidJumpNode) {
			MidJumpNode jumpNode = (MidJumpNode) n;
			return makeBlock(jumpNode.getLabelNode());
		}
		if (blockCache.containsKey(n)) {
			return blockCache.get(n);
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
		blockCache.put(n, b);
		return b;
	}

	public static List<Block> getAllBlocks(MidNodeList nodeList) {
		blockCache.clear();
		// Make block will recursively make all the blocks and save them into
		// the block cache.
		makeBlock(nodeList.getHead());
		return new ArrayList<Block>(blockCache.values());
	}

}
