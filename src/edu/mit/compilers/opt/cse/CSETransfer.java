package edu.mit.compilers.opt.cse;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.Main;
import edu.mit.compilers.codegen.nodes.MidCallNode;
import edu.mit.compilers.codegen.nodes.MidMethodCallNode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.memory.MidTempDeclNode;
import edu.mit.compilers.codegen.nodes.regops.MidArithmeticNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidNegNode;
import edu.mit.compilers.opt.AnalyzerHelpers;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.Transfer;
import edu.mit.compilers.opt.cse.data.BinaryGlobalExpr;
import edu.mit.compilers.opt.cse.data.GlobalExpr;
import edu.mit.compilers.opt.cse.data.LeafGlobalExpr;

public class CSETransfer implements Transfer<CSEGlobalState> {

	ArrayList<MidNode> assignments;

	@Override
	public CSEGlobalState apply(Block b, CSEGlobalState inState) {
		assert inState != null : "Input state should not be null.";

		this.assignments = new ArrayList<MidNode>();
		LogCenter.debug("OPT", "\n\n\nPROCESSING " + b
				+ ", THE GLOBAL STATE IS:\n##########\n" + inState);

		CSEGlobalState outState = inState.clone();

		for (MidNode node : b) {
			if (node instanceof MidSaveNode
					&& ((MidSaveNode) node).savesRegister()) {
				this.assignments.add(node);
			} else if (node instanceof MidCallNode) {
				if (node instanceof MidMethodCallNode
						&& ((MidMethodCallNode) node).isStarbucksCall()) {
					continue;
				}
				this.assignments.add(node);
			}
		}

		for (MidNode assignmentNode : this.assignments) {
			LogCenter.debug("OPT", "\nProcessing " + assignmentNode);
			if (assignmentNode instanceof MidSaveNode) {
				if (assignmentNode instanceof OptSaveNode) {
					continue;
				}
				MidSaveNode saveNode = (MidSaveNode) assignmentNode;
				// a = x
				if (saveNode.getRegNode() instanceof MidLoadNode) {
					processSimpleAssignment(saveNode, outState);
				}
				// a = -x
				if (saveNode.getRegNode() instanceof MidNegNode) {
					processUnaryAssignment(saveNode, outState);
				}
				// a = x + y
				if (saveNode.getRegNode() instanceof MidArithmeticNode) {
					processArithmeticAssignment(saveNode, outState);
				}
			} else if (assignmentNode instanceof MidCallNode) {
				outState.clearGlobals();
			}
		}

		LogCenter.debug("OPT", "FINAL STATE IS " + outState);
		LogCenter.debug("OPT", "");

		return outState;
	}

	private void processSimpleAssignment(MidSaveNode node, CSEGlobalState state) {

		MidLoadNode loadNode = (MidLoadNode) node.getRegNode();

		if (!(loadNode.getMemoryNode() instanceof MidTempDeclNode)) {
			GlobalExpr expr = new LeafGlobalExpr(loadNode.getMemoryNode());
			state.genReference(node.getDestinationNode(), expr);
		}
		state.killReferences(node.getDestinationNode());
	}

	private void processUnaryAssignment(MidSaveNode saveNode,
			CSEGlobalState state) {
		processUnaryAssignmentHelper(saveNode, state, false);
	}

	/**
	 * Helper method to process a unary assignment for global CSE. Only set
	 * shouldModify to true in the transform call.
	 * 
	 * @param saveNode
	 * @param state
	 * @param shouldModify
	 * @return Whether or not it modified something.
	 */
	public static boolean processUnaryAssignmentHelper(MidSaveNode saveNode,
			CSEGlobalState state, boolean shouldModify) {
		boolean modified = false;
		MidNegNode r = (MidNegNode) saveNode.getRegNode();

		GlobalExpr expr = GlobalExpr.createUnaryExpr(r);

		if (shouldModify) {
			List<MidMemoryNode> reusableReferences = state.getReferences(expr);
			if (reusableReferences.size() > 0) {
				// If there's a reusable reference, reuse it!
				// TODO: are we sure we just take the first one?
				MidMemoryNode ref = reusableReferences.get(0);
				LogCenter
						.debug("OPT", "HALLELUJAH OPTIMIZING GLOBAL CSE, reusing "
								+ ref + " -> " + expr);
				MidLoadNode loadTempNode = new MidLoadNode(ref);
				MidSaveNode newSaveNode = new MidSaveNode(loadTempNode,
						saveNode.getDestinationNode());
				newSaveNode.isOptimization = true;
				loadTempNode.insertAfter(saveNode);
				newSaveNode.insertAfter(loadTempNode);
				AnalyzerHelpers.completeDeleteUnary(saveNode);
				modified = true;
				Main.setHasAdditionalChanges();
			}
		}

		// Save reference in global CSE.
		state.genReference(saveNode.getDestinationNode(), expr);
		state.killReferences(saveNode.getDestinationNode());

		return modified;
	}

	private void processArithmeticAssignment(MidSaveNode saveNode,
			CSEGlobalState state) {
		processArithmeticAssignmentHelper(saveNode, state, false);
	}

	public static boolean processArithmeticAssignmentHelper(
			MidSaveNode saveNode, CSEGlobalState state, boolean shouldModify) {
		boolean modified = false;
		MidArithmeticNode r = (MidArithmeticNode) saveNode.getRegNode();

		GlobalExpr expr = new BinaryGlobalExpr(r, new LeafGlobalExpr(r
				.getLeftOperand().getMemoryNode()), new LeafGlobalExpr(r
				.getRightOperand().getMemoryNode()), r.isCommutative());

		if (shouldModify) {
			List<MidMemoryNode> reusableReferences = state.getReferences(expr);
			if (reusableReferences.size() > 0) {
				// If there's a reusable reference, reuse it!
				// TODO: are we sure we just take the first one?
				MidMemoryNode ref = reusableReferences.get(0);
				LogCenter
						.debug("CSE", "HALLELUJAH OPTIMIZING GLOBAL CSE, reusing "
								+ ref + " -> " + expr);
				MidLoadNode loadTempNode = new MidLoadNode(ref);
				MidSaveNode newSaveNode = new MidSaveNode(loadTempNode,
						saveNode.getDestinationNode());
				newSaveNode.isOptimization = true;
				loadTempNode.insertAfter(saveNode);
				newSaveNode.insertAfter(loadTempNode);
				AnalyzerHelpers.completeDeleteBinary(saveNode);
				modified = true;
				Main.setHasAdditionalChanges();
			}
		}

		// Save reference in global CSE.
		state.genReference(saveNode.getDestinationNode(), expr);
		state.killReferences(saveNode.getDestinationNode());

		return modified;
	}

}
