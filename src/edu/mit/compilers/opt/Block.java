package edu.mit.compilers.opt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.nodes.MidLabelNode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.jumpops.MidJumpNode;

public class Block implements Iterable<MidNode> {

	private MidNode head;
	private MidNode tail;
	private List<Block> predecessors;
	private List<Block> successors;
	private int blockNum;

	public Block(MidNode h, int blockNum) {
		this.head = h;
		this.blockNum = blockNum;
		predecessors = new ArrayList<Block>();
		successors = new ArrayList<Block>();
	}

	private MidNode getHead() {
		return head;
	}

	public void setTail(MidNode t) {
		this.tail = t;
	}

	private MidNode getTail() {
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

	public int getBlockNum() {
		return blockNum;
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

	public String toString() {
		String out = "B" + blockNum + "[" + getHead() + "]";
		MidNode node = getHead();
		while (true) {
			out += "\n  " + node;
			if (node == getTail()) {
				break;
			}
			node = node.getNextNode();
		}
		return out;
	}

	public static String recursiveToString(Block b, List<Block> visited,
			int indent) {
		String out = b.getBlockNum() + " [" + b.getHead() + "]";
		visited.add(b);
		for (Block s : b.getSuccessors()) {
			out += "\n";
			for (int i = 0; i < indent; i++) {
				out += " ";
			}
			if (visited.contains(s)) {
				out += "-> " + s.getBlockNum() + " [" + s.getHead() + "]";
			} else {
				out += "-> " + recursiveToString(s, visited, indent + 2);
			}
		}
		return out;
	}

	private static Map<MidNode, Block> blockCache = new LinkedHashMap<MidNode, Block>();
	public static int blockNumCounter = 0;

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
		LogCenter.debug("OPT", "BLOCK: makeBlock " + n);
		Block b = new Block(n, blockNumCounter++);
		blockCache.put(n, b);
		MidNode lastNonJumpLabel = n;
		MidNode nextNode = n.getNextNode();
		while (!(nextNode == null || nextNode instanceof MidJumpNode || nextNode instanceof MidLabelNode)) {
			lastNonJumpLabel = nextNode;
			nextNode = lastNonJumpLabel.getNextNode();
		}
		b.setTail(lastNonJumpLabel);
		Block newSuc = makeBlock(nextNode);
		if (newSuc != null) {
			b.addSuccessor(newSuc);
			LogCenter.debug("OPT", "Connecting " + b.getHead() + " to "
					+ newSuc.getHead());
		}
		if (nextNode != null && nextNode instanceof MidJumpNode
				&& ((MidJumpNode) nextNode).isConditional()) {
			Block secondSuc = makeBlock(nextNode.getNextNode());
			if (secondSuc != null) {
				b.addSuccessor(secondSuc);
				LogCenter.debug("OPT", "Connecting " + b.getHead() + " to "
						+ secondSuc.getHead());
			}
		}
		return b;
	}

	public static List<Block> getAllBlocks(MidNodeList nodeList) {
		blockCache.clear();
		// Make block will recursively make all the blocks and save them into
		// the block cache.
		LogCenter.debug("OPT", "BLOCK: Starting getAllBlocks with "
				+ nodeList.getHead());
		Block head = makeBlock(nodeList.getHead());
		List<Block> out = new ArrayList<Block>(blockCache.values());
		// Force head to the beginning.
		out.remove(head);
		out.add(0, head);
		return out;
	}

	@Override
	public Iterator<MidNode> iterator() {
		return new Iterator<MidNode>() {

			private MidNode curNode = Block.this.getHead();

			@Override
			public boolean hasNext() {
				return !(curNode == Block.this.getTail());
			}

			@Override
			public MidNode next() {
				MidNode out = curNode;
				curNode = curNode.getNextNode();
				return out;
			}

			@Override
			public void remove() {
				throw new NotImplementedException();
			}

		};
	}

	public Iterable<MidNode> reverse() {
		return new Iterable<MidNode>() {

			@Override
			public Iterator<MidNode> iterator() {
				return new Iterator<MidNode>() {
					private MidNode curNode = Block.this.getTail();

					@Override
					public boolean hasNext() {
						return !(curNode == Block.this.getHead());
					}

					@Override
					public MidNode next() {
						MidNode out = curNode;
						curNode = curNode.getPrevNode();
						return out;
					}

					@Override
					public void remove() {
						throw new NotImplementedException();
					}
				};

			}

		};
	}
}
