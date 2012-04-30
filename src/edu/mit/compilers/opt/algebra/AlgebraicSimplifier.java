package edu.mit.compilers.opt.algebra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.compilers.ErrorCenter;
import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.PotentialCheckDivideByZeroNode;
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
import edu.mit.compilers.grammar.tokens.EQNode;
import edu.mit.compilers.grammar.tokens.FALSENode;
import edu.mit.compilers.grammar.tokens.FIELD_DECLNode;
import edu.mit.compilers.grammar.tokens.FORNode;
import edu.mit.compilers.grammar.tokens.FOR_TERMINATENode;
import edu.mit.compilers.grammar.tokens.GTENode;
import edu.mit.compilers.grammar.tokens.GTNode;
import edu.mit.compilers.grammar.tokens.IDNode;
import edu.mit.compilers.grammar.tokens.IFNode;
import edu.mit.compilers.grammar.tokens.IF_CLAUSENode;
import edu.mit.compilers.grammar.tokens.INT_LITERALNode;
import edu.mit.compilers.grammar.tokens.LTENode;
import edu.mit.compilers.grammar.tokens.LTNode;
import edu.mit.compilers.grammar.tokens.METHOD_CALLNode;
import edu.mit.compilers.grammar.tokens.METHOD_DECLNode;
import edu.mit.compilers.grammar.tokens.MODNode;
import edu.mit.compilers.grammar.tokens.NEQNode;
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
				LogCenter.debug("AS", "Array " + declNode.getIDNode().getText()
						+ "->" + declNode.getArrayLength());
			}
		}
		for (METHOD_DECLNode methodNode : node.getMethodNodes()) {
			methodNode.simplifyExpressions();
		}
	}

	public static void visit(METHOD_DECLNode node) {
		LogCenter.debug("[AS]", "Simplifying method " + node.getId());
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

		LogCenter.debug("AS", "Simplifying " + oldList + " --> "
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
		String oldList = node.toStringList();
		LogCenter.debug("AS", "About to simplify return node "
				+ node.getReturnExpression().toStringList());
		node.setReturnExpression(node.getReturnExpression().simplify(null));

		LogCenter.debug("AS", "Simplified" + oldList + " --> "
				+ node.getReturnExpression().toStringList());

	}

	@SuppressWarnings("serial")
	public static ExpressionNode simplifyExpression(PLUSNode node,
			MidSymbolTable symbolTable) {

		// Case 1: int(a) + int(b) --> int(a+b)
		// Case 2: int(0) + expr(x) --> expr(x)
		// Case 3: expr(x) + int(0) --> expr(x)
		// case 4: expr(x) + expr(-x) this is difficult

		final ExpressionNode leftOp = node.getLeftOperand()
				.simplify(symbolTable);
		final ExpressionNode rightOp = node.getRightOperand()
				.simplify(symbolTable);

		Canonicalization retCanonicalization = Canonicalization.add(leftOp
				.getCanonicalization(), rightOp.getCanonicalization());
		node.setCanonicalization(retCanonicalization);

		if (leftOp instanceof INT_LITERALNode) {
			long leftVal = ((INT_LITERALNode) leftOp).getValue();
			if (rightOp instanceof INT_LITERALNode) {
				long rightVal = ((INT_LITERALNode) rightOp).getValue();
				// Case 1
				long newVal = leftVal + rightVal;
				INT_LITERALNode newNode = new INT_LITERALNode();
				newNode.setText(Long.toString(newVal));
				newNode.setCanonicalization(Canonicalization
						.makeLiteral(newVal));
				newNode.initializeValue();
				// Update pre-post instructions
				newNode.getCallsBeforeExecution()
						.addAll(leftOp.getAllCallsDuringExecution());
				newNode.getCallsAfterExecution()
						.addAll(rightOp.getAllCallsDuringExecution());
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
						.addAll(rightOp.getAllCallsDuringExecution());
				leftOp.setNextSibling(null);
				return leftOp;
			}
		} else if (Canonicalization
				.equals(retCanonicalization, Canonicalization.ZERO)) {
			// Case 4
			return new INT_LITERALNode() {
				{
					setText(Long.toString(0));
					setCanonicalization(Canonicalization.ZERO);
					initializeValue();
					getCallsBeforeExecution()
							.addAll(leftOp.getAllCallsDuringExecution());
					getCallsAfterExecution()
							.addAll(rightOp.getAllCallsDuringExecution());
				}
			};
		}

		node.setLeftOperand(leftOp);
		node.setRightOperand(rightOp);
		return node;
	}

	@SuppressWarnings("serial")
	public static ExpressionNode simplifyExpression(SubtractNode node,
			MidSymbolTable symbolTable) {
		// Case 1: int(a) - int(b) --> int(a+b)
		// Case 2: int(0) - expr(x) --> -expr(x)
		// Case 3: expr(x) - int(0) --> expr(x)
		// case 4: expr(x) - expr(x) --> 0
		// this is difficult... what about 2*expr(x) - 2*expr(x)
		// We can use canonicalization of things without function calls!
		final ExpressionNode leftOp = node.getLeftOperand()
				.simplify(symbolTable);
		final ExpressionNode rightOp = node.getRightOperand()
				.simplify(symbolTable);

		Canonicalization retCanonicalization = Canonicalization.sub(leftOp
				.getCanonicalization(), rightOp.getCanonicalization());
		node.setCanonicalization(retCanonicalization);

		if (leftOp instanceof INT_LITERALNode) {
			long leftVal = ((INT_LITERALNode) leftOp).getValue();
			if (rightOp instanceof INT_LITERALNode) {
				long rightVal = ((INT_LITERALNode) rightOp).getValue();
				// Case 1
				long newVal = leftVal - rightVal;
				INT_LITERALNode newNode = new INT_LITERALNode();
				newNode.setText(Long.toString(newVal));
				newNode.setCanonicalization(Canonicalization
						.makeLiteral(newVal));
				newNode.initializeValue();
				// Update pre-post instructions
				newNode.getCallsBeforeExecution()
						.addAll(leftOp.getAllCallsDuringExecution());
				newNode.getCallsAfterExecution()
						.addAll(rightOp.getAllCallsDuringExecution());

				return newNode;
			} else if (leftVal == 0) {
				// Case 2
				rightOp.getCallsBeforeExecution()
						.addAll(0, leftOp.getAllCallsDuringExecution());
				return new UnaryMinusNode() {
					{
						setText("-"); // is this right?
						setCanonicalization(Canonicalization.inv(rightOp
								.getCanonicalization()));
						setFirstChild(rightOp);
					}
				};
			}
		} else if (rightOp instanceof INT_LITERALNode) {
			long rightVal = ((INT_LITERALNode) rightOp).getValue();
			if (rightVal == 0) {
				// Case 3
				leftOp.getCallsAfterExecution()
						.addAll(rightOp.getAllCallsDuringExecution());
				leftOp.setNextSibling(null);
				return leftOp;
			}
		} else if (Canonicalization
				.equals(retCanonicalization, Canonicalization.ZERO)) {
			// Case 4
			return new INT_LITERALNode() {
				{
					setText(Long.toString(0));
					setCanonicalization(Canonicalization.ZERO);
					initializeValue();
					getCallsBeforeExecution()
							.addAll(leftOp.getAllCallsDuringExecution());
					getCallsAfterExecution()
							.addAll(rightOp.getAllCallsDuringExecution());
				}
			};
		}

		node.setLeftOperand(leftOp);
		node.setRightOperand(rightOp);
		return node;
	}

	@SuppressWarnings("serial")
	public static ExpressionNode simplifyExpression(UnaryMinusNode node,
			MidSymbolTable symbolTable) {

		// Note, this applies to cleaning non-literal expressions that reduce to
		// literal expressions
		// E.g.: -(a*0+4) --> -(4) --> -4

		final ExpressionNode expr = node.getOperand().simplify(symbolTable);
		final Canonicalization retCanonicalization = Canonicalization.inv(expr
				.getCanonicalization());

		if (expr instanceof INT_LITERALNode) {
			final long exprValue = ((INT_LITERALNode) expr).getValue();
			return new INT_LITERALNode() {
				{
					setText(Long.toString(-exprValue));
					setCanonicalization(retCanonicalization);
					initializeValue();
					getCallsBeforeExecution()
							.addAll(expr.getAllCallsDuringExecution());
				}
			};
		}
		node.setCanonicalization(retCanonicalization);
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
		node.setLeftOperand(leftNode);
		node.setRightOperand(rightNode);
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

		node.setLeftOperand(leftNode);
		node.setRightOperand(rightNode);
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
							.addAll(opNode.getAllCallsDuringExecution());
				}
			};
		} else if (opNode instanceof FALSENode) {
			return new TRUENode() {
				{
					setText("true");
					getCallsBeforeExecution()
							.addAll(opNode.getAllCallsDuringExecution());
				}
			};
		} else if (opNode instanceof BANGNode) {
			ExpressionNode replNode = ((BANGNode) opNode).getOperand();
			replNode.getCallsBeforeExecution()
					.addAll(0, opNode.getCallsBeforeExecution());
			replNode.getCallsAfterExecution()
					.addAll(opNode.getCallsAfterExecution());
			// Don't need addAll because replNode still has the Operand.
			return replNode;
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
				setCanonicalization(Canonicalization.makeLiteral(node
						.getValue()));
				initializeValue();
				getCallsBeforeExecution()
						.addAll(node.getAllCallsDuringExecution());
			}
		};
	}

	public static ExpressionNode simplifyExpression(INT_LITERALNode node,
			MidSymbolTable symbolTable) {
		node.setCanonicalization(Canonicalization.makeLiteral(node.getValue()));
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

		Canonicalization retCanonicalization = Canonicalization.div(leftOp
				.getCanonicalization(), rightOp.getCanonicalization());
		node.setCanonicalization(retCanonicalization);

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
				newNode.setCanonicalization(Canonicalization
						.makeLiteral(newVal));
				newNode.initializeValue();
				// Update pre-post instructions
				newNode.getCallsBeforeExecution()
						.addAll(leftOp.getAllCallsDuringExecution());
				newNode.getCallsAfterExecution()
						.addAll(rightOp.getAllCallsDuringExecution());
				return newNode;
			}
		} else if (leftOp instanceof INT_LITERALNode) {
			long leftVal = ((INT_LITERALNode) leftOp).getValue();
			if (leftVal == 0 && !rightOp.hasMethodCalls()) {
				return new INT_LITERALNode() {
					{
						setText("0");
						setCanonicalization(Canonicalization.ZERO);
						initializeValue();
						getCallsBeforeExecution()
								.addAll(leftOp.getAllCallsDuringExecution());

						getCallsBeforeExecution()
								.add(new PotentialCheckDivideByZeroNode(
										rightOp, true));

						getCallsAfterExecution()
								.addAll(rightOp.getAllCallsDuringExecution());
					}
				};
			}
		}

		leftOp.getCallsAfterExecution().add(new PotentialCheckDivideByZeroNode(
				rightOp, false));

		node.setLeftOperand(leftOp);
		node.setRightOperand(rightOp);
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

		Canonicalization retCanonicalization = Canonicalization.mod(leftOp
				.getCanonicalization(), rightOp.getCanonicalization());
		node.setCanonicalization(retCanonicalization);

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
						setCanonicalization(Canonicalization.ZERO);
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
				long newVal = leftVal % rightVal;
				INT_LITERALNode newNode = new INT_LITERALNode();
				newNode.setText(Long.toString(newVal));
				newNode.setCanonicalization(Canonicalization
						.makeLiteral(newVal));
				newNode.initializeValue();
				// Update pre-post instructions
				newNode.getCallsBeforeExecution()
						.addAll(leftOp.getAllCallsDuringExecution());
				newNode.getCallsAfterExecution()
						.addAll(rightOp.getAllCallsDuringExecution());
				return newNode;
			}
		} else if (leftOp instanceof INT_LITERALNode) {
			long leftVal = ((INT_LITERALNode) leftOp).getValue();
			if (leftVal == 0 && !rightOp.hasMethodCalls()) {
				return new INT_LITERALNode() {
					{
						setText("0");
						setCanonicalization(Canonicalization.ZERO);

						initializeValue();
						getCallsBeforeExecution()
								.addAll(leftOp.getAllCallsDuringExecution());
						getCallsBeforeExecution()
								.add(new PotentialCheckDivideByZeroNode(
										rightOp, true));

						getCallsAfterExecution()
								.addAll(rightOp.getAllCallsDuringExecution());
					}
				};
			}
		}

		node.setLeftOperand(leftOp);
		node.setRightOperand(rightOp);
		return node;
	}

	@SuppressWarnings("serial")
	public static ExpressionNode simplifyExpression(
			final OpSameSame2BoolNode node, MidSymbolTable symbolTable) {

		final ExpressionNode leftNode = node.getLeftOperand()
				.simplify(symbolTable);
		final ExpressionNode rightNode = node.getRightOperand()
				.simplify(symbolTable);

		if (leftNode instanceof TRUENode && node instanceof EQNode
				|| leftNode instanceof FALSENode && node instanceof NEQNode) {
			rightNode.getCallsBeforeExecution()
					.addAll(0, leftNode.getAllCallsDuringExecution());
			return rightNode;
			// Case 1, true == expr() --> expr(), and false != expr() --> expr()
		} else if (rightNode instanceof TRUENode && node instanceof EQNode
				|| rightNode instanceof FALSENode && node instanceof NEQNode) {
			leftNode.getCallsAfterExecution()
					.addAll(rightNode.getAllCallsDuringExecution());
			leftNode.setNextSibling(null);

			return leftNode;
			// Case 2, expr() == true --> expr(), and expr() != false -->
			// expr();
		} else if (leftNode instanceof FALSENode && node instanceof EQNode
				|| leftNode instanceof TRUENode && node instanceof NEQNode) {
			// Case 3, false == expr() --> !expr(), and true != expr() -->
			// !expr()
			rightNode.getCallsBeforeExecution()
					.addAll(0, leftNode.getAllCallsDuringExecution());

			return new BANGNode() {
				{
					setText("!");
					setFirstChild(rightNode);
				}
			}.simplify(symbolTable);
		} else if (rightNode instanceof FALSENode && node instanceof EQNode
				|| rightNode instanceof TRUENode && node instanceof NEQNode) {
			// Case 4, expr() == false --> !expr(), and expr() != true -->
			// !expr()
			leftNode.getCallsAfterExecution()
					.addAll(rightNode.getAllCallsDuringExecution());
			leftNode.setNextSibling(null);
			return new BANGNode() {
				{
					setText("!");
					setFirstChild(leftNode);
				}
			}.simplify(symbolTable);

		} else if (leftNode instanceof INT_LITERALNode
				&& rightNode instanceof INT_LITERALNode) {
			boolean eq = ((INT_LITERALNode) leftNode).getValue() == ((INT_LITERALNode) rightNode)
					.getValue();
			boolean ret;
			if (node instanceof EQNode) {
				ret = eq;
			} else {
				ret = !eq;
			}
			if (ret) {
				return new TRUENode() {
					{
						setText("true");
						getCallsBeforeExecution()
								.addAll(leftNode.getAllCallsDuringExecution());
						getCallsAfterExecution()
								.addAll(rightNode.getAllCallsDuringExecution());
					}
				};
			}
			return new FALSENode() {
				{
					setText("false");
					getCallsBeforeExecution()
							.addAll(leftNode.getAllCallsDuringExecution());
					getCallsAfterExecution()
							.addAll(rightNode.getAllCallsDuringExecution());
				}
			};
		}
		node.setLeftOperand(leftNode);
		node.setRightOperand(rightNode);
		return node;
	}

	public static ExpressionNode simplifyExpression(IDNode node,
			MidSymbolTable symbolTable) {
		// node.setCanonicalization(Canonicalization.makeVariable(node.getText()));

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
			node.setCanonicalization(Canonicalization.makeArray(node.getText(), expr
					.getCanonicalization()));
			return node;
		}
		node.setCanonicalization(Canonicalization.makeVariable(node.getText()));
		return node;
	}

	public static ExpressionNode simplifyExpression(BooleanNode node,
			MidSymbolTable symbolTable) {
		return node;
	}

	@SuppressWarnings("serial")
	public static ExpressionNode simplifyExpression(OpIntInt2BoolNode node,
			MidSymbolTable symbolTable) {

		final ExpressionNode leftNode = node.getLeftOperand()
				.simplify(symbolTable);
		final ExpressionNode rightNode = node.getRightOperand()
				.simplify(symbolTable);

		if (leftNode instanceof INT_LITERALNode
				&& rightNode instanceof INT_LITERALNode) {
			long leftVal = ((INT_LITERALNode) leftNode).getValue();
			long rightVal = ((INT_LITERALNode) rightNode).getValue();

			boolean ret = false;
			if (node instanceof LTNode) {
				ret = leftVal < rightVal;
			} else if (node instanceof LTENode) {
				ret = leftVal <= rightVal;
			} else if (node instanceof GTNode) {
				ret = leftVal > rightVal;
			} else if (node instanceof GTENode) {
				ret = leftVal >= rightVal;
			} else {
				assert false : "There are no other intint2bools";
			}

			if (ret) {
				return new TRUENode() {
					{
						setText("true");
						getCallsBeforeExecution()
								.addAll(leftNode.getAllCallsDuringExecution());
						getCallsAfterExecution()
								.addAll(rightNode.getAllCallsDuringExecution());
					}
				};
			}
			return new FALSENode() {
				{
					setText("false");
					getCallsBeforeExecution()
							.addAll(leftNode.getAllCallsDuringExecution());
					getCallsAfterExecution()
							.addAll(rightNode.getAllCallsDuringExecution());
				}
			};
		}
		node.setLeftOperand(leftNode);
		node.setRightOperand(rightNode);
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

		Canonicalization retCanonicalization = Canonicalization.mult(leftOp
				.getCanonicalization(), rightOp.getCanonicalization());
		node.setCanonicalization(retCanonicalization);

		if (leftOp instanceof INT_LITERALNode) {
			long leftVal = ((INT_LITERALNode) leftOp).getValue();
			if (rightOp instanceof INT_LITERALNode) {
				long rightVal = ((INT_LITERALNode) rightOp).getValue();
				// Case 1
				long newVal = leftVal * rightVal;
				INT_LITERALNode newNode = new INT_LITERALNode();
				newNode.setText(Long.toString(newVal));
				newNode.setCanonicalization(retCanonicalization);
				newNode.initializeValue();
				// Update pre-post instructions
				newNode.getCallsBeforeExecution()
						.addAll(leftOp.getAllCallsDuringExecution());
				newNode.getCallsAfterExecution()
						.addAll(rightOp.getAllCallsDuringExecution());
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
						setCanonicalization(Canonicalization.ZERO);
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
						setCanonicalization(Canonicalization.ZERO);
						initializeValue();
						getCallsBeforeExecution()
								.addAll(rightOp.getAllCallsDuringExecution());
						getCallsAfterExecution()
								.addAll(leftOp.getAllCallsDuringExecution());
					}
				};
			}
		}
		node.setLeftOperand(leftOp);
		node.setRightOperand(rightOp);
		return node;
	}
}
