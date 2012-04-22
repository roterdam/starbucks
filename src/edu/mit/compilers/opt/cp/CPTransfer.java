package edu.mit.compilers.opt.cp;

import java.util.ArrayList;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.MidMethodCallNode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.Transfer;

public class CPTransfer implements Transfer<CPGlobalState> {

	private ArrayList<MidNode> assignments;

	@Override
	public CPGlobalState apply(Block b, CPGlobalState inState) {
		assert inState != null : "Input state should not be null.";

		this.assignments = new ArrayList<MidNode>();
		LogCenter.debug("[OPT]\n[OPT]\n[OPT]\n[OPT] PROCESSING " + b
				+ ", THE GLOBAL STATE IS:\n[OPT] ##########\n[OPT] " + inState);

		CPGlobalState outState = inState.clone();

		MidNode node = b.getHead();
		while (true) {
			if (node instanceof MidSaveNode
					&& ((MidSaveNode) node).savesRegister()) {
				this.assignments.add(node);
			} else if (node instanceof MidMethodCallNode
					&& !((MidMethodCallNode) node).isStarbucksCall()) {
				this.assignments.add(node);
			}
			if (node == b.getTail()) {
				break;
			}
			node = node.getNextNode();
		}

		for (MidNode assignmentNode : this.assignments) {
			LogCenter.debug("[OPT]\n[OPT] Processing " + assignmentNode);
//			if (assignmentNode instanceof MidSaveNode) {
//				if (assignmentNode instanceof OptSaveNode) {
//					continue;
//				}
//				MidSaveNode saveNode = (MidSaveNode) assignmentNode;
//				// a = x
//				if (saveNode.getRegNode() instanceof MidLoadNode) {
//					processSimpleAssignment(saveNode, inState, outState);
//				}
//				// a = -x
//				if (saveNode.getRegNode() instanceof MidNegNode) {
//					processUnaryAssignment(saveNode, inState, outState);
//				}
//				// a = x + y
//				if (saveNode.getRegNode() instanceof MidArithmeticNode) {
//					processArithmeticAssignment(saveNode, inState, outState);
//				}
//			} else if (assignmentNode instanceof MidMethodCallNode) {
//				MidMethodCallNode methodNode = (MidMethodCallNode) assignmentNode;
//				LogCenter.debug(inState.getReferenceMap().toString());
//				processMethodCall(methodNode, inState, outState);
//			}
		}

		LogCenter.debug("[OPT] FINAL STATE IS " + outState);
		LogCenter.debug("[OPT]");

		return outState;
	}

}
