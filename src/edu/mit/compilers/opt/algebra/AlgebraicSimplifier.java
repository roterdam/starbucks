package edu.mit.compilers.opt.algebra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.compilers.ErrorCenter;
import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.CheckDivideByZeroNode;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.grammar.BooleanNode;
import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.grammar.ExpressionNode;
import edu.mit.compilers.grammar.SubtractNode;
import edu.mit.compilers.grammar.UnaryMinusNode;
import edu.mit.compilers.grammar.expressions.OpIntInt2BoolNode;
import edu.mit.compilers.grammar.expressions.OpSameSame2BoolNode;
import edu.mit.compilers.grammar.tokens.ANDNode;
import edu.mit.compilers.grammar.tokens.ASSIGNNode;
import edu.mit.compilers.grammar.tokens.BANGNode;
import edu.mit.compilers.grammar.tokens.BLOCKNode;
import edu.mit.compilers.grammar.tokens.CALLOUTNode;
import edu.mit.compilers.grammar.tokens.CHAR_LITERALNode;
import edu.mit.compilers.grammar.tokens.CLASSNode;
import edu.mit.compilers.grammar.tokens.DIVIDENode;
import edu.mit.compilers.grammar.tokens.ELSENode;
import edu.mit.compilers.grammar.tokens.FALSENode;
import edu.mit.compilers.grammar.tokens.FIELD_DECLNode;
import edu.mit.compilers.grammar.tokens.FORNode;
import edu.mit.compilers.grammar.tokens.FOR_TERMINATENode;
import edu.mit.compilers.grammar.tokens.IDNode;
import edu.mit.compilers.grammar.tokens.IFNode;
import edu.mit.compilers.grammar.tokens.IF_CLAUSENode;
import edu.mit.compilers.grammar.tokens.INT_LITERALNode;
import edu.mit.compilers.grammar.tokens.METHOD_CALLNode;
import edu.mit.compilers.grammar.tokens.METHOD_DECLNode;
import edu.mit.compilers.grammar.tokens.MODNode;
import edu.mit.compilers.grammar.tokens.ORNode;
import edu.mit.compilers.grammar.tokens.PLUSNode;
import edu.mit.compilers.grammar.tokens.RETURNNode;
import edu.mit.compilers.grammar.tokens.TIMESNode;
import edu.mit.compilers.grammar.tokens.TRUENode;
import edu.mit.compilers.grammar.tokens.WHILENode;
import edu.mit.compilers.grammar.tokens.WHILE_TERMINATENode;

// WORRIES: function calls need to be called still, even if they get whacked.
// f(x)*0
// Function calls can also modify field variables.

// NOTE: This must necessarily be done after semantic checking to avoid overflow
// errors.

// Note: if we replace a node with it's child node, do we need to clear out it's
// siblings? it should happen already.

public class AlgebraicSimplifier {

	public static final String DIVIDE_BY_ZERO_ERROR = "Divide by zero";
	public static final String OUT_OF_BOUNDS_ERROR = "Array out of Bounds access";

	private static Map<String, Long> arrayLengths = new HashMap<String, Long>();

	public static void visit(CLASSNode node) {
		for (FIELD_DECLNode declNode : node.getFieldNodes()) {
			if (declNode.getArrayLength() != -1) {
				arrayLengths.put(declNode.getIDNode().getText(), declNode
						.getArrayLength());
				LogCenter.debug("[AS] Array " + declNode.getIDNode().getText()
						+ "->" + declNode.getArrayLength());
			}
		}
		for (METHOD_DECLNode methodNode : node.getMethodNodes()) {
			methodNode.simplifyExpressions();
		}
	}

	public static void visit(METHOD_DECLNode node) {
		LogCenter.debug("[AS] Simplifying method " + node.getId());
		node.getBlockNode().simplifyExpressions();
	}

	public static void visit(IDNode node) {
		if (node.isArray()) {
			node.setExpressionNode(node.getExpressionNode().simplify(null));
		}
	}

	public static void visit(BLOCKNode node) {
		for (DecafNode statement : node.getStatementNodes()) {
			statement.simplifyExpressions();
		}
	}

	public static void visit(CALLOUTNode node) {
		node.simplify(null);
	}

	public static void visit(METHOD_CALLNode node) {
		node.simplify(null);
	}

	public static void visit(ASSIGNNode node) {
		node.getLocation().simplifyExpressions();
		String oldList = node.getExpression().toStringList();
		node.setExpression(node.getExpression().simplify(null));

		LogCenter.debug("[AS] Simplifying " + oldList + " --> "
				+ node.getExpression().toStringList());

	}

	public static void visit(IFNode node) {
		IF_CLAUSENode ifClauseNode = node.getIfClauseNode();
		ifClauseNode.setExpressionNode(ifClauseNode.getExpressionNode()
				.simplify(null));

		node.getBlockNode().simplifyExpressions();
		if (node.hasElseBlockNode()) {
			node.getElseBlock().simplifyExpressions();
		}
	}

	public static void visit(ELSENode node) {
		node.getBlockNode().simplifyExpressions();
	}

	public static void visit(FORNode node) {
		ASSIGNNode forAssign = node.getForInitializeNode().getAssignNode();
		forAssign.simplifyExpressions();

		FOR_TERMINATENode forTerminate = node.getForTerminateNode();
		forTerminate.setExpressionNode(forTerminate.getExpressionNode()
				.simplify(null));

		node.getBlockNode().simplifyExpressions();

		// if it's an array
	}

	public static void visit(WHILENode node) {
		WHILE_TERMINATENode whileTerminate = node.getWhileTerminateNode();
		whileTerminate.setExpressionNode(whileTerminate.getExpressionNode()
				.simplify(null));

		node.getBlockNode().simplifyExpressions();
	}

	public static void visit(RETURNNode node) {
		node.setReturnExpression(node.getReturnExpression().simplify(null));
	}

	public static ExpressionNode simplifyExpression(PLUSNode node,
			MidSymbolTable symbolTable) {

		// Case 1: int(a) + int(b) --> int(a+b)
		// Case 2: int(0) + expr(x) --> expr(x)
		// Case 3: expr(x) + int(0) --> expr(x)
		// TODO case 4: expr(x) + expr(-x) this is difficult
		ExpressionNode leftOp = node.getLeftOperand().simplify(symbolTable);
		ExpressionNode rightOp = node.getRightOperand().simplify(symbolTable);

		if (leftOp instanceof INT_LITERALNode) {
			long leftVal = ((INT_LITERALNode) leftOp).getValue();
			if (rightOp instanceof INT_LITERALNode) {
				long rightVal = ((INT_LITERALNode) rightOp).getValue();
				// Case 1
				long newVal = leftVal + rightVal;
				INT_LITERALNode newNode = new INT_LITERALNode();
				newNode.setText(Long.toString(newVal));
				newNode.initializeValue();
				// Update pre-post instructions
				newNode.getCallsBeforeExecution()
						.addAll(leftOp.getCallsBeforeExecution());
				newNode.getCallsBeforeExecution()
						.addAll(leftOp.getCallsAfterExecution());
				newNode.getCallsBeforeExecution()
						.addAll(rightOp.getCallsBeforeExecution());
				newNode.getCallsAfterExecution()
						.addAll(rightOp.getCallsAfterExecution());
				return newNode;
			} else if (leftVal == 0) {
				// Case 2
				rightOp.getCallsBeforeExecution()
						.addAll(0, leftOp.getAllCallsDuringExecution());
				return rightOp;
			}
		} else if (rightOp instanceof INT_LITERALNode) {
			long rightVal = ((INT_LITERALNode) rightOp).getValue();
			if (rightVal == 0) {
				// Case 3
				leftOp.getCallsAfterExecution()
						.addAll(rightOp.getCallsBeforeExecution());
				leftOp.getCallsAfterExecution()
						.addAll(rightOp.getCallsAfterExecution());
				leftOp.setNextSibling(null);
				return leftOp;
			}
		}

		node.setLeftOperand(node.getLeftOperand().simplify(symbolTable));
		node.setRightOperand(node.getRightOperand().simplify(symbolTable));
		return node;
	}

	@SuppressWarnings("serial")
	public static ExpressionNode simplifyExpression(SubtractNode node,
			MidSymbolTable symbolTable) {
		// Case 1: int(a) - int(b) --> int(a+b)
		// Case 2: int(0) - expr(x) --> -expr(x)
		// Case 3: expr(x) - int(0) --> expr(x)
		// TODO case 4: expr(x) - expr(x) --> 0
		// this is difficult... what about 2*expr(x) - 2*expr(x)
		// We can use canonicalization of things without function calls!

		ExpressionNode leftOp = node.getLeftOperand().simplify(symbolTable);
		final ExpressionNode rightOp = node.getRightOperand()
				.simplify(symbolTable);

		if (leftOp instanceof INT_LITERALNode) {
			long leftVal = ((INT_LITERALNode) leftOp).getValue();
			if (rightOp instanceof INT_LITERALNode) {
				long rightVal = ((INT_LITERALNode) rightOp).getValue();
				// Case 1
				long newVal = leftVal - rightVal;
				INT_LITERALNode newNode = new INT_LITERALNode();
				newNode.setText(Long.toString(newVal));
				newNode.initializeValue();
				// Update pre-post instructions
				newNode.getCallsBeforeExecution()
						.addAll(leftOp.getCallsBeforeExecution());
				newNode.getCallsBeforeExecution()
						.addAll(leftOp.getCallsAfterExecution());
				newNode.getCallsBeforeExecution()
						.addAll(rightOp.getCallsBeforeExecution());
				newNode.getCallsAfterExecution()
						.addAll(rightOp.getCallsAfterExecution());
				return newNode;
			} else if (leftVal == 0) {
				// Case 2
				rightOp.getCallsBeforeExecution()
						.addAll(0, leftOp.getAllCallsDuringExecution());
				return new UnaryMinusNode() {
					{
						setText("-"); // is this right?
						setFirstChild(rightOp);
					}
				};
			}
		} else if (rightOp instanceof INT_LITERALNode) {
			long rightVal = ((INT_LITERALNode) rightOp).getValue();
			if (rightVal == 0) {
				// Case 3
				leftOp.getCallsAfterExecution()
						.addAll(rightOp.getCallsBeforeExecution());
				leftOp.getCallsAfterExecution()
						.addAll(rightOp.getCallsAfterExecution());
				leftOp.setNextSibling(null);
				return leftOp;
			}
		} else if (!leftOp.hasMethodCalls() && !rightOp.hasMethodCalls()
				&& leftOp instanceof IDNode && rightOp instanceof IDNode) {

		}

		node.setLeftOperand(node.getLeftOperand().simplify(symbolTable));
		node.setRightOperand(node.getRightOperand().simplify(symbolTable));
		return node;
	}

	@SuppressWarnings("serial")
	public static ExpressionNode simplifyExpression(UnaryMinusNode node,
			MidSymbolTable symbolTable) {

		// Note, this applies to cleaning non-literal expressions that reduce to
		// literal expressions
		// E.g.: -(a*0+4) --> -(4) --> -4

		final ExpressionNode expr = node.getOperand().simplify(symbolTable);

		if (expr instanceof INT_LITERALNode) {
			final long exprValue = ((INT_LITERALNode) expr).getValue();
			return new INT_LITERALNode() {
				{
					setText(Long.toString(-exprValue));
					initializeValue();
					getCallsBeforeExecution()
							.addAll(expr.getCallsBeforeExecution());
					getCallsAfterExecution()
							.addAll(expr.getCallsAfterExecution());
				}
			};
		}
		return node;
	}

	@SuppressWarnings("serial")
	public static ExpressionNode simplifyExpression(ANDNode node,
			MidSymbolTable symbolTable) {

		final ExpressionNode leftNode = node.getLeftOperand()
				.simplify(symbolTable);
		final ExpressionNode rightNode = node.getRightOperand()
				.simplify(symbolTable);
		if (leftNode instanceof TRUENode) {
			rightNode.getCallsBeforeExecution()
					.addAll(0, leftNode.getAllCallsDuringExecution());
			return rightNode;
		} else if (leftNode instanceof FALSENode && !rightNode.hasMethodCalls()
				|| rightNode instanceof FALSENode && !leftNode.hasMethodCalls()) {
			return new FALSENode() {
				{
					setText("false");
					getCallsBeforeExecution()
							.addAll(leftNode.getAllCallsDuringExecution());
					getCallsAfterExecution()
							.addAll(rightNode.getAllCallsDuringExecution());
				}
			};
		} else if (rightNode instanceof TRUENode) {
			leftNode.getCallsAfterExecution()
					.addAll(rightNode.getAllCallsDuringExecution());
			leftNode.setNextSibling(null);
			return leftNode;
		}
		node.setLeftOperand(node.getLeftOperand().simplify(symbolTable));
		node.setRightOperand(node.getRightOperand().simplify(symbolTable));
		return node;
	}

	@SuppressWarnings("serial")
	public static ExpressionNode simplifyExpression(ORNode node,
			MidSymbolTable symbolTable) {

		final ExpressionNode leftNode = node.getLeftOperand()
				.simplify(symbolTable);
		final ExpressionNode rightNode = node.getRightOperand()
				.simplify(symbolTable);
		if (leftNode instanceof FALSENode) {
			rightNode.getCallsBeforeExecution()
					.addAll(0, leftNode.getAllCallsDuringExecution());
			return rightNode;
		} else if (leftNode instanceof TRUENode && !rightNode.hasMethodCalls()
				|| rightNode instanceof TRUENode && !leftNode.hasMethodCalls()) {
			return new TRUENode() {
				{
					setText("true");
					getCallsBeforeExecution()
							.addAll(leftNode.getAllCallsDuringExecution());
					getCallsAfterExecution()
							.addAll(rightNode.getAllCallsDuringExecution());
				}
			};
		} else if (rightNode instanceof FALSENode) {
			leftNode.getCallsAfterExecution()
					.addAll(rightNode.getAllCallsDuringExecution());
			leftNode.setNextSibling(null);
			return leftNode;
		}

		node.setLeftOperand(node.getLeftOperand().simplify(symbolTable));
		node.setRightOperand(node.getRightOperand().simplify(symbolTable));
		return node;
	}

	@SuppressWarnings("serial")
	public static ExpressionNode simplifyExpression(BANGNode node,
			MidSymbolTable symbolTable) {

		final ExpressionNode opNode = node.getOperand().simplify(symbolTable);
		if (opNode instanceof TRUENode) {
			return new FALSENode() {
				{
					setText("false");
					getCallsBeforeExecution()
							.addAll(opNode.getCallsBeforeExecution());
					getCallsAfterExecution()
							.addAll(opNode.getCallsAfterExecution());
				}
			};
		} else if (opNode instanceof FALSENode) {
			return new TRUENode() {
				{
					setText("true");
					getCallsBeforeExecution()
							.addAll(opNode.getCallsBeforeExecution());
					getCallsAfterExecution()
							.addAll(opNode.getCallsAfterExecution());
				}
			};
		}
		return node;
	}

	public static ExpressionNode simplifyExpression(CALLOUTNode node,
			MidSymbolTable symbolTable) {

		List<DecafNode> paramNodes = new ArrayList<DecafNode>();
		for (DecafNode param : node.getArgs()) {
			if (param instanceof ExpressionNode) {
				paramNodes.add(((ExpressionNode) param).simplify(symbolTable));
			} else {
				paramNodes.add(param);
			}
		}
		for (int i = 0; i < paramNodes.size(); i++) {
			node.getArgsNode().replaceChild(i, paramNodes.get(i));
		}
		return node;
	}

	public static ExpressionNode simplifyExpression(METHOD_CALLNode node,
			MidSymbolTable symbolTable) {

		List<ExpressionNode> paramNodes = new ArrayList<ExpressionNode>();
		for (ExpressionNode expr : node.getParamNodes()) {
			paramNodes.add(expr.simplify(symbolTable));
		}
		for (int i = 0; i < paramNodes.size(); i++) {
			node.replaceChild(i + 1, paramNodes.get(i));
		}
		return node;
	}

	@SuppressWarnings("serial")
	public static ExpressionNode simplifyExpression(
			final CHAR_LITERALNode node, MidSymbolTable symbolTable) {

		return new INT_LITERALNode() {
			{
				setText(Long.toString(node.getValue()));
				initializeValue();
				getCallsBeforeExecution()
						.addAll(node.getCallsBeforeExecution());
				getCallsAfterExecution().addAll(node.getCallsAfterExecution());
			}
		};
	}

	public static ExpressionNode simplifyExpression(INT_LITERALNode node,
			MidSymbolTable symbolTable) {
		return node;
	}

	@SuppressWarnings("serial")
	public static ExpressionNode simplifyExpression(DIVIDENode node,
			MidSymbolTable symbolTable) {

		// Case 1: ??? / int(0) --> Divide by 0 error
		// Case 2: expr(x) / int(1) --> expr(x)
		// Case 3: int(a) / int(b) --> int(a/b) (b!=0)
		// Case 4: int(0) / expr(x) --> 0 but check for divide by expr(x)=0 if
		// expr(x) has no method calls

		final ExpressionNode leftOp = node.getLeftOperand()
				.simplify(symbolTable);
		final ExpressionNode rightOp = node.getRightOperand()
				.simplify(symbolTable);

		if (rightOp instanceof INT_LITERALNode) {
			long rightVal = ((INT_LITERALNode) rightOp).getValue();

			if (rightVal == 0) {
				// Case 1
				// ERROR OUT RIGHT NOW
				ErrorCenter
						.reportError(node.getLine(), node.getColumn(), String
								.format(DIVIDE_BY_ZERO_ERROR));

			} else if (rightVal == 1) {
				// Case 2
				leftOp.getCallsAfterExecution()
						.addAll(rightOp.getAllCallsDuringExecution());
				leftOp.setNextSibling(null);
				return leftOp;
			} else if (leftOp instanceof INT_LITERALNode) {
				// Case 3
				long leftVal = ((INT_LITERALNode) leftOp).getValue();
				long newVal = leftVal / rightVal;
				INT_LITERALNode newNode = new INT_LITERALNode();
				newNode.setText(Long.toString(newVal));
				newNode.initializeValue();
				// Update pre-post instructions
				newNode.getCallsBeforeExecution()
						.addAll(leftOp.getAllCallsDuringExecution());
				newNode.getCallsBeforeExecution()
						.addAll(rightOp.getCallsBeforeExecution());
				newNode.getCallsAfterExecution()
						.addAll(rightOp.getCallsAfterExecution());
				return newNode;
			}
		} else if (leftOp instanceof INT_LITERALNode) {
			long leftVal = ((INT_LITERALNode) leftOp).getValue();
			if (leftVal == 0 && !rightOp.hasMethodCalls()) {
				return new INT_LITERALNode() {
					{
						setText("0");
						initializeValue();
						getCallsBeforeExecution()
								.addAll(leftOp.getAllCallsDuringExecution());

						getCallsBeforeExecution()
								.add(new CheckDivideByZeroNode(rightOp));

						getCallsAfterExecution()
								.addAll(rightOp.getAllCallsDuringExecution());
					}
				};
			}
		}

		node.replaceChild(0, node.getLeftOperand().simplify(symbolTable));
		node.replaceChild(1, node.getRightOperand().simplify(symbolTable));
		return node;
	}

	@SuppressWarnings("serial")
	public static ExpressionNode simplifyExpression(MODNode node,
			MidSymbolTable symbolTable) {

		// Case 1: ??? % int(0) --> Divide by 0 error
		// Case 2: expr(x) % int(1) --> 0 if expr(x) has no function calls
		// Case 3: int(a) % int(b) --> int(a/b) (b!=0)
		// Case 4: int(0) % expr(x) --> 0 but check for divide by expr(x)=0,
		// only if expr(x) has no function calls

		final ExpressionNode leftOp = node.getLeftOperand()
				.simplify(symbolTable);
		final ExpressionNode rightOp = node.getRightOperand()
				.simplify(symbolTable);

		if (rightOp instanceof INT_LITERALNode) {
			long rightVal = ((INT_LITERALNode) rightOp).getValue();

			if (rightVal == 0) {
				// Case 1
				// ERROR OUT RIGHT NOW
				ErrorCenter
						.reportError(node.getLine(), node.getColumn(), String
								.format(DIVIDE_BY_ZERO_ERROR));
			} else if (rightVal == 1 && !leftOp.hasMethodCalls()) {
				// Case 2
				return new INT_LITERALNode() {
					{
						setText("0");
						initializeValue();
						getCallsBeforeExecution()
								.addAll(leftOp.getAllCallsDuringExecution());
						getCallsAfterExecution()
								.addAll(rightOp.getAllCallsDuringExecution());
					}
				};

			} else if (leftOp instanceof INT_LITERALNode) {
				// Case 3
				long leftVal = ((INT_LITERALNode) leftOp).getValue();
				// TODO (this is the behaviour of our mod operator, right?)
				long newVal = Math.abs(leftVal % rightVal);
				INT_LITERALNode newNode = new INT_LITERALNode();
				newNode.setText(Long.toString(newVal));
				newNode.initializeValue();
				// Update pre-post instructions
				newNode.getCallsBeforeExecution()
						.addAll(leftOp.getAllCallsDuringExecution());
				newNode.getCallsBeforeExecution()
						.addAll(rightOp.getCallsBeforeExecution());
				newNode.getCallsAfterExecution()
						.addAll(rightOp.getCallsAfterExecution());
				return newNode;
			}
		} else if (leftOp instanceof INT_LITERALNode) {
			long leftVal = ((INT_LITERALNode) leftOp).getValue();
			if (leftVal == 0 && !rightOp.hasMethodCalls()) {
				return new INT_LITERALNode() {
					{
						setText("0");
						initializeValue();
						getCallsBeforeExecution()
								.addAll(leftOp.getAllCallsDuringExecution());
						getCallsBeforeExecution()
								.add(new CheckDivideByZeroNode(rightOp));

						getCallsAfterExecution()
								.addAll(rightOp.getAllCallsDuringExecution());
					}
				};
			}
		}

		node.replaceChild(0, node.getLeftOperand().simplify(symbolTable));
		node.replaceChild(1, node.getRightOperand().simplify(symbolTable));
		return node;
	}

	public static ExpressionNode simplifyExpression(OpSameSame2BoolNode node,
			MidSymbolTable symbolTable) {
		// TODO: handle BoolBool2Bool --> x == true, and x == false,
		// handle IntInt2Bool --> 7 == 7, 3+4 == 7,
		// and !=
		node.replaceChild(0, node.getLeftOperand().simplify(symbolTable));
		node.replaceChild(1, node.getRightOperand().simplify(symbolTable));
		return node;
	}

	public static ExpressionNode simplifyExpression(IDNode node,
			MidSymbolTable symbolTable) {

		if (node.isArray()) {
			ExpressionNode expr = node.getExpressionNode()
					.simplify(symbolTable);
			if (expr instanceof INT_LITERALNode) {
				long exprValue = ((INT_LITERALNode) expr).getValue();
				// MidFieldArrayDeclNode arrayNode = (MidFieldArrayDeclNode)
				// symbolTable.getVar(node.getText());
				// long arrayLength = arrayNode.getLength();
				long arrayLength = arrayLengths.get(node.getText());
				if (exprValue < 0 || exprValue >= arrayLength) {
					ErrorCenter
							.reportError(node.getLine(), node.getColumn(), String
									.format(OUT_OF_BOUNDS_ERROR));
				}
			}
			node.setExpressionNode(expr);
			return node;
		}
		return node;
	}

	public static ExpressionNode simplifyExpression(BooleanNode node,
			MidSymbolTable symbolTable) {
		return node;
	}

	public static ExpressionNode simplifyExpression(OpIntInt2BoolNode node,
			MidSymbolTable symbolTable) {

		// TODO: 1 <= 5
		node.setLeftOperand(node.getLeftOperand().simplify(symbolTable));
		node.setRightOperand(node.getRightOperand().simplify(symbolTable));
		return node;
	}

	@SuppressWarnings("serial")
	public static ExpressionNode simplifyExpression(TIMESNode node,
			MidSymbolTable symbolTable) {

		// Case 1: int(a) * int(b) --> int(a*b)
		// Case 2: int(1) * expr(x) --> expr(x)
		// Case 3: int(0) * expr(x) --> int(0) if expr(x) has a function call it
		// should still be called... for now don't simplify.
		// Case 4: expr(x) * int(1) --> expr(x)
		// Case 5: expr(x) * int(0) --> int(0) if expr(x) has a function call it
		// should still be called... for now don't simplify.
		// (f(x)+g(x))*0

		final ExpressionNode leftOp = node.getLeftOperand()
				.simplify(symbolTable);

		final ExpressionNode rightOp = node.getRightOperand()
				.simplify(symbolTable);

		if (leftOp instanceof INT_LITERALNode) {
			long leftVal = ((INT_LITERALNode) leftOp).getValue();
			if (rightOp instanceof INT_LITERALNode) {
				long rightVal = ((INT_LITERALNode) rightOp).getValue();
				// Case 1
				long newVal = leftVal * rightVal;
				INT_LITERALNode newNode = new INT_LITERALNode();
				newNode.setText(Long.toString(newVal));
				newNode.initializeValue();
				// Update pre-post instructions
				newNode.getCallsBeforeExecution()
						.addAll(leftOp.getAllCallsDuringExecution());
				newNode.getCallsBeforeExecution()
						.addAll(rightOp.getCallsBeforeExecution());
				newNode.getCallsAfterExecution()
						.addAll(rightOp.getCallsAfterExecution());
				return newNode;
			} else if (leftVal == 1) {
				// Case 2
				rightOp.getCallsBeforeExecution()
						.addAll(0, leftOp.getAllCallsDuringExecution());
				return rightOp;
			} else if (leftVal == 0 && !rightOp.hasMethodCalls()) {
				// Case 3
				return new INT_LITERALNode() {
					{
						setText("0");
						initializeValue();
						getCallsBeforeExecution()
								.addAll(leftOp.getAllCallsDuringExecution());
						getCallsAfterExecution()
								.addAll(rightOp.getAllCallsDuringExecution());
					}
				};
			}
		} else if (rightOp instanceof INT_LITERALNode) {
			long rightVal = ((INT_LITERALNode) rightOp).getValue();
			if (rightVal == 1) {
				// Case 4
				leftOp.getCallsAfterExecution()
						.addAll(rightOp.getAllCallsDuringExecution());
				leftOp.setNextSibling(null);
				return leftOp;
			} else if (rightVal == 0 && !leftOp.hasMethodCalls()) {
				// Case 5
				return new INT_LITERALNode() {
					{
						setText("0");
						initializeValue();
						getCallsBeforeExecution()
								.addAll(rightOp.getAllCallsDuringExecution());
						getCallsAfterExecution()
								.addAll(leftOp.getAllCallsDuringExecution());
					}
				};
			}
		}

		node.setLeftOperand(node.getLeftOperand().simplify(symbolTable));
		node.setRightOperand(node.getRightOperand().simplify(symbolTable));
		return node;
	}
}
