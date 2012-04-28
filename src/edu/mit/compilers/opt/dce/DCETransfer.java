package edu.mit.compilers.opt.dce;

import java.util.ArrayList;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.MidCalloutNode;
import edu.mit.compilers.codegen.nodes.MidMethodCallNode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.memory.MidArrayElementNode;
import edu.mit.compilers.codegen.nodes.memory.MidTempDeclNode;
import edu.mit.compilers.codegen.nodes.regops.MidArithmeticNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidNegNode;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.Transfer;

/**
 * Reaching definition analysis - set of related analyses.
 */
public class DCETransfer implements Transfer<DCEGlobalState> {

	private ArrayList<MidNode> nodesOfInterest;
	private ArrayList<MidNode> skipList;
	
	public DCEGlobalState apply(Block b, DCEGlobalState inState) {
		assert inState != null : "Input state should not be null.";

		this.skipList = new ArrayList<MidNode>();
		this.nodesOfInterest = new ArrayList<MidNode>();
		LogCenter.debug("[DCE]\n[DCE]\n[DCE]\n[DCE] PROCESSING " + b
				+ ", THE GLOBAL STATE IS:\n[DCE] ##########\n[DCE] " + inState);

		DCEGlobalState outState = inState.clone();

		MidNode node = b.getTail();
		
		while (true) {
			System.out.println("NEW NODE: " + node);
			
			if (node instanceof MidSaveNode
					&& ((MidSaveNode) node).savesRegister()) {
				MidSaveNode saveNode = (MidSaveNode) node;
				// Skip optimizing array access saves for now.
				// TODO: perhaps it's optimizeable? Same in CPLocalAnalyzer.
				boolean skip = false;
				if (saveNode.getRegNode() instanceof MidLoadNode) {
					MidLoadNode loadNode = (MidLoadNode) saveNode.getRegNode();
					if (loadNode.getMemoryNode() instanceof MidArrayElementNode) {
						skip = true;
					}
				}
				if (!skip) {
					this.nodesOfInterest.add(node);
				}

			} else if (node instanceof MidLoadNode) {
				this.nodesOfInterest.add(node);
			} else if (node instanceof MidMethodCallNode
					&& !((MidMethodCallNode) node).isStarbucksCall()) {
				this.nodesOfInterest.add(node);
			} else if (node instanceof MidCalloutNode) {
				this.nodesOfInterest.addAll(((MidCalloutNode)node).getParams());
			}
			if (node == b.getHead()) {
				break;
			}
			node = node.getPrevNode();
		}

		
		for (MidNode assignmentNode : this.nodesOfInterest) {
			System.out.println("+SKIP LIST " + skipList);
			if (skipList.contains(assignmentNode)){
				System.out.println("[DCE] Skipping " + assignmentNode);
				continue;
			}
			LogCenter.debug("[DCE]\n[DCE] Processing " + assignmentNode);
			if (assignmentNode instanceof MidSaveNode) {
				MidSaveNode saveNode = (MidSaveNode) assignmentNode;
				processDefinition(outState, saveNode);
			} else if (assignmentNode instanceof MidLoadNode) {
				MidLoadNode loadNode = (MidLoadNode) assignmentNode;
				processUse(outState, loadNode);
			} else if (assignmentNode instanceof MidMethodCallNode) {
				outState.reset();
			} else if (assignmentNode instanceof MidTempDeclNode){
				// need to get the loads that tempDecl does on behalf of callouts.
				processUse(outState, (MidTempDeclNode)assignmentNode);
			}
		}

		LogCenter.debug("[DCE] FINAL STATE IS " + outState);
		LogCenter.debug("[DCE]");

		return outState;
	}

	private void processUse(DCEGlobalState outState, MidLoadNode loadNode) {
		outState.addNeeded(loadNode.getMemoryNode());
	}

	private void processUse(DCEGlobalState outState, MidTempDeclNode declNode) {
		outState.addNeeded(declNode);
	}
	
	private void processDefinition(DCEGlobalState outState, MidSaveNode saveNode) {
		Boolean needed = outState.isNeeded(saveNode.getDestinationNode());
		if (!needed){
			System.out.println("[DCE] " + saveNode.toString() + " not needed");
			deleteSaveNodeEtAl(saveNode);
		} else { 
			System.out.println("[DCE] " + saveNode.toString() + " NEEDED");
		}

	}
	
	
	private void deleteSaveNodeEtAl(MidSaveNode saveNode){
		LogCenter.debug("[DCE] DELETING " + saveNode);
		if (saveNode.getRegNode() instanceof MidLoadNode) {
			skipList.add(saveNode);
			skipList.add(saveNode.getRegNode());

			saveNode.delete();
			saveNode.getRegNode().delete();
		}
		// a = -x
		if (saveNode.getRegNode() instanceof MidNegNode) {
			saveNode.delete();
			assert saveNode.getRegNode() instanceof MidNegNode;
			MidNegNode negNode = (MidNegNode) saveNode.getRegNode();
			LogCenter.debug("[DCE] DELETING " + negNode);
			skipList.add(negNode);
			skipList.add(negNode.getOperand());

			negNode.delete();
			negNode.getOperand().delete();
		}
		// a = x + y
		if (saveNode.getRegNode() instanceof MidArithmeticNode) {
			saveNode.delete();
			assert saveNode.getRegNode() instanceof MidArithmeticNode;
			MidArithmeticNode arithNode = (MidArithmeticNode) saveNode.getRegNode();
			LogCenter.debug("[DCE] DELETING " + arithNode);
			skipList.add(arithNode);
			skipList.add(arithNode.getLeftOperand());
			skipList.add(arithNode.getRightOperand());

			arithNode.delete();
			arithNode.getLeftOperand().delete();
			arithNode.getRightOperand().delete();

		}
	}

}
