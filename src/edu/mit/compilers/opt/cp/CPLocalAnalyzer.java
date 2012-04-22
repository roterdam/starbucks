package edu.mit.compilers.opt.cp;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.memory.MidArrayElementNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.memory.MidTempDeclNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidRegisterNode;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.LocalAnalyzer;

public class CPLocalAnalyzer extends LocalAnalyzer {

	@Override
	public void transform(Block b) {
		CPLocalState s = new CPLocalState();
		LogCenter.debug("[CP] APPLYING CP TRANSFER.");
		MidNode node = b.getHead();
		while (true) {
			if (node instanceof MidLoadNode) {
				process(s, (MidLoadNode) node);
			}
			if (node instanceof MidSaveNode
					&& ((MidSaveNode) node).savesRegister()) {
				MidSaveNode saveNode = (MidSaveNode) node;
				// Skip optimizing array access saves for now.
				// TODO: perhaps it's optimizeable? Same in CPTransfer.
				boolean skip = false;
				if (saveNode.getRegNode() instanceof MidLoadNode) {
					MidLoadNode loadNode = (MidLoadNode) saveNode.getRegNode();
					if (loadNode.getMemoryNode() instanceof MidArrayElementNode) {
						skip = true;
					}
				}
				if (!skip) {
					process(s, (MidSaveNode) node);
				}
			}
			if (node == b.getTail()) {
				break;
			}
			node = node.getNextNode();
		}
	}

	private void process(CPLocalState s, MidLoadNode loadNode) {
		LogCenter.debug("[CP] Processing " + loadNode);
		// If it references a memory node, check to see if we can copy propagate
		// an earlier one.
		LogCenter.debug("[CP]    "
				+ (loadNode.getMemoryNode() instanceof MidTempDeclNode));
		if (loadNode.getMemoryNode() instanceof MidTempDeclNode) {
			MidTempDeclNode tempNode = (MidTempDeclNode) loadNode
					.getMemoryNode();
			MidMemoryNode tempReplacement = s.getReplacement(tempNode);
			if (tempReplacement != null) {
				loadNode.updateMemoryNode(tempReplacement, true);
			}
		}
	}

	private void process(CPLocalState s, MidSaveNode saveNode) {
		LogCenter.debug("[CP] Processing " + saveNode);
		if (saveNode.getDestinationNode() instanceof MidTempDeclNode) {
			// If it's a temp node, record what it's saving.
			MidTempDeclNode tempDestination = (MidTempDeclNode) saveNode
					.getDestinationNode();
			// Only process save nodes that simply copy memory values, not
			// expressions.
			MidRegisterNode regNode = saveNode.getRegNode();
			if (!(regNode instanceof MidLoadNode)) {
				return;
			}
			MidLoadNode loadNode = (MidLoadNode) regNode;
			s.putTempReference(tempDestination, loadNode.getMemoryNode());
		}
		// Kill references.
		s.killReferences(saveNode.getDestinationNode());
	}

}
