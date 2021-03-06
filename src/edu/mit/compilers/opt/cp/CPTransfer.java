package edu.mit.compilers.opt.cp;

import edu.mit.compilers.codegen.nodes.MidCallNode;
import edu.mit.compilers.codegen.nodes.MidMethodCallNode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.memory.MidArrayElementNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidRegisterNode;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.Transfer;

/**
 * Reaching definition analysis - set of related analyses. Only collects
 * information regarding what definitions are available at the beginning of a
 * block. The transformations are left up to the local analyzer.
 */
public class CPTransfer implements Transfer<CPState> {

	@Override
	public CPState apply(Block b, CPState s) {

		CPState outState = s.clone();

		for (MidNode node : b) {
			if (node instanceof MidSaveNode
					&& ((MidSaveNode) node).savesRegister()) {
				MidSaveNode saveNode = (MidSaveNode) node;
				// TODO: We skip optimizing array access saves for now.
				if (saveNode.getRegNode() instanceof MidLoadNode) {
					MidLoadNode loadNode = (MidLoadNode) saveNode.getRegNode();
					if (loadNode.getMemoryNode() instanceof MidArrayElementNode) {
						continue;
					}
				}

				MidRegisterNode regNode = saveNode.getRegNode();
				MidMemoryNode destNode = saveNode.getDestinationNode();
				// Kill definitions.
				outState.killReferences(destNode);
				if (regNode instanceof MidLoadNode) {
					// Process save node if it just saves a load node.
					MidLoadNode loadNode = (MidLoadNode) regNode;
					outState.processDef(loadNode.getMemoryNode(), destNode);
				}

			} else if (node instanceof MidCallNode) {
				if (node instanceof MidMethodCallNode
						&& ((MidMethodCallNode) node).isStarbucksCall()) {
					continue;
				}
				outState.reset();
			}
		}
		return outState;
	}

}
