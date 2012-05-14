package edu.mit.compilers.opt.cp;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.Main;
import edu.mit.compilers.codegen.nodes.MidCallNode;
import edu.mit.compilers.codegen.nodes.MidMethodCallNode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.memory.MidArrayElementNode;
import edu.mit.compilers.codegen.nodes.memory.MidConstantNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidRegisterNode;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.Transformer;

public class CPTransformer extends Transformer<CPState> {

	@Override
	protected void transform(Block block, CPState state) {
		CPState localState;
		if (state == null) {
			localState = new CPState();
		} else {
			localState = state.clone();
		}

		LogCenter.debug("CP", "\n\nTransforming block:\n" + block
				+ "\n State:\n" + state);

		for (MidNode node : block) {
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

				MidMemoryNode destNode = saveNode.getDestinationNode();
				if (destNode instanceof MidArrayElementNode) {
					processArrayElement((MidArrayElementNode) destNode);
				} else {
					localState.killReferences(destNode);
					MidRegisterNode regNode = saveNode.getRegNode();
					if (regNode instanceof MidLoadNode) {
						MidLoadNode loadNode = (MidLoadNode) regNode;
						// Update definitions.
						localState
								.processDef(loadNode.getMemoryNode(), destNode);
					}
				}
			} else if (node instanceof MidLoadNode) {
				// See if we can optimize.
				MidLoadNode loadNode = (MidLoadNode) node;
				MidMemoryNode memNode = loadNode.getMemoryNode();
				MidMemoryNode replacementNode;

				if (memNode instanceof MidArrayElementNode) {
					processArrayElement((MidArrayElementNode) memNode);
				} else {
					replacementNode = localState.lookup(memNode);
					if (replacementNode != memNode) {
						loadNode.updateMemoryNode(replacementNode, true);
						Main.setHasAdditionalChanges();
					}
				}

			} else if (node instanceof MidCallNode) {
				if (node instanceof MidMethodCallNode
						&& ((MidMethodCallNode) node).isStarbucksCall()) {
					continue;
				}
				LogCenter.debug("CP", "Resetting state because of "
						+ ((MidCallNode) node).getName());
				localState.reset();
			}
		}
	}

	private void processArrayElement(MidArrayElementNode arrayElementNode) {
		MidLoadNode loadNode = arrayElementNode.getLoadNode();
		MidMemoryNode memNode = loadNode.getMemoryNode();
		LogCenter.debug("CPJ", "Looking at " + memNode + " ("
				+ memNode.isConstant() + ")");
		if (memNode.isConstant()) {
			// Remove register operation and use a constant instead.
			loadNode.delete();
			arrayElementNode.setConstantNode((MidConstantNode) memNode);
		}
	}

}
