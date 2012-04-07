package edu.mit.compilers.opt.cse;

import java.util.ArrayList;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.memory.MidTempDeclNode;
import edu.mit.compilers.codegen.nodes.regops.MidArithmeticNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.Transfer;
import edu.mit.compilers.opt.Value;

public class CSETransfer implements Transfer<CSEState> {

	ArrayList<MidSaveNode> assignments;

	public CSETransfer() {
		this.assignments = new ArrayList<MidSaveNode>();
	}

	@Override
	public CSEState apply(Block b, CSEState s) {
		assert s != null : "Input state should not be null.";
		MidNode node = b.getHead();
		while (node != null) {
			// TODO: Handle unary ops.
			if (node instanceof MidSaveNode
					&& ((MidSaveNode) node).savesRegister()) {
				MidSaveNode saveNode = (MidSaveNode) node;
				this.assignments.add(saveNode);
			}
			node = node.getNextNode();
		}

		for (MidSaveNode saveNode : this.assignments) {
			// a = x
			if (saveNode.getRegNode() instanceof MidLoadNode) {
				processSimpleAssignment(saveNode, s);
			}
			// a = x + y
			if (saveNode.getRegNode() instanceof MidArithmeticNode) {
				processArithmeticAssignment(saveNode, s);
			}
		}

		// TODO: Does s need modification before returning?
		return s;
	}

	private void processSimpleAssignment(MidSaveNode node, CSEState s) {
		MidLoadNode loadNode = (MidLoadNode) node.getRegNode();
		// b = x;
		// Get the value of the node to be assigned to, create a new one for it
		// if necessary, i.e. x -> v1
		Value v = s.addVar(loadNode.getMemoryNode());
		// Assign destination to that value also, i.e. b -> v1
		s.addVarVal(node, v);
		// Get the temp node associated with that value, i.e. x -> t1
		MidSaveNode tempNode = s.getTemp(v);
		// Check if temp node is already in the list.
		if (tempNode == null) {
			// If not, add it.
			// b = x; t1 = b;
			tempInsertHelper(tempNode, node, v, s);
		} else {
			// If yes, we can use the temp instead of the value.
			// b = t1;
			MidLoadNode loadTempNode = new MidLoadNode(
					tempNode.getDestinationNode());
			MidSaveNode saveTempNode = new MidSaveNode(loadTempNode,
					node.getDestinationNode());
			// Replace both the save node and the load node before it.
			loadTempNode.replace(loadNode);
			saveTempNode.replace(node);
		}
	}

	private void processArithmeticAssignment(MidSaveNode node, CSEState s) {
		MidArithmeticNode r = (MidArithmeticNode) node.getRegNode();
		// Value-number left and right operands if necessary.
		Value v1 = s.addVar(r.getLeftOperand().getMemoryNode());
		Value v2 = s.addVar(r.getRightOperand().getMemoryNode());
		// Value-number the resulting expression.
		Value v3 = s.addExpr(v1, v2, r.getNodeClass());
		// Number the assigned var with the same value.
		s.addVarVal(node, v3);
		// Check if the value is already in a temp.
		MidSaveNode tempNode = s.getTemp(v3);
		if (tempNode == null) {
			// It's not (in a temp), so create a new temp.
			tempInsertHelper(tempNode, node, v3, s);
		} else {
			// If the value is already stored in a temp, use that temp
			// instead. This is the magical optimization step.
			// We assume tempNode is already in the midNodeList and can be
			// loaded.
			LogCenter.debug("[OPT] HALLELUJAH OPTIMIZING.");
			MidLoadNode loadTempNode = new MidLoadNode(
					tempNode.getDestinationNode());
			MidSaveNode newM = new MidSaveNode(loadTempNode,
					node.getDestinationNode());
			newM.isOptimization = true;
			loadTempNode.replace(node);
			newM.insertAfter(loadTempNode);
		}
	}

	private void tempInsertHelper(MidSaveNode tempNode, MidNode originalNode,
			Value v, CSEState s) {
		MidTempDeclNode tempDeclNode = new MidTempDeclNode();
		tempNode = s.addTemp(v, tempDeclNode);
		// Add the temp after the save node. Don't forget the decl node!
		tempDeclNode.insertAfter(originalNode);
		tempNode.insertAfter(tempDeclNode);
		LogCenter.debug("[OPT] Inserting a temp node.");
	}

}
