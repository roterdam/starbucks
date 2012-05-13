package edu.mit.compilers.opt.cpold;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.memory.MidArrayElementNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.memory.MidTempDeclNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidRegisterNode;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.Transformer;

public class CPTransformer extends Transformer<CPGlobalState> {

	@Override
	public void transform(Block b, CPGlobalState g) {
		CPGlobalState globalState;
		if (g == null) {
			globalState = new CPGlobalState();
		} else {
			globalState = g.clone();
		}
		CPLocalState s = new CPLocalState();
		LogCenter.debug("CPLOCAL", "APPLYING CP TRANSFORM ON BLOCK:");
		LogCenter.debug("CPLOCAL", "" + b);
		LogCenter.debug("CPLOCAL", "globalState: " + globalState);
		for (MidNode node : b) {
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
			} else if (node instanceof MidLoadNode) {
				// First check if we can reuse a constant.
				boolean replaced = processGlobal(globalState, (MidLoadNode) node);
				if (!replaced) {
					// Otherwise, see if we can reuse something locally.
					process(s, (MidLoadNode) node);
				}
			}
		}
	}

	// private void processArrayUse(CPGlobalState outState,
	// ArrayReferenceNode arrayNode) {
	// // Check if the register node used is a load node determined to be a
	// // constant.
	// MidArrayElementNode arrayElementNode = arrayNode
	// .getMidArrayElementNode();
	// MidLoadNode indexLoadNode = arrayElementNode.getLoadNode();
	// MidMemoryNode refNode = indexLoadNode.getMemoryNode();
	// MidMemoryNode lookedUpNode = outState.lookupDefinition(refNode);
	// if (lookedUpNode != null) {
	// refNode = lookedUpNode;
	// }
	// if (refNode instanceof MidConstantNode) {
	// arrayElementNode.setConstantNode((MidConstantNode) refNode);
	// }
	// }

	/**
	 * Processes a definition usage based on the global state. If it can reuse a
	 * definition, reuses it and returns true. Otherwise returns false;
	 * 
	 * @param global
	 * @param loadNode
	 * @return
	 */
	private boolean processGlobal(CPGlobalState global, MidLoadNode loadNode) {
		MidMemoryNode refNode = loadNode.getMemoryNode();
		MidMemoryNode lookedUpNode = global.lookupDefinition(refNode);
		if (lookedUpNode != null) {
			refNode = lookedUpNode;
			loadNode.updateMemoryNode(refNode, true);
			return true;
		}
		return false;
	}

	private void process(CPLocalState s, MidLoadNode loadNode) {
		LogCenter.debug("CP", "Processing " + loadNode);
		// If it references a memory node, check to see if we can copy propagate
		// an earlier one.
		LogCenter.debug("CP", "   "
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
		LogCenter.debug("CP", "Processing " + saveNode);
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
