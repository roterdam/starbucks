package edu.mit.compilers.crawler;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.ErrorCenter;
import edu.mit.compilers.crawler.Scope.BlockType;
import edu.mit.compilers.grammar.BranchNode;
import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.grammar.DeclNode;
import edu.mit.compilers.grammar.ExpressionNode;
import edu.mit.compilers.grammar.ModifyAssignNode;
import edu.mit.compilers.grammar.expressions.OpBool2BoolNode;
import edu.mit.compilers.grammar.expressions.OpBoolBool2BoolNode;
import edu.mit.compilers.grammar.expressions.OpInt2IntNode;
import edu.mit.compilers.grammar.expressions.OpIntInt2BoolNode;
import edu.mit.compilers.grammar.expressions.OpIntInt2IntNode;
import edu.mit.compilers.grammar.expressions.OpSameSame2BoolNode;
import edu.mit.compilers.grammar.tokens.ASSIGNNode;
import edu.mit.compilers.grammar.tokens.BLOCKNode;
import edu.mit.compilers.grammar.tokens.CLASSNode;
import edu.mit.compilers.grammar.tokens.FIELD_DECLNode;
import edu.mit.compilers.grammar.tokens.FOR_INITIALIZENode;
import edu.mit.compilers.grammar.tokens.FOR_TERMINATENode;
import edu.mit.compilers.grammar.tokens.IDNode;
import edu.mit.compilers.grammar.tokens.IF_CLAUSENode;
import edu.mit.compilers.grammar.tokens.INT_LITERALNode;
import edu.mit.compilers.grammar.tokens.METHOD_CALLNode;
import edu.mit.compilers.grammar.tokens.METHOD_DECLNode;
import edu.mit.compilers.grammar.tokens.METHOD_IDNode;
import edu.mit.compilers.grammar.tokens.RETURNNode;
import edu.mit.compilers.grammar.tokens.WHILE_TERMINATENode;

public class SemanticRules {
	public static String MAIN = "main";
	static String REDECLARE_IDENTIFIER = "Cannot redeclare identifier `%1$s`.";
	static String ID_BEFORE_DECLARATION = "Cannot access identifier `%1$s` before declaration.";
	static String UNALLOWED_JUMP = "Cannot call `%1$s` from outside a while/for loop.";
	static String REDECLARE_METHOD = "Cannot declare method `%1$s`; variable or field `%1$s` already exists.";
	static String MISSING_MAIN = "Program must contain definition for 'main' with no parameters.";
	static String INCORRECT_MAIN = "Program must contain definition for 'main' with no parameters; %1$s found instead.";
	static String METHOD_ARGS = "Cannot call method `%1$s` with arguments `%2$s`. Expected %3$s.";
	static String RETURN_TYPE = "Return type mismatch. Expected `%1$s` but got `%2$s` instead.";
	static String ARRAY_INDEX_TYPE = "Invalid array index: expected "
			+ VarType.INT.name() + ", found %1$s instead.";
	static String INVALID_CLASS_NAME = "The class must be named `Program`. It is currently `%1$s`.";
	static String METHOD_BEFORE_DECLARATION = "Method `%1$s` is not visible or valid.";
	static String INVALID_ARRAY_ACCESS = "Cannot access `%1$s` as an array: `%1$s` has type %2$s.";
	static String ARRAY_INDEX_NEGATIVE = "Size of array `%1$s` cannot be negative.";
	static String INT_OPERAND_ERROR = "Incorrect use of `%1$s` operator. Expecting <"
			+ VarType.INT.name() + ">, found <%2$s>.";
	static String FOR_LOOP_TERMINATE_INT = "For loop termination condition must be an ["
			+ VarType.INT.name() + "], found [%1$s].";
	static String FOR_LOOP_INIT_INT = "For loop initial condition must be ["
			+ VarType.INT.name() + "], found [%1$s].";
	static String OP_SAME_SAME_BAD_TYPE = "Incorrect use of equality operator. Expecting ["
			+ VarType.INT + "] or [" + VarType.BOOLEAN + "], found `%1$s`.";
	static String OP_SAME_SAME_NOT_SAME_TYPE = "Comparison type mismatch. Expecting ["
			+ VarType.INT
			+ ","
			+ VarType.INT
			+ "] or ["
			+ VarType.BOOLEAN
			+ "," + VarType.BOOLEAN + "]. Found `%1$s` `%2$s`.";
	static String ASSIGN_EXPRESSION_WRONG_TYPE = "Cannot assign %1$s value to %2$s `%3$s`.";
	static String MODIFY_ASSIGN_EXPRESSION_WRONG_TYPE1 = "Cannot increment/decrement `%1$s`. Expecting ["
			+ VarType.INT.name() + "], found [%2$s].";
	static String MODIFY_ASSIGN_EXPRESSION_WRONG_TYPE2 = "Cannot increment/decrement. Expecting ["
			+ VarType.INT.name() + "], found [%1$s].";
	static String OP_EQ_COND_BAD_TYPE_ERROR = "Incorrect use of conditional or logical not operator. Expecting `%1$s` found `%2$s`.";
	static String IF_EXPR_BOOL_ERROR = "The if loop requires ["
			+ VarType.BOOLEAN + "].";
	static String WHILE_EXPR_BOOL_ERROR = "Ill-formed while loop. Expecting ["
			+ VarType.BOOLEAN.name() + "], found [%1$s].";
	static String INTEGER_OUT_OF_BOUNDS = "Int literal `%1$s` out of bounds.";
	static String OP_UNARY_MINUS_TYPE_ERROR = "Unary minus operator type error. Expecting `%1$s` found `%2$s`.";
	static String DOES_NOT_RETURN = "Method `%1$s` might not return a value. Be sure all branching logic results in a return statement.";

	static public void apply(DecafNode node, Scope scope) {
		// assert false :
		// "apply on DecafNode should not be called, only its children. " +
		// node.toStringTree();
	}

	static public void apply(INT_LITERALNode node, Scope scope) {
		if (!node.isWithinBounds()) {
			ErrorCenter.reportError(node.getLine(), node.getColumn(), String
					.format(INTEGER_OUT_OF_BOUNDS, node.getText()));
		}

	}

	static public void apply(DeclNode node, Scope scope) {
		// Rule 1, Rule 9
		IDNode idNode = node.getIDNode();
		String id = idNode.getText();
		VarType t = node.getVarType();

		// FIELD_DECLs are not allowed to shadow methods, other DECLs are.
		if ((node instanceof FIELD_DECLNode && scope.hasSymbol(id))
				|| (scope.hasLocalVar(id))) {
			ErrorCenter
					.reportError(idNode.getLine(), idNode.getColumn(), String
							.format(REDECLARE_IDENTIFIER, id));
		} else {
			scope.addVar(id, new VarDecl(t, id, idNode.getLine(), idNode
					.getColumn()));
		}

		// Rule 4
		// If the node's VarTypeNode has children, check the array size.
		if (node.getVarTypeNode().isArray()) {
			INT_LITERALNode intNode = node.getVarTypeNode().getIntLiteralNode();
			if (!intNode.isPositive()) {
				ErrorCenter
						.reportError(intNode.getLine(), intNode.getColumn(), String
								.format(ARRAY_INDEX_NEGATIVE, id));
			}
		}

	}

	static public void apply(IDNode node, Scope scope) {
		// Rule 2
		String id = node.getText();
		if (node.getReturnType(scope) == VarType.UNDECLARED) {
			ErrorCenter.reportError(node.getLine(), node.getColumn(), String
					.format(ID_BEFORE_DECLARATION, id));
		}

		// Rule 10
		assert node.getNumberOfChildren() <= 1;

		// If there's a child, it must be array access, i.e. a[5]
		if (node.isArray()) {
			// Check that the IDNode is an array.
			if (scope.getType(id) != VarType.INT_ARRAY
					&& scope.getType(id) != VarType.BOOLEAN_ARRAY) {
				ErrorCenter
						.reportError(node.getLine(), node.getColumn(), String
								.format(INVALID_ARRAY_ACCESS, id, scope
										.getType(id)));
				return;
			}
			// Check that the index is an INT.
			ExpressionNode indexNode = node.getExpressionNode();
			VarType indexType = indexNode.getReturnType(scope);
			if (indexType != VarType.INT) {
				ErrorCenter.reportError(indexNode.getLine(), indexNode
						.getColumn(), String.format(ARRAY_INDEX_TYPE, indexType
						.name()));
			}
		}
	}

	static public void apply(BranchNode node, Scope scope) {
		// Rule 18.
		Scope currentScope = scope;
		while (currentScope != null) {
			if (currentScope.getBlockType() == BlockType.WHILE
					|| currentScope.getBlockType() == BlockType.FOR) {
				break;
			}
			currentScope = currentScope.getParent();
		}
		if (currentScope == null) {
			ErrorCenter.reportError(node.getLine(), node.getColumn(), String
					.format(UNALLOWED_JUMP, node.getText()));
		}
	}

	static public void apply(METHOD_DECLNode node, Scope scope) {
		VarType returnType = node.getReturnType();
		String id = node.getId();
		List<VarType> params = node.getParamNodes();
		// Don't allow shadowing existing methods or fields.
		if (scope.hasSymbol(id)) {
			ErrorCenter.reportError(node.getLine(), node.getColumn(), String
					.format(REDECLARE_METHOD, id));
		} else {
			scope.getMethods().put(id, new MethodDecl(returnType, id, params,
					node.getLine(), node.getColumn()));
		}

		if (returnType == VarType.VOID) {
			return;
		}
		// Check for valid return statement.
		ValidReturnChecker returnChecker = new ValidReturnChecker(returnType);
		if (!node.hasValidReturn(returnChecker)) {
			ErrorCenter.reportError(node.getLine(), node.getColumn(), String
					.format(DOES_NOT_RETURN, id));
		}
	}

	static public void apply(RETURNNode node, Scope scope) {
		if (node.getReturnType(scope) != scope.getReturnType()) {
			ErrorCenter.reportError(node.getLine(), node.getColumn(), String
					.format(RETURN_TYPE, scope.getReturnType(), node
							.getReturnType(scope)));
		}
	}

	static public void apply(CLASSNode node, Scope scope) {
		IDNode idNode = node.getIdNode();
		if (!idNode.getText().equals("Program")) {
			ErrorCenter
					.reportError(idNode.getLine(), idNode.getColumn(), String
							.format(INVALID_CLASS_NAME, idNode.getText()));
		}
	}

	static public void finalApply(CLASSNode node, Scope scope) {
		// Rule 3.
		if (!scope.getMethods().containsKey(MAIN)) {
			ErrorCenter.reportError(0, 0, MISSING_MAIN);
		} else {
			MethodDecl mainDecl = scope.getMethods().get(MAIN);
			if (mainDecl.getParams().size() != 0) {
				StringBuilder paramsStringBuilder = new StringBuilder();
				paramsStringBuilder.append("<");
				for (int i = 0; i < mainDecl.getParams().size(); i++) {
					paramsStringBuilder.append(mainDecl.getParams().get(i));
					if (i != mainDecl.getParams().size() - 1) {
						paramsStringBuilder.append(", ");
					}
				}
				paramsStringBuilder.append(">");
				ErrorCenter
						.reportError(mainDecl.getLine(), mainDecl.getColumn(), String
								.format(INCORRECT_MAIN, paramsStringBuilder
										.toString()));
			}
		}
	}

	static public void finalApply(BLOCKNode node, Scope scope) {
		// Rule 6

	}

	static public void apply(METHOD_CALLNode node, Scope scope) {
		// Rule 2b
		assert node.getNumberOfChildren() > 0;

		METHOD_IDNode methodIdNode = node.getMethodIdNode();
		String methodName = node.getName();
		if (scope.hasMethod(methodName)) {
			// Rule 5
			// Construct a list of passed in parameters.
			List<ExpressionNode> args = node.getParamNodes();

			List<VarType> argTypes = new ArrayList<VarType>();
			for (ExpressionNode argNode : node.getParamNodes()) {
				argTypes.add(argNode.getReturnType(scope));
			}

			MethodDecl method = scope.getMethods().get(methodName);
			List<VarType> params = method.getParams();

			if (reportErrorForParams(params, argTypes)) {
				ErrorCenter
						.reportError(methodIdNode.getLine(), methodIdNode
								.getColumn(), String
								.format(METHOD_ARGS, methodName, args
										.toString(), params.toString()));
			}
		} else {
			ErrorCenter.reportError(node.getLine(), node.getColumn(), String
					.format(METHOD_BEFORE_DECLARATION, methodName));
		}
	}

	private static boolean reportErrorForParams(List<VarType> params,
			List<VarType> args) {
		// Silently fail for undeclared variables
		for (VarType type : args) {
			if (type == VarType.UNDECLARED)
				return false;
		}

		if (params.size() != args.size()) {
			return true;
		}

		for (int i = 0; i < params.size(); i++) {
			if (args.get(i) != params.get(i))
				return true;
		}
		return false;
	}

	// the exact same as intint2bool
	static public void apply(OpIntInt2IntNode node, Scope scope) {
		// Rule 12
		assert node.getNumberOfChildren() == 2 : node.toStringTree();
		assert node.getFirstChild() instanceof ExpressionNode;
		assert node.getFirstChild().getNextSibling() instanceof ExpressionNode;

		ExpressionNode leftOperand = node.getLeftOperand();
		ExpressionNode rightOperand = node.getRightOperand();

		VarType leftType = leftOperand.getReturnType(scope);
		VarType rightType = rightOperand.getReturnType(scope);

		// Silently fail if variable is undeclared
		if (leftType != VarType.UNDECLARED && leftType != VarType.INT) {
			ErrorCenter.reportError(leftOperand.getLine(), leftOperand
					.getColumn(), String.format(INT_OPERAND_ERROR, node
					.getText(), leftType));
		}
		if (rightType != VarType.UNDECLARED && rightType != VarType.INT) {
			ErrorCenter.reportError(rightOperand.getLine(), rightOperand
					.getColumn(), String.format(INT_OPERAND_ERROR, node
					.getText(), rightType));
		}
	}

	static public void apply(OpSameSame2BoolNode node, Scope scope) {
		// Rule 13
		assert node.getNumberOfChildren() == 2;
		assert node.getChild(0) instanceof ExpressionNode;
		assert node.getChild(1) instanceof ExpressionNode;

		ExpressionNode leftOperand = node.getLeftOperand();
		ExpressionNode rightOperand = node.getRightOperand();

		VarType firstType = leftOperand.getReturnType(scope);
		VarType secondType = rightOperand.getReturnType(scope);

		// Could get away only checking first type but doing both in order to
		// identify the second token as error causing.
		// Probably ugly to call report error multiple times, store in temp
		// variable and call at the end if exists.

		if (firstType != VarType.UNDECLARED && secondType != VarType.UNDECLARED) {
			if (firstType != VarType.INT && firstType != VarType.BOOLEAN) {
				ErrorCenter.reportError(leftOperand.getLine(), leftOperand
						.getColumn(), String
						.format(OP_SAME_SAME_BAD_TYPE, firstType));
			} else if (secondType != VarType.INT
					&& secondType != VarType.BOOLEAN) {
				ErrorCenter.reportError(rightOperand.getLine(), rightOperand
						.getColumn(), String
						.format(OP_SAME_SAME_BAD_TYPE, secondType));
			} else if (firstType != secondType) {
				ErrorCenter
						.reportError(rightOperand.getLine(), rightOperand
								.getColumn(), String
								.format(OP_SAME_SAME_NOT_SAME_TYPE, firstType, secondType));
			}
		}
	}

	static public void apply(OpBool2BoolNode node, Scope scope) {
		// Rule 14
		assert node.getNumberOfChildren() == 1;
		assert node.getFirstChild() instanceof ExpressionNode;

		ExpressionNode operand = node.getOperand();
		VarType type = operand.getReturnType(scope);

		// Silently fail if variable is undeclared
		if (type != VarType.UNDECLARED && type != VarType.BOOLEAN) {
			ErrorCenter
					.reportError(operand.getLine(), operand.getColumn(), String
							.format(OP_EQ_COND_BAD_TYPE_ERROR, VarType.BOOLEAN, type));
		}
	}

	static public void apply(OpBoolBool2BoolNode node, Scope scope) {
		// Rule 14
		assert node.getNumberOfChildren() == 2;
		assert node.getFirstChild() instanceof ExpressionNode;
		assert node.getFirstChild().getNextSibling() instanceof ExpressionNode;

		ExpressionNode leftOperand = node.getLeftOperand();
		ExpressionNode rightOperand = node.getRightOperand();

		VarType leftType = leftOperand.getReturnType(scope);
		VarType rightType = rightOperand.getReturnType(scope);

		// Silently fail if variable is undeclared
		if (leftType != VarType.UNDECLARED && leftType != VarType.BOOLEAN) {
			ErrorCenter.reportError(leftOperand.getLine(), leftOperand
					.getColumn(), String.format(OP_EQ_COND_BAD_TYPE_ERROR, node
					.getText(), leftType));
		}
		if (rightType != VarType.UNDECLARED && rightType != VarType.BOOLEAN) {
			ErrorCenter.reportError(rightOperand.getLine(), rightOperand
					.getColumn(), String.format(OP_EQ_COND_BAD_TYPE_ERROR, node
					.getText(), rightType));
		}
	}

	static public void apply(OpInt2IntNode node, Scope scope) {
		assert node.getNumberOfChildren() == 1;
		assert node.getFirstChild() instanceof ExpressionNode;

		ExpressionNode operand = node.getOperand();
		VarType type = operand.getReturnType(scope);

		// Silently fail if variable is undeclared
		if (type != VarType.UNDECLARED && type != VarType.INT) {
			ErrorCenter
					.reportError(operand.getLine(), operand.getColumn(), String
							.format(OP_UNARY_MINUS_TYPE_ERROR, VarType.INT, type));
		}
	}

	static public void apply(OpIntInt2BoolNode node, Scope scope) {
		// Rule 12
		assert node.getNumberOfChildren() == 2;
		assert node.getFirstChild() instanceof ExpressionNode;
		assert node.getFirstChild().getNextSibling() instanceof ExpressionNode;

		ExpressionNode leftOperand = node.getLeftOperand();
		ExpressionNode rightOperand = node.getRightOperand();

		VarType leftType = leftOperand.getReturnType(scope);
		VarType rightType = rightOperand.getReturnType(scope);

		// Silently fail if variable is undeclared
		if (leftType != VarType.UNDECLARED && leftType != VarType.INT) {
			ErrorCenter.reportError(leftOperand.getLine(), leftOperand
					.getColumn(), String.format(INT_OPERAND_ERROR, node
					.getText(), leftType));
		}
		if (rightType != VarType.UNDECLARED && rightType != VarType.INT) {
			ErrorCenter.reportError(rightOperand.getLine(), rightOperand
					.getColumn(), String.format(INT_OPERAND_ERROR, node
					.getText(), rightType));
		}
	}

	static public void apply(ASSIGNNode node, Scope scope) {
		// Rule 15
		IDNode idNode = node.getLocation();
		ExpressionNode val = node.getExpression();

		VarType leftType = idNode.getReturnType(scope);
		VarType rightType = val.getReturnType(scope);

		// Silently fail if variable is undeclared
		if (leftType != VarType.UNDECLARED && rightType != VarType.UNDECLARED
				&& leftType != rightType) {
			ErrorCenter
					.reportError(val.getLine(), val.getColumn(), String
							.format(ASSIGN_EXPRESSION_WRONG_TYPE, rightType, leftType, idNode
									.getRepresentation()));
		}
	}

	static public void apply(ModifyAssignNode node, Scope scope) {
		// Rule 16
		IDNode idNode = node.getLocation();
		ExpressionNode val = node.getExpression();

		VarType leftType = idNode.getReturnType(scope);
		VarType rightType = val.getReturnType(scope);

		// Silently fail if variable is undeclared
		if (leftType != VarType.UNDECLARED && leftType != VarType.INT) {
			ErrorCenter
					.reportError(idNode.getLine(), idNode.getColumn(), String
							.format(MODIFY_ASSIGN_EXPRESSION_WRONG_TYPE1, idNode
									.getRepresentation(), leftType));
		}
		if (rightType != VarType.UNDECLARED && rightType != VarType.INT) {
			ErrorCenter.reportError(val.getLine(), val.getColumn(), String
					.format(MODIFY_ASSIGN_EXPRESSION_WRONG_TYPE2, rightType));
		}

	}

	static public void apply(FOR_TERMINATENode node, Scope scope) {
		// Rule 17

		ExpressionNode expr = node.getExpressionNode();
		VarType returnType = expr.getReturnType(scope);

		if (returnType != VarType.UNDECLARED && returnType != VarType.INT) {
			ErrorCenter.reportError(expr.getLine(), expr.getColumn(), String
					.format(FOR_LOOP_TERMINATE_INT, returnType));
		}
	}

	static public void apply(FOR_INITIALIZENode node, Scope scope) {
		// Rule 17

		assert node.getAssignNode().getNumberOfChildren() == 2;
		assert node.getAssignNode().getFirstChild() instanceof IDNode;
		assert node.getAssignNode().getChild(1) instanceof ExpressionNode;

		ASSIGNNode assignNode = node.getAssignNode();
		VarType returnType = assignNode.getExpression().getReturnType(scope);
		if (returnType != VarType.UNDECLARED && returnType != VarType.INT) {
			ErrorCenter.reportError(assignNode.getLine(), assignNode
					.getColumn(), String.format(FOR_LOOP_INIT_INT, returnType));
		}
	}

	// TODO(saif): Raise IF_CLAUSENode and WHILE_TERMINATENode to common class
	static public void apply(IF_CLAUSENode node, Scope scope) {
		// Rule 11

		VarType returnType = node.getExpressionNode().getReturnType(scope);
		if (returnType != VarType.UNDECLARED && returnType != VarType.BOOLEAN) {
			ErrorCenter.reportError(node.getExpressionNode().getLine(), node
					.getExpressionNode().getColumn(), String
					.format(IF_EXPR_BOOL_ERROR, returnType));
		}
	}

	static public void apply(WHILE_TERMINATENode node, Scope scope) {
		// Rule 11
		assert node.getNumberOfChildren() == 1;
		assert node.getFirstChild() instanceof ExpressionNode;

		ExpressionNode expr = node.getExpressionNode();
		VarType returnType = expr.getReturnType(scope);
		if (returnType != VarType.UNDECLARED && returnType != VarType.BOOLEAN) {
			ErrorCenter.reportError(expr.getLine(), expr.getColumn(), String
					.format(WHILE_EXPR_BOOL_ERROR, returnType));
		}
	}

}
