package edu.mit.compilers.opt.cp;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidRegisterNode;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.LocalAnalyzer;

public class CPTransformer extends LocalAnalyzer<CPState> {

	@Override
	protected void transform(Block block, CPState state) {
		CPState localState;
		if (state == null) {
			localState = new CPState();
		} else {
			localState = state.clone();
		}

		LogCenter.debug("CP", "\n\nTransforming block:\n" + block + "\n State:\n"
				+ state);

		for (MidNode node : block) {
			if (node instanceof MidSaveNode
					&& ((MidSaveNode) node).savesRegister()) {
				MidSaveNode saveNode = (MidSaveNode) node;
				MidRegisterNode regNode = saveNode.getRegNode();
				if (regNode instanceof MidLoadNode) {
					MidLoadNode loadNode = (MidLoadNode) regNode;
					// Update definitions.
					localState.processDef(loadNode.getMemoryNode(), saveNode
							.getDestinationNode());
				}
			} else if (node instanceof MidLoadNode) {
				// See if we can optimize.
				MidMemoryNode memNode = ((MidLoadNode) node).getMemoryNode();
				memNode = localState.lookup(memNode);
				((MidLoadNode) node).updateMemoryNode(memNode, true);
			}
		}
	}

}
