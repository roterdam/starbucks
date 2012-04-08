package edu.mit.compilers.opt.cse;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;

/**
 * Note that this is a *symbolic* expression needed for GLOBAL CSE, since
 * global CSE is concerned with symbolic similarities, i.e.
 * if (b) { x = 5; c = x+y; } else { d = x+y; }
 * e = x+y;
 * Should be optimized by
 * if (b) { x = 5; c = x+y; t=c; } else { d = x+y; t=d; }
 * e = t;
 */
public class GlobalExpr {

	private final MidNode node;
	private final GlobalExpr left;
	private final GlobalExpr right;

	public GlobalExpr(MidNode node) {
		this.node = node;
		this.left = null;
		this.right = null;
	}

	public GlobalExpr(MidNode node, GlobalExpr left, GlobalExpr right,
			boolean isCommutative) {
		this.node = node;
		if (isCommutative) {
			if (left.toString().compareTo(right.toString()) <= 0) {
				this.left = left;
				this.right = right;
			} else {
				this.left = right;
				this.right = left;
			}
		} else {
			this.left = left;
			this.right = right;
		}
	}

	public String toString() {
		if (left != null && right != null) {
			return node.toString() + ":" + left.toString() + "," + right.toString();
		}
		return node.toString();
	}

	public List<MidMemoryNode> getMemoryNodes() {
		List<MidMemoryNode> nodes = new ArrayList<MidMemoryNode>();
		if(left != null && right != null){ // not a leaf
			nodes.addAll(left.getMemoryNodes());
			nodes.addAll(right.getMemoryNodes());
		}else {
			assert node instanceof MidMemoryNode : "Expected MidMemoryNodes. Found "+node.getClass();
			nodes.add((MidMemoryNode) node);
		}
		return nodes;
	}

}
