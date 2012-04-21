package edu.mit.compilers.opt.cp;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.memory.MidTempDeclNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidRegisterNode;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.Transfer;

public class CPTransfer implements Transfer<CPState> {

	@Override
	public CPState apply(Block b, CPState s) {
		LogCenter.debug("[CP] APPLYING CP TRANSFER.");
		MidNode node = b.getHead();
		while (true) {
			if (node instanceof MidLoadNode) {
				process(s, (MidLoadNode) node);
			}
			if (node instanceof MidSaveNode
					&& ((MidSaveNode) node).savesRegister()) {
				process(s, (MidSaveNode) node);
			}
			if (node == b.getTail()) {
				break;
			}
			node = node.getNextNode();
		}

		return new CPState();
	}

	private void process(CPState s, MidLoadNode loadNode) {
		LogCenter.debug("[CP] Processing " + loadNode);
		// If it references a memory node, check to see if we can copy propagate an earlier one.
		if (loadNode.getMemoryNode() instanceof MidTempDeclNode) {
			MidTempDeclNode tempNode = (MidTempDeclNode) loadNode.getMemoryNode();
			MidMemoryNode tempReplacement = s.getReplacement(tempNode);
			if (tempReplacement != null) {
				loadNode.updateMemoryNode(tempReplacement);
			}
		}
	}

	private void process(CPState s, MidSaveNode saveNode) {
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
