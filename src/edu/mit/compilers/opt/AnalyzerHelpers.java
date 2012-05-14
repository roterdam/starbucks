package edu.mit.compilers.opt;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.regops.MidArithmeticNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidNegNode;

public class AnalyzerHelpers {

	/**
	 * Deletes a node and all now-useless nodes before it. Assumes saveNode
	 * saves an arithmetic node.
	 */
	public static void completeDeleteBinary(MidSaveNode saveNode, Block block) {
		block.delete(saveNode);
		assert saveNode.getRegNode() instanceof MidArithmeticNode;
		MidArithmeticNode arithNode = (MidArithmeticNode) saveNode.getRegNode();
		LogCenter.debug("OPT", "DELETING " + arithNode);
		block.delete(arithNode);
		block.delete(arithNode.getLeftOperand());
		block.delete(arithNode.getRightOperand());
	}

	/**
	 * Deletes a node and all now-useless nodes before it. Assumes saveNode
	 * saves a neg node.
	 */
	public static void completeDeleteUnary(MidSaveNode saveNode, Block block) {
		block.delete(saveNode);
		assert saveNode.getRegNode() instanceof MidNegNode;
		MidNegNode negNode = (MidNegNode) saveNode.getRegNode();
		LogCenter.debug("OPT", "DELETING " + negNode);
		block.delete(negNode);
		block.delete(negNode.getOperand());
	}

	public static void completeDeleteAssignment(MidSaveNode saveNode,
			Block block) {
		assert saveNode.getRegNode() instanceof MidLoadNode;
		block.delete(saveNode.getRegNode());
		block.delete(saveNode);
	}

}