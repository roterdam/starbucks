package edu.mit.compilers.opt.algebra;

import java.util.List;

import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.grammar.ExpressionNode;
import edu.mit.compilers.grammar.tokens.BLOCKNode;
import edu.mit.compilers.grammar.tokens.IFNode;
import edu.mit.compilers.grammar.tokens.TRUENode;
import edu.mit.compilers.grammar.tokens.FALSENode;
import edu.mit.compilers.grammar.tokens.WHILENode;

public class UnreachableCodeEliminator {
	// Optimizes: if(true), if(false), while(false), continue, break, return
	
	/** 
	 * Never returns null 
	 */
	public static BLOCKNode visit(BLOCKNode node) {
		DecafNode currentNode = node.getFirstChild();
		DecafNode previousNode = null;
		while(currentNode != null){
			DecafNode nextNode = currentNode.getNextSibling();
			
			DecafNode replacementNode = currentNode.eliminateUnreachableCode();
			DecafNode tailNode = replacementNode.getTail();
			if(tailNode.isBlockEnder()){
				tailNode.setNextSibling(null);
			}else{
				tailNode.setNextSibling(nextNode);
			}
	
			if(previousNode == null){
				node.setFirstChild(replacementNode);
			}else{
				previousNode.setNextSibling(replacementNode);
			}
			
			previousNode = tailNode;
			currentNode = tailNode.getNextSibling();
		}
		return node;
	}
	public static DecafNode visit(WHILENode node){
		ExpressionNode exprNode = node.getWhileTerminateNode().getExpressionNode();
		if(exprNode instanceof FALSENode){
			List<DecafNode> returnList = exprNode.getAllCallsDuringExecution();
			DecafNode returnNode = node.getBlockNode().eliminateUnreachableCode();
			returnList.add(returnNode);
			return stitchNodes(returnList);
		}else{
			return node;
		}
	}
	
	public static DecafNode visit(IFNode node) {
		ExpressionNode exprNode = node.getIfClauseNode().getExpressionNode();
		if(exprNode instanceof TRUENode){
			List<DecafNode> returnList = exprNode.getAllCallsDuringExecution();
			BLOCKNode returnNode = node.getBlockNode().eliminateUnreachableCode();
			returnList.add(returnNode);
			return stitchNodes(returnList);
		}else if(exprNode instanceof FALSENode){
			List<DecafNode> returnList = exprNode.getAllCallsDuringExecution();
			if(node.hasElseBlockNode()){
				DecafNode returnNode = node.getElseBlock().getBlockNode().eliminateUnreachableCode();
				returnList.add(returnNode);
			}
			return stitchNodes(returnList);
		}else {
			node.setBlockNode(node.getBlockNode().eliminateUnreachableCode());
			if(node.hasElseBlockNode()){
				node.getElseBlock().setBlockNode(node.getElseBlock().getBlockNode().eliminateUnreachableCode());
			}
			return node;
		}
	}
	
	private static DecafNode stitchNodes(List<DecafNode> nodes){
		for(int i=0; i < nodes.size()-1; i++){
			nodes.get(i).setNextSibling(nodes.get(i+1));
		}
		return nodes.size() > 0 ? nodes.get(0) : new BLOCKNode();
	}
}
