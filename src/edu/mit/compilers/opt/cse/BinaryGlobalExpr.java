package edu.mit.compilers.opt.cse;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.regops.MidArithmeticNode;

public class BinaryGlobalExpr extends GlobalExpr {

	protected final MidNode node;
	protected final GlobalExpr left;
	protected final GlobalExpr right;

	public BinaryGlobalExpr(MidArithmeticNode node, GlobalExpr left,
			GlobalExpr right, boolean isCommutative) {
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
		return node.toString() + ":" + left.toString() + "," + right.toString();
	}

	public List<MidMemoryNode> getMemoryNodes() {
		List<MidMemoryNode> nodes = new ArrayList<MidMemoryNode>();
		if (left != null && right != null) { // not a leaf
			nodes.addAll(left.getMemoryNodes());
			nodes.addAll(right.getMemoryNodes());
		} else {
			assert node instanceof MidMemoryNode : "Expected MidMemoryNodes. Found "
					+ node.getClass();
			nodes.add((MidMemoryNode) node);
		}
		return nodes;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof BinaryGlobalExpr)) {
			return false;
		}
		GlobalExpr oLeft = ((BinaryGlobalExpr) o).left;
		GlobalExpr oRight = ((BinaryGlobalExpr) o).right;
		MidNode oNode = ((BinaryGlobalExpr) o).node;

		return oLeft.equals(left) && oRight.equals(right)
				&& node.getNodeClass().equals(oNode.getNodeClass());
	}

	@Override
	public int hashCode() {
		return node.hashCode() + left.hashCode() + right.hashCode();
	}

}
