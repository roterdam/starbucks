package edu.mit.compilers.opt.dce;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.MidCalloutNode;
import edu.mit.compilers.codegen.nodes.MidMethodCallNode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.regops.MidArithmeticNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidNegNode;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.Transfer;

/**
 * Reaching definition analysis - set of related analyses.
 */
public class DCETransfer implements Transfer<DCEGlobalState> {

	public DCEGlobalState apply(Block b, DCEGlobalState inState) {
		assert inState != null : "Input state should not be null.";
		
		LogCenter.debug("[DCE]\n[DCE]\n[DCE]\n[DCE] PROCESSING " + b
				+ ", THE GLOBAL STATE IS:\n[DCE] ##########\n[DCE] " + inState);

		DCEGlobalState outState = inState.clone();

		//Traverse and see what should be added to the needed state.

		MidNode node = b.getTail();
		if (node == null){
			//Will happen for tail block
			return inState;
		}
		while (true) {
			System.out.println("NEW NODE: " + node);

			//if it's a save node, add it's load nodes in if it's part of the outstate already.

			if (node instanceof MidSaveNode) {
				outState.processSaveNode((MidSaveNode)node);
			} else if (node instanceof MidMethodCallNode
					&& !((MidMethodCallNode) node).isStarbucksCall()) {
				//reset?
				//Not sure what to do. it was copied from CP opt.
			} else if (node instanceof MidCalloutNode) {
				outState.processCalloutNode((MidCalloutNode)node);
			}
			if (node == b.getHead()) {
				break;
			}
			System.out.println(node);
			node = node.getPrevNode();
		}
		
		LogCenter.debug("[DCE] FINAL STATE IS " + outState);
		LogCenter.debug("[DCE]");

		return outState;
	}

	
	public void cleanUp(Block b, DCEGlobalState finalState) {

		MidNode node = b.getTail();	

		while (true) {
			if (node == null){
				//Will happen for tail block
				break;
			}
			if (node instanceof MidSaveNode){
				if (!finalState.isNeeded(node)){
					deleteSaveNodeEtAl((MidSaveNode)node);
				}
			}
			if (node == b.getHead()) {
				break;
			}
			node = node.getPrevNode();
		}
	}


//	public DCEGlobalState apply(Block b, DCEGlobalState inState) {
//		assert inState != null : "Input state should not be null.";
//
//		this.skipList = new ArrayList<MidNode>();
//		this.nodesOfInterest = new ArrayList<MidNode>();
//		LogCenter.debug("[DCE]\n[DCE]\n[DCE]\n[DCE] PROCESSING " + b
//				+ ", THE GLOBAL STATE IS:\n[DCE] ##########\n[DCE] " + inState);
//
//		DCEGlobalState outState = inState.clone();
//
//		MidNode node = b.getTail();
//		
//		while (true) {
//			System.out.println("NEW NODE: " + node);
//			
//			if (node instanceof MidSaveNode
//					&& ((MidSaveNode) node).savesRegister()) {
//				MidSaveNode saveNode = (MidSaveNode) node;
//				// Skip optimizing array access saves for now.
//				// TODO: perhaps it's optimizeable? Same in CPLocalAnalyzer.
//				boolean skip = false;
//				if (saveNode.getRegNode() instanceof MidLoadNode) {
//					MidLoadNode loadNode = (MidLoadNode) saveNode.getRegNode();
//					if (loadNode.getMemoryNode() instanceof MidArrayElementNode) {
//						skip = true;
//					}
//				}
//				if (!skip) {
//					this.nodesOfInterest.add(node);
//				}
//
//			} else if (node instanceof MidLoadNode) {
//				this.nodesOfInterest.add(node);
//			} else if (node instanceof MidMethodCallNode
//					&& !((MidMethodCallNode) node).isStarbucksCall()) {
//				this.nodesOfInterest.add(node);
//			} else if (node instanceof MidCalloutNode) {
//				this.nodesOfInterest.addAll(((MidCalloutNode)node).getParams());
//			}
//			if (node == b.getHead()) {
//				break;
//			}
//			node = node.getPrevNode();
//		}
//
//		
//		for (MidNode assignmentNode : this.nodesOfInterest) {
//			System.out.println("+SKIP LIST " + skipList);
//			if (skipList.contains(assignmentNode)){
//				System.out.println("[DCE] Skipping " + assignmentNode);
//				continue;
//			}
//			LogCenter.debug("[DCE]\n[DCE] Processing " + assignmentNode);
//			if (assignmentNode instanceof MidSaveNode) {
//				MidSaveNode saveNode = (MidSaveNode) assignmentNode;
//				processDefinition(outState, saveNode);
//			} else if (assignmentNode instanceof MidLoadNode) {
//				MidLoadNode loadNode = (MidLoadNode) assignmentNode;
//				processUse(outState, loadNode);
//			} else if (assignmentNode instanceof MidMethodCallNode) {
//				outState.reset();
//			} else if (assignmentNode instanceof MidTempDeclNode){
//				// need to get the loads that tempDecl does on behalf of callouts.
//				processUse(outState, (MidTempDeclNode)assignmentNode);
//			}
//		}
//
//		LogCenter.debug("[DCE] FINAL STATE IS " + outState);
//		LogCenter.debug("[DCE]");
//
//		return outState;
//	}
//
//	private void processUse(DCEGlobalState outState, MidLoadNode loadNode) {
//		outState.addNeeded(loadNode.getMemoryNode());
//	}
//
//	private void processUse(DCEGlobalState outState, MidTempDeclNode declNode) {
//		outState.addNeeded(declNode);
//	}
//	
//	private void processDefinition(DCEGlobalState outState, MidSaveNode saveNode) {
//		Boolean needed = outState.isNeeded(saveNode.getDestinationNode());
//		if (!needed){
//			System.out.println("[DCE] " + saveNode.toString() + " not needed");
//			deleteSaveNodeEtAl(saveNode);
//		} else { 
//			System.out.println("[DCE] " + saveNode.toString() + " NEEDED");
//		}
//
//	}
	
	

	
	private void deleteSaveNodeEtAl(MidSaveNode saveNode){
		LogCenter.debug("[DCE] DELETING " + saveNode);
		if (saveNode.getRegNode() instanceof MidLoadNode) {
			saveNode.delete();
			saveNode.getRegNode().delete();
		}
		// a = -x
		if (saveNode.getRegNode() instanceof MidNegNode) {
			MidNegNode negNode = (MidNegNode) saveNode.getRegNode();
			LogCenter.debug("[DCE] DELETING " + negNode);

			saveNode.delete();
			negNode.delete();
			negNode.getOperand().delete();
		}
		// a = x + y
		if (saveNode.getRegNode() instanceof MidArithmeticNode) {
			saveNode.delete();
			MidArithmeticNode arithNode = (MidArithmeticNode) saveNode.getRegNode();
			LogCenter.debug("[DCE] DELETING " + arithNode);

			arithNode.delete();
			arithNode.getLeftOperand().delete();
			arithNode.getRightOperand().delete();

		}
	}

}
