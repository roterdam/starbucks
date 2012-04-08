package edu.mit.compilers.opt;

import edu.mit.compilers.grammar.BooleanNode;
import edu.mit.compilers.grammar.ExpressionNode;
import edu.mit.compilers.grammar.SubtractNode;
import edu.mit.compilers.grammar.UnaryMinusNode;
import edu.mit.compilers.grammar.expressions.OpIntInt2BoolNode;
import edu.mit.compilers.grammar.expressions.OpSameSame2BoolNode;
import edu.mit.compilers.grammar.tokens.ANDNode;
import edu.mit.compilers.grammar.tokens.BANGNode;
import edu.mit.compilers.grammar.tokens.CALLOUTNode;
import edu.mit.compilers.grammar.tokens.CHAR_LITERALNode;
import edu.mit.compilers.grammar.tokens.DIVIDENode;
import edu.mit.compilers.grammar.tokens.FALSENode;
import edu.mit.compilers.grammar.tokens.IDNode;
import edu.mit.compilers.grammar.tokens.INT_LITERALNode;
import edu.mit.compilers.grammar.tokens.METHOD_CALLNode;
import edu.mit.compilers.grammar.tokens.MODNode;
import edu.mit.compilers.grammar.tokens.ORNode;
import edu.mit.compilers.grammar.tokens.PLUSNode;
import edu.mit.compilers.grammar.tokens.TIMESNode;
import edu.mit.compilers.grammar.tokens.TRUENode;

// WORRIES: function calls need to be called still, even if they get whacked. f(x)*0
// Function calls can also modify field variables.

// NOTE: This must necessarily be done after semantic checking to avoid overflow errors.

// TODO(saif): move UnaryMinusNode clean into here.

// TODO(saif): inherit list of things to do.

// Note: if we replace a node with it's child node, do we need to clear out it's siblings? it should happen already.


public class AlgebraicSimplifier {

	public static ExpressionNode simplifyExpression(SubtractNode node) {
		// TODO implement
		return node;
	}

	public static ExpressionNode simplifyExpression(UnaryMinusNode node) {
		// TODO implement
		return node;
	}
	
	@SuppressWarnings("serial")
	public static ExpressionNode simplifyExpression(ANDNode node){
		final BooleanNode leftNode = (BooleanNode) node.getLeftOperand().simplify();
		final BooleanNode rightNode = (BooleanNode) node.getRightOperand().simplify();
		if(leftNode instanceof TRUENode){
			rightNode.getCallsBeforeExecution().addAll(0, leftNode.getAllCallsDuringExecution());
			return rightNode;
		}else if(leftNode instanceof FALSENode || rightNode instanceof FALSENode){
			return new FALSENode(){{
				setText("false");
				getCallsBeforeExecution().addAll(leftNode.getAllCallsDuringExecution());
				getCallsAfterExecution().addAll(rightNode.getAllCallsDuringExecution());
			}};
		}else if(rightNode instanceof TRUENode){
			leftNode.getCallsAfterExecution().addAll(rightNode.getAllCallsDuringExecution());
			leftNode.setNextSibling(null);
			return leftNode;
		}
		node.replaceChild(0, node.getLeftOperand().simplify());
		node.replaceChild(1, node.getRightOperand().simplify());
		return node;
	}
	
	public static ExpressionNode simplifyExpression(BANGNode node) {
		// TODO implement
		return node;
	}
	
	public static ExpressionNode simplifyExpression(CALLOUTNode node) {
		// TODO implement
		return node;
	}
	
	public static ExpressionNode simplifyExpression(METHOD_CALLNode node) {
		// TODO implement
		return node;
	}
	
	public static ExpressionNode simplifyExpression(CHAR_LITERALNode node) {
		// TODO implement
		return node;
	}
	
	public static ExpressionNode simplifyExpression(INT_LITERALNode node) {
		// TODO implement
		return node;
	}
	
	public static ExpressionNode simplifyExpression(DIVIDENode node) {
		// TODO implement
		return node;
	}
	
	public static ExpressionNode simplifyExpression(MODNode node) {
		// TODO implement
		return node;
	}
	
	public static ExpressionNode simplifyExpression(OpSameSame2BoolNode node) {
		node.replaceChild(0, node.getLeftOperand().simplify());
		node.replaceChild(1, node.getRightOperand().simplify());
		return node;
	}
	
	public static ExpressionNode simplifyExpression(IDNode node) {
		return node;
	}
	
	public static ExpressionNode simplifyExpression(BooleanNode node) {
		return node;
	}
	
	public static ExpressionNode simplifyExpression(OpIntInt2BoolNode node) {
		// TODO implement
		return node;
	}
	
	public static ExpressionNode simplifyExpression(ORNode node){
		final BooleanNode leftNode = (BooleanNode) node.getLeftOperand().simplify();
		final BooleanNode rightNode = (BooleanNode) node.getRightOperand().simplify();
		if(leftNode instanceof FALSENode){
			rightNode.getCallsBeforeExecution().addAll(0, leftNode.getAllCallsDuringExecution());
			return rightNode;
		}else if(leftNode instanceof TRUENode || rightNode instanceof TRUENode){
			return new TRUENode(){{
				setText("true");
				getCallsBeforeExecution().addAll(leftNode.getAllCallsDuringExecution());
				getCallsAfterExecution().addAll(rightNode.getAllCallsDuringExecution());
			}};
		}else if(rightNode instanceof FALSENode){
			leftNode.getCallsAfterExecution().addAll(rightNode.getAllCallsDuringExecution());
			leftNode.setNextSibling(null);
			return leftNode;
		}
		node.replaceChild(0, node.getLeftOperand().simplify());
		node.replaceChild(1, node.getRightOperand().simplify());
		return node;
	}
	
	public static ExpressionNode simplifyExpression(PLUSNode node){
		// Case 1: int(a)  + int(b)  --> int(a+b)
		// Case 2: int(0)  + expr(x) --> expr(x)
		// Case 3: expr(x) + int(0)  --> expr(x)
		
		ExpressionNode leftOp = node.getLeftOperand().simplify();
		ExpressionNode rightOp = node.getRightOperand().simplify();
		
		if(leftOp instanceof INT_LITERALNode){
			long leftVal = ((INT_LITERALNode) leftOp).getValue();
			if(rightOp instanceof INT_LITERALNode){
				long rightVal = ((INT_LITERALNode) rightOp).getValue();
				// Case 1
				long newVal = leftVal + rightVal;
				INT_LITERALNode newNode = new INT_LITERALNode();
				newNode.setText(Long.toString(newVal));
				newNode.initializeValue();
				// Update pre-post instructions
				newNode.getCallsBeforeExecution().addAll(leftOp.getCallsBeforeExecution());
				newNode.getCallsBeforeExecution().addAll(leftOp.getCallsAfterExecution());
				newNode.getCallsBeforeExecution().addAll(rightOp.getCallsBeforeExecution());
				newNode.getCallsAfterExecution().addAll(rightOp.getCallsAfterExecution());
				return newNode;
			}else if(leftVal == 0){			
				// Case 2
				rightOp.getCallsBeforeExecution().addAll(0, leftOp.getCallsBeforeExecution());
				rightOp.getCallsBeforeExecution().addAll(0, leftOp.getCallsAfterExecution());
				rightOp.setNextSibling(null);
				return rightOp;
			}
		}else if(rightOp instanceof INT_LITERALNode){
			long rightVal = ((INT_LITERALNode) rightOp).getValue();
			if(rightVal == 0){
				// Case 3
				leftOp.getCallsAfterExecution().addAll(rightOp.getCallsBeforeExecution());
				leftOp.getCallsAfterExecution().addAll(rightOp.getCallsAfterExecution());
				rightOp.setNextSibling(null);
				//leftOp leftOp;
			}
		}
		
		node.replaceChild(0, node.getLeftOperand().simplify());
		node.replaceChild(1, node.getRightOperand().simplify());
		return node;
	}
	
	public static ExpressionNode simplifyExpression(TIMESNode node){
		// TODO implement
		return node;
	}
}
