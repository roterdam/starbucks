package edu.mit.compilers.opt.cse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.MidMethodCallNode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.memory.MidFieldDeclNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.memory.MidTempDeclNode;
import edu.mit.compilers.codegen.nodes.regops.MidArithmeticNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidNegNode;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.Transfer;
import edu.mit.compilers.opt.Value;

public class CSETransfer implements Transfer<CSEGlobalState> {

	ArrayList<MidNode> assignments;

	@Override
	public CSEGlobalState apply(Block b, CSEGlobalState state) {
		assert state != null : "Input state should not be null.";

		this.assignments = new ArrayList<MidNode>();
		LogCenter.debug("[OPT]\n[OPT]\n[OPT]\n[OPT] PROCESSING " + b.getHead()
				+ ", THE GLOBAL STATE IS:\n[OPT] ##########\n[OPT] " + state);

		// TODO: shouldn't local state be somewhat dependent on the initial
		// CSEState? new CSELocalState(state)? it should at least know about
		// temps that map to existing symbol expressions
		// Possibly not, if we look in global state first before doing local
		// opts.
		CSELocalState localState = new CSELocalState();
		MidNode node = b.getHead();
		while (true) {
			if (node instanceof MidSaveNode
					&& ((MidSaveNode) node).savesRegister()) {
				MidSaveNode saveNode = (MidSaveNode) node;
				this.assignments.add(saveNode);
			} else if (node instanceof MidMethodCallNode) {
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
				MidSaveNode saveNode = (MidSaveNode) assignmentNode;
				// a = x
				if (saveNode.getRegNode() instanceof MidLoadNode) {
					processSimpleAssignment(saveNode, localState, state);
				}
				// a = -x
				if (saveNode.getRegNode() instanceof MidNegNode) {
					processUnaryAssignment(saveNode, localState, state);
				}
				// a = x + y
				if (saveNode.getRegNode() instanceof MidArithmeticNode) {
					processArithmeticAssignment(saveNode, localState, state);
				}
			} else if (assignmentNode instanceof MidMethodCallNode) {
				MidMethodCallNode methodNode = (MidMethodCallNode) assignmentNode;
				processMethodCall(methodNode, localState, state);
			}
		}

		// TODO: Does state need modification before returning?
		LogCenter.debug("[OPT] FINAL STATE IS " + state);
		LogCenter.debug("[OPT]");
		return state;
	}

	private void processMethodCall(MidMethodCallNode methodNode,
			CSELocalState localState, CSEGlobalState state) {
		Map<MidMemoryNode, List<GlobalExpr>> refMap = state.getReferenceMap();
		for (MidMemoryNode node : refMap.keySet()) {
			if (node instanceof MidFieldDeclNode) {
				state.killReferences(node);
			}
		}
	}

	private void processSimpleAssignment(MidSaveNode node, CSELocalState s,
			CSEGlobalState g) {
		
		MidLoadNode loadNode = (MidLoadNode) node.getRegNode();
//		GlobalExpr expr = new LeafGlobalExpr(loadNode.getMemoryNode());

//		if (!(loadNode.getMemoryNode() instanceof MidTempDeclNode)) {
//			g.genReference(node.getDestinationNode(), expr);
//			g.killReferences(node.getDestinationNode());
//		}

		// Get the value of the node to be assigned to, create a new one for it
		// if necessary, i.e. x -> v1
		Value v = s.addVar(loadNode.getMemoryNode());
		// Assign destination to that value also, i.e. b -> v1
		s.addVarVal(node.getDestinationNode(), node.getRegNode(), v);
	}

	private void processUnaryAssignment(MidSaveNode node, CSELocalState s,
			CSEGlobalState g) {
		MidNegNode r = (MidNegNode) node.getRegNode();

		GlobalExpr expr = new UnaryGlobalExpr(r, new LeafGlobalExpr(r
				.getOperand().getMemoryNode()));

//		List<MidMemoryNode> reusableReferences = g.getReferences(expr);
//
//		if (reusableReferences.size() > 0) {
//			// If there's a reusable reference, reuse it!
//			// TODO: are we sure we just take the first one?
//			MidMemoryNode ref = reusableReferences.get(0);
//			LogCenter.debug("[OPT] HALLELUJAH OPTIMIZING GLOBAL CSE, reusing "
//					+ ref + " -> " + expr);
//			MidLoadNode loadTempNode = new MidLoadNode(ref);
//			MidSaveNode newSaveNode = new MidSaveNode(loadTempNode,
//					node.getDestinationNode());
//			newSaveNode.isOptimization = true;
//			// loadTempNode.replace(node);
//			// newSaveNode.insertAfter(loadTempNode);
//			// Save destination node as a value.
//			s.addVar(ref);
//			return;
//		}

		// Value-number left and right operands if necessary.
		// x = a+b, a->v1, b->v2, x->v3, v3->v1+v2
		Value v1 = s.addVar(r.getOperand().getMemoryNode());
		// Value-number the resulting expression.
		Value v3 = s.addUnaryExpr(v1, r);
		// Number the assigned var with the same value.
		s.addVarVal(node.getDestinationNode(), node.getRegNode(), v3);
		MidSaveNode tempNode = s.getTemp(v3);

		// Save reference in global CSE.
//		g.genReference(node.getDestinationNode(), expr);
//		g.killReferences(node.getDestinationNode());

		// Check if the value is already in a temp.
		if (tempNode == null) {
			// It's not (in a temp), so create a new temp.
//			tempInsertHelper(node, v3, s);
		} else {
			// If the value is already stored in a temp, use that temp
			// instead. This is the magical optimization step.
			// We assume tempNode is already in the midNodeList and can be
			// loaded.
			LogCenter.debug("[OPT] HALLELUJAH OPTIMIZING CSE.");
			MidLoadNode loadTempNode = new MidLoadNode(
					tempNode.getDestinationNode());
			MidSaveNode newSaveNode = new MidSaveNode(loadTempNode,
					node.getDestinationNode());
			newSaveNode.isOptimization = true;
			loadTempNode.replace(node);
			newSaveNode.insertAfter(loadTempNode);
		}
	}

	private void processArithmeticAssignment(MidSaveNode saveNode, CSELocalState s,
			CSEGlobalState g) {
		MidArithmeticNode r = (MidArithmeticNode) saveNode.getRegNode();
//		MidMemoryNode nonTempNodeLeft = g.getNonTempMapping(r.getLeftOperand()
//				.getMemoryNode());
//		MidMemoryNode nonTempNodeRight = g.getNonTempMapping(r
//				.getRightOperand().getMemoryNode());
//		GlobalExpr expr = new BinaryGlobalExpr(r, new LeafGlobalExpr(
//				nonTempNodeLeft), new LeafGlobalExpr(nonTempNodeRight),
//				r.isCommutative());

//		List<MidMemoryNode> reusableReferences = g.getReferences(expr);
//		if (reusableReferences.size() > 0) {
//			// If there's a reusable reference, reuse it!
//			// TODO: are we sure we just take the first one?
//			MidMemoryNode ref = reusableReferences.get(0);
//			LogCenter.debug("[OPT] HALLELUJAH OPTIMIZING GLOBAL CSE, reusing "
//					+ ref + " -> " + expr);
//			MidLoadNode loadTempNode = new MidLoadNode(ref);
//			MidSaveNode newSaveNode = new MidSaveNode(loadTempNode,
//					node.getDestinationNode());
//			newSaveNode.isOptimization = true;
//			loadTempNode.insertAfter(node);
//			newSaveNode.insertAfter(loadTempNode);
//			// completeDelete(node);
//			// Save destination node as a value.
//			s.addVar(ref);
//			return;
//		}

		// Value-number left and right operands if necessary.
		// x = a+b, a->v1, b->v2, x->v3, v3->v1+v2
		Value v1 = s.addVar(r.getLeftOperand().getMemoryNode());
		Value v2 = s.addVar(r.getRightOperand().getMemoryNode());
		// Value-number the resulting expression.
		Value v3 = s.addBinaryExpr(v1, v2, r);
		// Number the assigned var with the same value.
		s.addVarVal(saveNode.getDestinationNode(), saveNode.getRegNode(), v3);
		MidSaveNode tempNode = s.getTemp(v3);

		// Save reference in global CSE.
//		g.genReference(node.getDestinationNode(), expr);
//		g.killReferences(node.getDestinationNode());

		// Check if the value is already in a temp.
		if (tempNode == null) {
			// It's not (in a temp), so create a new temp.
			MidTempDeclNode tempDeclNode = new MidTempDeclNode();
			MidSaveNode newTempNode = s.addTemp(v3, tempDeclNode);
			// Add the temp after the save node. Don't forget the decl node!
			tempDeclNode.insertAfter(saveNode);
			newTempNode.insertAfter(tempDeclNode);
			LogCenter.debug("[OPT] Inserting a temp node.");
		} else {
			// If the value is already stored in a temp, use that temp
			// instead. This is the magical optimization step.
			// We assume tempNode is already in the midNodeList and can be
			// loaded.
			LogCenter.debug("[OPT] HALLELUJAH OPTIMIZING CSE.");
			MidLoadNode loadTempNode = new MidLoadNode(
					tempNode.getDestinationNode());
			MidSaveNode newSaveNode = new MidSaveNode(loadTempNode,
					saveNode.getDestinationNode());
			newSaveNode.isOptimization = true;
			loadTempNode.insertAfter(saveNode);
			newSaveNode.insertAfter(loadTempNode);
			completeDelete(saveNode);
		}
	}

	/**
	 * Deletes a node and all now-useless nodes before it. Assumes saveNode
	 * saves an arithmetic node.
	 */
	private void completeDelete(MidSaveNode saveNode) {
		saveNode.delete();
		assert saveNode.getRegNode() instanceof MidArithmeticNode;
		MidArithmeticNode arithNode = (MidArithmeticNode) saveNode.getRegNode();
		LogCenter.debug("[OPT] DELETING " + arithNode);
		arithNode.delete();
		arithNode.getLeftOperand().delete();
		arithNode.getRightOperand().delete();
	}

}
