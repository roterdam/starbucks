package edu.mit.compilers.opt.cp;

import java.util.ArrayList;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.MidMethodCallNode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.memory.MidTempDeclNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.Transfer;

/**
 * Reaching definition analysis - set of related analyses.
 */
public class CPTransfer implements Transfer<CPGlobalState> {

	private ArrayList<MidNode> nodesOfInterest;

	@Override
	public CPGlobalState apply(Block b, CPGlobalState inState) {
		assert inState != null : "Input state should not be null.";

		this.nodesOfInterest = new ArrayList<MidNode>();
		LogCenter.debug("[CP]\n[CP]\n[CP]\n[CP] PROCESSING " + b
				+ ", THE GLOBAL STATE IS:\n[CP] ##########\n[CP] " + inState);

		CPGlobalState outState = inState.clone();

		MidNode node = b.getHead();
		while (true) {
			if (node instanceof MidSaveNode
					&& ((MidSaveNode) node).savesRegister()) {
				this.nodesOfInterest.add(node);
			} else if (node instanceof MidLoadNode) {
				this.nodesOfInterest.add(node);
			} else if (node instanceof MidMethodCallNode
					&& !((MidMethodCallNode) node).isStarbucksCall()) {
				this.nodesOfInterest.add(node);
			}
			if (node == b.getTail()) {
				break;
			}
			node = node.getNextNode();
		}

		for (MidNode assignmentNode : this.nodesOfInterest) {
			LogCenter.debug("[CP]\n[CP] Processing " + assignmentNode);
			if (assignmentNode instanceof MidSaveNode) {
				MidSaveNode saveNode = (MidSaveNode) assignmentNode;
				processDefinition(outState, saveNode);
			} else if (assignmentNode instanceof MidLoadNode) {
				MidLoadNode loadNode = (MidLoadNode) assignmentNode;
				processUse(outState, loadNode);
			} else if (assignmentNode instanceof MidMethodCallNode) {
				outState.reset();
			}
		}

		LogCenter.debug("[CP] FINAL STATE IS " + outState);
		LogCenter.debug("[CP]");

		return outState;
	}

	private void processUse(CPGlobalState outState, MidLoadNode loadNode) {
		MidMemoryNode refNode = loadNode.getMemoryNode();
		if (refNode instanceof MidTempDeclNode) {
			refNode = outState.lookupAlias((MidTempDeclNode) refNode);
		}
		if (refNode == null) {
			return;
		}
		MidMemoryNode lookedUpNode = outState.lookupDefinition(refNode);
		if (lookedUpNode != null) {
			refNode = lookedUpNode;
		}
		loadNode.updateMemoryNode(refNode);

	}

	private void processDefinition(CPGlobalState outState, MidSaveNode saveNode) {
		// Track what real fields temp nodes correspond to.
		MidMemoryNode memNode = saveNode.getDestinationNode();
		if (saveNode.getRegNode() instanceof MidLoadNode) {
			MidLoadNode loadNode = (MidLoadNode) saveNode.getRegNode();
			MidMemoryNode refNode = loadNode.getMemoryNode();

			if (saveNode.getDestinationNode() instanceof MidTempDeclNode) {
				MidTempDeclNode tempNode = (MidTempDeclNode) memNode;
				outState.saveAlias(tempNode, refNode);
			} else {
				outState.saveDefinition(memNode, refNode);
			}

		}

		outState.killReferences(memNode);
	}

}
