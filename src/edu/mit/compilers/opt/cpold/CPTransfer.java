package edu.mit.compilers.opt.cpold;

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
 * Reaching definition analysis - set of related analyses. Only collects
 * information regarding what definitions are available at the beginning of a
 * block. The transformations are left up to the local analyzer.
 */
public class CPTransfer implements Transfer<CPGlobalState> {

	private ArrayList<MidNode> nodesOfInterest;

	@Override
	public CPGlobalState apply(Block b, CPGlobalState inState) {
		assert inState != null : "Input state should not be null.";

		this.nodesOfInterest = new ArrayList<MidNode>();
		LogCenter.debug("CP", "\n\n\nPROCESSING " + b
				+ ", THE GLOBAL STATE IS:\n##########\n" + inState);

		CPGlobalState outState = inState.clone();

		for (MidNode node : b) {
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

			} else if (node instanceof MidMethodCallNode
					&& !((MidMethodCallNode) node).isStarbucksCall()) {
				this.nodesOfInterest.add(node);
			}
		}

		for (MidNode assignmentNode : this.nodesOfInterest) {
			LogCenter.debug("CP", "\nProcessing " + assignmentNode);
			if (assignmentNode instanceof MidSaveNode) {
				MidSaveNode saveNode = (MidSaveNode) assignmentNode;
				processDefinition(outState, saveNode);
			} else if (assignmentNode instanceof MidMethodCallNode) {
				outState.reset();
			}
		}

		LogCenter.debug("CP", "FINAL STATE IS " + outState);
		LogCenter.debug("CP", "");

		return outState;
	}

	private void processDefinition(CPGlobalState outState, MidSaveNode saveNode) {
		// Track what real fields temp nodes correspond to.
		MidMemoryNode memNode = saveNode.getDestinationNode();
		if (saveNode.getRegNode() instanceof MidLoadNode) {
			MidLoadNode loadNode = (MidLoadNode) saveNode.getRegNode();
			MidMemoryNode refNode = loadNode.getMemoryNode();
			
			

		}
		outState.killReferences(memNode);
	}

}
