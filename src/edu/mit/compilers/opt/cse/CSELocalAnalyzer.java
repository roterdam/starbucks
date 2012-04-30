package edu.mit.compilers.opt.cse;

import java.util.ArrayList;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.MidMethodCallNode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.memory.MidTempDeclNode;
import edu.mit.compilers.codegen.nodes.regops.MidArithmeticNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidNegNode;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.LocalAnalyzer;
import edu.mit.compilers.opt.Value;

public class CSELocalAnalyzer extends LocalAnalyzer {

	ArrayList<MidNode> assignments;

	@Override
	protected void transform(Block b) {

		assignments = new ArrayList<MidNode>();

		CSELocalState localState = new CSELocalState();

		MidNode node = b.getHead();
		while (true) {
			if (node instanceof MidSaveNode
					&& ((MidSaveNode) node).savesRegister()) {
				MidSaveNode saveNode = (MidSaveNode) node;
				this.assignments.add(saveNode);
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
			LogCenter.debug("OPT", "\nProcessing " + assignmentNode);
			if (assignmentNode instanceof MidSaveNode) {
				MidSaveNode saveNode = (MidSaveNode) assignmentNode;
				// a = x
				if (saveNode.getRegNode() instanceof MidLoadNode) {
					processSimpleAssignment(saveNode, localState);
				}
				// a = -x
				if (saveNode.getRegNode() instanceof MidNegNode) {
					processUnaryAssignment(saveNode, localState);
				}
				// a = x + y
				if (saveNode.getRegNode() instanceof MidArithmeticNode) {
					processArithmeticAssignment(saveNode, localState);
				}
			} else if (assignmentNode instanceof MidMethodCallNode) {
				// Clear all state after a method call.
				localState.clear();
			}
		}

	}

	private void processSimpleAssignment(MidSaveNode node, CSELocalState s) {
		// TODO Auto-generated method stub
		MidLoadNode loadNode = (MidLoadNode) node.getRegNode();

		// Get the value of the node to be assigned to, create a new one for it
		// if necessary, i.e. x -> v1
		Value v = s.addVar(loadNode.getMemoryNode());
		// Assign destination to that value also, i.e. b -> v1
		s.addVarVal(node.getDestinationNode(), node.getRegNode(), v);
	}

	private void processUnaryAssignment(MidSaveNode saveNode, CSELocalState s) {
		MidNegNode r = (MidNegNode) saveNode.getRegNode();

		// Value-number left operand if necessary.
		// x = -a, a->v1, v3->-v1
		Value v1 = s.addVar(r.getOperand().getMemoryNode());
		// Value-number the resulting expression.
		Value v3 = s.addUnaryExpr(v1, r);
		// Number the assigned var with the same value.
		s.addVarVal(saveNode.getDestinationNode(), saveNode.getRegNode(), v3);
		MidSaveNode tempNode = s.getTemp(v3);

		// Check if the value is already in a temp.
		if (tempNode == null) {
			// It's not (in a temp), so create a new temp.
			addTempNode(saveNode, v3, s);
		} else {
			// If the value is already stored in a temp, use that temp
			// instead. This is the magical optimization step.
			// We assume tempNode is already in the midNodeList and can be
			// loaded.
			LogCenter.debug("OPT", "HALLELUJAH OPTIMIZING CSE (UNARY).");
			MidLoadNode loadTempNode = new MidLoadNode(
					tempNode.getDestinationNode());
			MidSaveNode newSaveNode = new MidSaveNode(loadTempNode,
					saveNode.getDestinationNode());
			newSaveNode.isOptimization = true;
			loadTempNode.insertAfter(saveNode);
			newSaveNode.insertAfter(loadTempNode);
			completeDeleteUnary(saveNode);
		}
	}

	private void processArithmeticAssignment(MidSaveNode saveNode,
			CSELocalState s) {
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

		// Check if the value is already in a temp.
		if (tempNode == null) {
			CSELocalAnalyzer.addTempNode(saveNode, v3, s);
		} else {
			// If the value is already stored in a temp, use that temp
			// instead. This is the magical optimization step.
			// We assume tempNode is already in the midNodeList and can be
			// loaded.
			LogCenter.debug("OPT", s.toString());
			LogCenter.debug("OPT", "HALLELUJAH OPTIMIZING CSE (BINARY).");
			LogCenter.debug("OPT", "tempNode to replace with: " + tempNode
					+ " (" + tempNode.hashCode() + ")");
			MidLoadNode loadTempNode = new MidLoadNode(
					tempNode.getDestinationNode());
			MidSaveNode newSaveNode = new MidSaveNode(loadTempNode,
					saveNode.getDestinationNode());
			newSaveNode.isOptimization = true;
			loadTempNode.insertAfter(saveNode);
			newSaveNode.insertAfter(loadTempNode);
			completeDeleteBinary(saveNode);
		}
	}

	/**
	 * Deletes a node and all now-useless nodes before it. Assumes saveNode
	 * saves a neg node.
	 */
	protected static void completeDeleteUnary(MidSaveNode saveNode) {
		saveNode.delete();
		assert saveNode.getRegNode() instanceof MidNegNode;
		MidNegNode negNode = (MidNegNode) saveNode.getRegNode();
		LogCenter.debug("OPT", "DELETING " + negNode);
		negNode.delete();
		negNode.getOperand().delete();
	}

	/**
	 * Deletes a node and all now-useless nodes before it. Assumes saveNode
	 * saves an arithmetic node.
	 */
	protected static void completeDeleteBinary(MidSaveNode saveNode) {
		saveNode.delete();
		assert saveNode.getRegNode() instanceof MidArithmeticNode;
		MidArithmeticNode arithNode = (MidArithmeticNode) saveNode.getRegNode();
		LogCenter.debug("OPT", "DELETING " + arithNode);
		arithNode.delete();
		arithNode.getLeftOperand().delete();
		arithNode.getRightOperand().delete();
	}

	protected static void addTempNode(MidSaveNode saveNode, Value v3,
			CSELocalState s) {
		// It's not (in a temp), so create a new temp.
		MidTempDeclNode tempDeclNode = new MidTempDeclNode();
		MidSaveNode newTempNode = s.addTemp(v3, tempDeclNode);
		// Add the temp after the save node. Don't forget the decl node!
		tempDeclNode.insertAfter(saveNode);
		newTempNode.insertAfter(tempDeclNode);
		LogCenter.debug("OPT", "Inserting a temp node: " + newTempNode + " ("
				+ newTempNode.hashCode() + ")");
	}

}
