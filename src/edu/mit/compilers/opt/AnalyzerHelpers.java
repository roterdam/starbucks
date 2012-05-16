package edu.mit.compilers.opt;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.nodes.MidCallNode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.memory.MidArrayElementNode;
import edu.mit.compilers.codegen.nodes.regops.MidArithmeticNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidNegNode;

public class AnalyzerHelpers {

	/**
	 * Deletes a node and all now-useless nodes before it. Assumes saveNode
	 * saves an arithmetic node.
	 */
	public static List<MidNode> completeDeleteBinary(MidSaveNode saveNode,
			Block block) {
		List<MidNode> deleted = new ArrayList<MidNode>();
		block.delete(saveNode);
		assert saveNode.getRegNode() instanceof MidArithmeticNode;
		MidArithmeticNode arithNode = (MidArithmeticNode) saveNode.getRegNode();
		LogCenter.debug("OPT", " --> DELETING binary" + arithNode);
		block.delete(arithNode);
		block.delete(arithNode.getLeftOperand());
		block.delete(arithNode.getRightOperand());

		deleted.add(arithNode.getLeftOperand());
		deleted.add(arithNode.getRightOperand());
		deleted.add(arithNode);
		deleted.add(saveNode);
		return deleted;
	}

	/**
	 * Deletes a node and all now-useless nodes before it. Assumes saveNode
	 * saves a neg node.
	 */
	public static List<MidNode> completeDeleteUnary(MidSaveNode saveNode,
			Block block) {
		List<MidNode> deleted = new ArrayList<MidNode>();
		block.delete(saveNode);
		assert saveNode.getRegNode() instanceof MidNegNode;
		MidNegNode negNode = (MidNegNode) saveNode.getRegNode();
		LogCenter.debug("OPT", " --> DELETING unary " + negNode);
		block.delete(negNode);
		block.delete(negNode.getOperand());

		deleted.add(negNode.getOperand());
		deleted.add(negNode);
		deleted.add(saveNode);
		return deleted;
	}

	public static List<MidNode> completeDeleteAssignment(MidSaveNode saveNode,
			Block block) {
		List<MidNode> deleted = new ArrayList<MidNode>();
		assert saveNode.getRegNode() instanceof MidLoadNode;

		MidLoadNode loadNode = (MidLoadNode) saveNode.getRegNode();

		// FIXME is this correct?
		if (loadNode.getMemoryNode() instanceof MidArrayElementNode) {
			block.delete(((MidArrayElementNode) loadNode.getMemoryNode())
					.getLoadNode());
		}

		LogCenter.debug("DCE", "DELETING ASSIGN " + saveNode.getRegNode());
		block.delete(saveNode.getRegNode());
		block.delete(saveNode);
		deleted.add(saveNode.getRegNode());
		deleted.add(saveNode);
		return deleted;
	}

	public static List<MidNode> completeDeleteMethodSave(MidSaveNode saveNode,
			Block block) {
		List<MidNode> deleted = new ArrayList<MidNode>();
		assert saveNode.getRegNode() instanceof MidCallNode;
		LogCenter.debug("DCE", "DELETEING SAVE NODE " + saveNode);
		block.delete(saveNode);
		MidCallNode callNode = (MidCallNode) saveNode.getRegNode();
		LogCenter.debug("DCE", "DISABLING " + callNode);
		callNode.disableSaveValue();
		deleted.add(callNode);
		deleted.add(saveNode);
		return deleted;
	}

	public static void completeReplaceBinary(MidSaveNode saveNode,
			MidNodeList replaceList) {
		MidNode insertBefore = saveNode.getNextNode();

		saveNode.delete();
		assert saveNode.getRegNode() instanceof MidArithmeticNode;
		MidArithmeticNode arithNode = (MidArithmeticNode) saveNode.getRegNode();
		LogCenter.debug("OPT", "DELETING " + arithNode);
		arithNode.delete();
		arithNode.getLeftOperand().delete();
		arithNode.getRightOperand().delete();

		MidNode insertAfter = insertBefore.getPrevNode();
		insertAfter.insertNodeListAfter(replaceList);
	}

	public static void completeReplaceUnary(MidSaveNode saveNode,
			MidNodeList replaceList) {
		MidNode insertBefore = saveNode.getNextNode();

		saveNode.delete();
		assert saveNode.getRegNode() instanceof MidNegNode;
		MidNegNode negNode = (MidNegNode) saveNode.getRegNode();
		LogCenter.debug("OPT", "DELETING " + negNode);
		negNode.delete();
		negNode.getOperand().delete();

		MidNode insertAfter = insertBefore.getPrevNode();
		insertAfter.insertNodeListAfter(replaceList);
	}
}