package edu.mit.compilers.opt.cse;

import java.util.ArrayList;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.Main;
import edu.mit.compilers.codegen.nodes.MidCallNode;
import edu.mit.compilers.codegen.nodes.MidMethodCallNode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.memory.MidTempDeclNode;
import edu.mit.compilers.codegen.nodes.regops.MidArithmeticNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidNegNode;
import edu.mit.compilers.opt.AnalyzerHelpers;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.Transformer;
import edu.mit.compilers.opt.Value;

public class CSETransformer extends Transformer<CSEGlobalState> {

	ArrayList<MidNode> assignments;

	@Override
	protected void transform(Block b, CSEGlobalState inGlobalState) {

		assignments = new ArrayList<MidNode>();
		CSEGlobalState globalState;
		if (inGlobalState == null) {
			globalState = new CSEGlobalState();
		} else {
			globalState = inGlobalState.clone();
		}

		CSELocalState localState = new CSELocalState();

		for (MidNode node : b) {
			if (node instanceof MidSaveNode
					&& ((MidSaveNode) node).savesRegister()) {
				MidSaveNode saveNode = (MidSaveNode) node;
				this.assignments.add(saveNode);
			} else if (node instanceof MidCallNode) {
				if (node instanceof MidMethodCallNode
						&& ((MidMethodCallNode) node).isStarbucksCall()) {
					continue;
				}
				this.assignments.add(node);
			}
		}

		for (MidNode assignmentNode : this.assignments) {
			LogCenter.debug("CSE", "Processing " + assignmentNode);
			if (assignmentNode instanceof MidSaveNode) {
				MidSaveNode saveNode = (MidSaveNode) assignmentNode;
				// a = x
				if (saveNode.getRegNode() instanceof MidLoadNode) {
					processSimpleAssignment(saveNode, localState);
				}
				// a = -x
				if (saveNode.getRegNode() instanceof MidNegNode) {
					processUnaryAssignment(saveNode, globalState, localState);
				}
				// a = x + y
				if (saveNode.getRegNode() instanceof MidArithmeticNode) {
					processArithmeticAssignment(saveNode, globalState, localState);
				}
			} else if (assignmentNode instanceof MidCallNode) {
				// Clear all state after a method call.
				globalState.clearGlobals();
				localState.clearGlobals();
			}
		}

	}

	private void processSimpleAssignment(MidSaveNode node, CSELocalState s) {
		MidLoadNode loadNode = (MidLoadNode) node.getRegNode();

		// Get the value of the node to be assigned to, create a new one for it
		// if necessary, i.e. x -> v1
		Value v = s.addVar(loadNode.getMemoryNode());
		// Assign destination to that value also, i.e. b -> v1
		s.addVarVal(node.getDestinationNode(), node.getRegNode(), v);
	}

	private void processUnaryAssignment(MidSaveNode saveNode,
			CSEGlobalState globalState, CSELocalState s) {
		MidNegNode r = (MidNegNode) saveNode.getRegNode();

		// Value-number left operand if necessary.
		// x = -a, a->v1, v3->-v1
		Value v1 = s.addVar(r.getOperand().getMemoryNode());
		// Value-number the resulting expression.
		Value v3 = s.addUnaryExpr(v1, r);
		// Number the assigned var with the same value.
		s.addVarVal(saveNode.getDestinationNode(), saveNode.getRegNode(), v3);
		MidSaveNode tempNode = s.getTemp(v3);

		// Check if we can reuse from an earlier block, i.e. global state
		boolean modified = CSETransfer
				.processUnaryAssignmentHelper(saveNode, globalState, true);

		// Check if the value is already in a temp.
		if (tempNode == null) {
			// It's not (in a temp), so create a new temp.
			addTempNode(saveNode, v3, s);
		} else if (!modified) {
			// If the value is already stored in a temp, use that temp
			// instead. This is the magical optimization step.
			// We assume tempNode is already in the midNodeList and can be
			// loaded.
			LogCenter.debug("CSE", "HALLELUJAH OPTIMIZING CSE (UNARY).");
			MidLoadNode loadTempNode = new MidLoadNode(
					tempNode.getDestinationNode());
			MidSaveNode newSaveNode = new MidSaveNode(loadTempNode,
					saveNode.getDestinationNode());
			newSaveNode.isOptimization = true;
			loadTempNode.insertAfter(saveNode);
			newSaveNode.insertAfter(loadTempNode);
			AnalyzerHelpers.completeDeleteUnary(saveNode);

			Main.setHasAdditionalChanges();
		}
	}

	private void processArithmeticAssignment(MidSaveNode saveNode,
			CSEGlobalState globalState, CSELocalState s) {

		MidArithmeticNode r = (MidArithmeticNode) saveNode.getRegNode();

		// Value-number left and right operands if necessary.
		// x = a+b, a->v1, b->v2, x->v3, v3->v1+v2
		Value v1 = s.addVar(r.getLeftOperand().getMemoryNode());
		Value v2 = s.addVar(r.getRightOperand().getMemoryNode());
		// Value-number the resulting expression.
		Value v3 = s.addBinaryExpr(v1, v2, r);
		// Number the assigned var with the same value.
		s.addVarVal(saveNode.getDestinationNode(), saveNode.getRegNode(), v3);
		MidSaveNode tempNode = s.getTemp(v3);

		boolean modified = CSETransfer
				.processArithmeticAssignmentHelper(saveNode, globalState, true);

		// Check if the value is already in a temp.
		if (tempNode == null) {
			CSETransformer.addTempNode(saveNode, v3, s);
		} else if (!modified) {
			// If the value is already stored in a temp, use that temp
			// instead. This is the magical optimization step.
			// We assume tempNode is already in the midNodeList and can be
			// loaded.
			LogCenter.debug("CSE|CPJ", s.toString());
			LogCenter.debug("CSE|CPJ", "HALLELUJAH OPTIMIZING CSE (BINARY).");
			LogCenter.debug("CSE|CPJ", "replacing " + saveNode + " with: "
					+ tempNode + " (" + tempNode.hashCode() + ")");
			MidLoadNode loadTempNode = new MidLoadNode(
					tempNode.getDestinationNode());
			MidSaveNode newSaveNode = new MidSaveNode(loadTempNode,
					saveNode.getDestinationNode());
			newSaveNode.isOptimization = true;
			loadTempNode.insertAfter(saveNode);
			newSaveNode.insertAfter(loadTempNode);
			AnalyzerHelpers.completeDeleteBinary(saveNode);

			Main.setHasAdditionalChanges();
		}
	}

	protected static void addTempNode(MidSaveNode saveNode, Value v3,
			CSELocalState s) {
		// It's not (in a temp), so create a new temp.
		MidTempDeclNode tempDeclNode = new MidTempDeclNode();
		MidLoadNode newLoadNode = new MidLoadNode(saveNode.getDestinationNode());
		MidSaveNode newTempNode = s.addTemp(v3, tempDeclNode, newLoadNode);
		// Add the temp after the save node. Don't forget the decl node!
		tempDeclNode.insertAfter(saveNode);
		newLoadNode.insertAfter(tempDeclNode);
		newTempNode.insertAfter(newLoadNode);
		LogCenter.debug("CSE", "Inserting a temp node: " + newTempNode + " ("
				+ newTempNode.hashCode() + ")");
	}

}
