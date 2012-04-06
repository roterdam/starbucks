package edu.mit.compilers.opt.cse;

import java.util.ArrayList;

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
		MidNode node = b.getHead();
		while (node != null) {
			// a = x + y
			// TODO: Handle unary (and assignment?) ops.
			if (node instanceof MidSaveNode
					&& ((MidSaveNode) node).savesRegister()) {
				if (((MidSaveNode) node).getRegNode() instanceof MidArithmeticNode) {
					this.assignments.add((MidSaveNode) node);
				}
			}
		}

		for (MidSaveNode m : this.assignments) {
			MidArithmeticNode r = (MidArithmeticNode) m.getRegNode();
			// Value-number left and right operands if necessary.
			Value v1 = s.addVar(r.getLeftOperand());
			Value v2 = s.addVar(r.getRightOperand());
			// Value-number the resulting expression.
			Value v3 = s.addExpr(v1, v2, r.getNodeClass());
			// Number the assignmented var with the same value.
			s.addVarVal(m, v3);
			// Check if the value is already in a temp.
			MidSaveNode tempNode = s.getTemp(v3);
			if (tempNode == null) {
				// It's not, so create a new temp.
				MidTempDeclNode tempDeclNode = new MidTempDeclNode();
				tempNode = s.addTemp(v3, tempDeclNode);
				// Add the temp after the save node.
				tempNode.insertAfter(m);
			} else {
				// If the value is already stored in a temp, use that temp instead!
				// This is the magical optimization step.
				// We assume tempNode is already in the midNodeList and can be loaded.
				MidLoadNode loadTempNode = new MidLoadNode(tempNode.getDestinationNode());
				MidSaveNode newM = new MidSaveNode(loadTempNode, m.getDestinationNode());
				loadTempNode.replace(m);
				newM.insertAfter(loadTempNode);
			}
		}

		// TODO: Does s need modification before returning?
		return s;
	}

}
