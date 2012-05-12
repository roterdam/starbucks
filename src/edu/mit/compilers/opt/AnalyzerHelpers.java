package edu.mit.compilers.opt;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.regops.MidArithmeticNode;
import edu.mit.compilers.codegen.nodes.regops.MidNegNode;



public class AnalyzerHelpers {

	/**
	 * Deletes a node and all now-useless nodes before it. Assumes saveNode
	 * saves an arithmetic node.
	 */
	public static void completeDeleteBinary(MidSaveNode saveNode) {
		saveNode.delete();
		assert saveNode.getRegNode() instanceof MidArithmeticNode;
		MidArithmeticNode arithNode = (MidArithmeticNode) saveNode.getRegNode();
		LogCenter.debug("OPT", "DELETING " + arithNode);
		arithNode.delete();
		arithNode.getLeftOperand().delete();
		arithNode.getRightOperand().delete();
	}

	/**
	 * Deletes a node and all now-useless nodes before it. Assumes saveNode
	 * saves a neg node.
	 */
	public static void completeDeleteUnary(MidSaveNode saveNode) {
		saveNode.delete();
		assert saveNode.getRegNode() instanceof MidNegNode;
		MidNegNode negNode = (MidNegNode) saveNode.getRegNode();
		LogCenter.debug("OPT", "DELETING " + negNode);
		negNode.delete();
		negNode.getOperand().delete();
	}
	
	

}