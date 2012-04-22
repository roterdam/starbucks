package edu.mit.compilers.opt.cse;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.MidMethodCallNode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.memory.MidTempDeclNode;
import edu.mit.compilers.codegen.nodes.regops.MidArithmeticNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidNegNode;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.Transfer;

public class CSETransfer implements Transfer<CSEGlobalState> {

	ArrayList<MidNode> assignments;

	@Override
	public CSEGlobalState apply(Block b, CSEGlobalState inState) {
		assert inState != null : "Input state should not be null.";

		this.assignments = new ArrayList<MidNode>();
		LogCenter.debug("[OPT]\n[OPT]\n[OPT]\n[OPT] PROCESSING " + b
				+ ", THE GLOBAL STATE IS:\n[OPT] ##########\n[OPT] " + inState);

		CSEGlobalState outState = inState.clone();

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
			if (assignmentNode instanceof MidSaveNode) {
				if (assignmentNode instanceof OptSaveNode) {
					continue;
				}
				MidSaveNode saveNode = (MidSaveNode) assignmentNode;
				// a = x
				if (saveNode.getRegNode() instanceof MidLoadNode) {
					processSimpleAssignment(saveNode, inState, outState);
				}
				// a = -x
				if (saveNode.getRegNode() instanceof MidNegNode) {
					processUnaryAssignment(saveNode, inState, outState);
				}
				// a = x + y
				if (saveNode.getRegNode() instanceof MidArithmeticNode) {
					processArithmeticAssignment(saveNode, inState, outState);
				}
			} else if (assignmentNode instanceof MidMethodCallNode) {
				MidMethodCallNode methodNode = (MidMethodCallNode) assignmentNode;
				LogCenter.debug(inState.getReferenceMap().toString());
				processMethodCall(methodNode, inState, outState);
			}
		}

		LogCenter.debug("[OPT] FINAL STATE IS " + outState);
		LogCenter.debug("[OPT]");

		return outState;
	}

	private void processMethodCall(MidMethodCallNode methodNode,
			CSEGlobalState internalState, CSEGlobalState outputState) {
		outputState.clear();
	}

	private void processSimpleAssignment(MidSaveNode node,
			CSEGlobalState internalState, CSEGlobalState outputState) {

		MidLoadNode loadNode = (MidLoadNode) node.getRegNode();

		if (!(loadNode.getMemoryNode() instanceof MidTempDeclNode)) {
			GlobalExpr expr = new LeafGlobalExpr(loadNode.getMemoryNode());
			outputState.genReference(node.getDestinationNode(), expr);
		}
		outputState.killReferences(node.getDestinationNode());
		internalState.killReferences(node.getDestinationNode());
	}

	private void processUnaryAssignment(MidSaveNode saveNode,
			CSEGlobalState internalState, CSEGlobalState outputState) {
		MidNegNode r = (MidNegNode) saveNode.getRegNode();

		GlobalExpr expr = new UnaryGlobalExpr(r, new LeafGlobalExpr(r
				.getOperand().getMemoryNode()));

		List<MidMemoryNode> reusableReferences = internalState
				.getReferences(expr);
		if (reusableReferences.size() > 0) {
			// If there's a reusable reference, reuse it!
			// TODO: are we sure we just take the first one?
			MidMemoryNode ref = reusableReferences.get(0);
			LogCenter.debug("[OPT] HALLELUJAH OPTIMIZING GLOBAL CSE, reusing "
					+ ref + " -> " + expr);
			MidLoadNode loadTempNode = new MidLoadNode(ref);
			MidSaveNode newSaveNode = new MidSaveNode(loadTempNode,
					saveNode.getDestinationNode());
			newSaveNode.isOptimization = true;
			loadTempNode.insertAfter(saveNode);
			newSaveNode.insertAfter(loadTempNode);
			CSELocalAnalyzer.completeDeleteUnary(saveNode);

		}

		// Save reference in global CSE.
		outputState.genReference(saveNode.getDestinationNode(), expr);
		outputState.killReferences(saveNode.getDestinationNode());
		internalState.killReferences(saveNode.getDestinationNode());

	}

	private void processArithmeticAssignment(MidSaveNode saveNode,
			CSEGlobalState internalState, CSEGlobalState outputState) {
		MidArithmeticNode r = (MidArithmeticNode) saveNode.getRegNode();

		GlobalExpr expr = new BinaryGlobalExpr(r, new LeafGlobalExpr(r
				.getLeftOperand().getMemoryNode()), new LeafGlobalExpr(r
				.getRightOperand().getMemoryNode()), r.isCommutative());

		List<MidMemoryNode> reusableReferences = internalState
				.getReferences(expr);
		if (reusableReferences.size() > 0) {
			// If there's a reusable reference, reuse it!
			// TODO: are we sure we just take the first one?
			MidMemoryNode ref = reusableReferences.get(0);
			LogCenter.debug("[OPT] HALLELUJAH OPTIMIZING GLOBAL CSE, reusing "
					+ ref + " -> " + expr);
			MidLoadNode loadTempNode = new MidLoadNode(ref);
			MidSaveNode newSaveNode = new MidSaveNode(loadTempNode,
					saveNode.getDestinationNode());
			newSaveNode.isOptimization = true;
			loadTempNode.insertAfter(saveNode);
			newSaveNode.insertAfter(loadTempNode);
			CSELocalAnalyzer.completeDeleteBinary(saveNode);
		}

		// Save reference in global CSE.
		outputState.genReference(saveNode.getDestinationNode(), expr);
		outputState.killReferences(saveNode.getDestinationNode());
		internalState.killReferences(saveNode.getDestinationNode());

	}

}
