package edu.mit.compilers.opt.cp;

import java.util.ArrayList;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.MidMethodCallNode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.memory.MidArrayElementNode;
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
				MidSaveNode saveNode = (MidSaveNode) node;
				// Skip optimizing array access saves for now.
				// TODO: perhaps it's optimizeable? Same in CPLocalAnalyzer.
				boolean skip = false;
				if (saveNode.getRegNode() instanceof MidLoadNode) {
					MidLoadNode loadNode = (MidLoadNode) saveNode.getRegNode();
					if (loadNode.getMemoryNode() instanceof MidArrayElementNode) {
						skip = true;
					}
				}
				if (!skip) {
					this.nodesOfInterest.add(node);
				}

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

		// Clear aliases from temps.
		// If the same temp is used outside this block (probably due to CSE) we
		// don't want it to be converted to what it refers to in this block.
		//
		// i.e. if you have c=a+b; if(z) { d=a+b; } e=a+b;
		// Then global CSE will have c=a+b; t1=c; if (z) { d=a+b;t1=d } e=t1;
		// And we don't want e=t1 to be translated to e=c.
		//
		// One might say that t1=d should clear the reference to t1. However,
		// it's actually a different temp var - global CSE only links them to
		// the same reference later.

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
		loadNode.updateMemoryNode(refNode, true);
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
