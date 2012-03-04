package edu.mit.compilers.crawler;

import java.util.List;

import edu.mit.compilers.ErrorCenter;
import edu.mit.compilers.crawler.Scope.BlockType;
import edu.mit.compilers.grammar.BranchNode;
import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.grammar.DeclNode;
import edu.mit.compilers.grammar.tokens.CLASSNode;
import edu.mit.compilers.grammar.tokens.IDNode;
import edu.mit.compilers.grammar.tokens.METHOD_CALLNode;
import edu.mit.compilers.grammar.tokens.METHOD_DECLNode;
import edu.mit.compilers.grammar.tokens.METHOD_IDNode;

public class SemanticRules {

	static String REDECLARE_IDENTIFIER_ERROR = "Cannot redeclare identifier %1$s.";
	static String ID_BEFORE_DECLARATION_ERROR = "Cannot access identifier %1$s before declaration.";
	static String UNALLOWED_JUMP_ERROR = "Cannot call %1$s from outside a while/for loop.";
	static String REDECLARE_METHOD_ERROR = "Cannot redeclare method %1$s.";
	static String MISSING_MAIN_ERROR = "Program must contain definition for 'main' with no parameters.";
	static String INCORRECT_MAIN_ERROR = "Program must contain definition for 'main' with no parameters; %1$s found instead.";

	static public void apply(DecafNode node, Scope scope) {
		if (node instanceof METHOD_DECLNode) {
			apply((METHOD_DECLNode) node, scope);
			return;
		}
		if (node instanceof METHOD_CALLNode) {
			apply((METHOD_CALLNode) node, scope);
			return;
		}
		if (node instanceof DeclNode) {
			apply((DeclNode) node, scope);
			return;
		}
		if (node instanceof IDNode) {
			apply((IDNode) node, scope);
			return;
		}
		if (node instanceof BranchNode) {
			apply((BranchNode) node, scope);
			return;
		}
		
		if (node instanceof CLASSNode) {
			apply((CLASSNode) node, scope);
			return;
		}

		// TODO: enable this when all rules are done.
		// assert false :
		// "apply on DecafNode should not be called, only its children.";
		return;
	}

	static public void apply(DeclNode node, Scope scope) {
		// Rule 1, Rule 9
		// TODO: Should apply to methods also
		IDNode idNode = node.getIDNode();
		String id = idNode.getText();
		VarType t = node.getVarType();

		if (scope.hasVar(id)) {
			// TODO: Also store where the original ID was declared.
			ErrorCenter.reportError(idNode.getLine(), idNode.getColumn(),
					String.format(REDECLARE_IDENTIFIER_ERROR, id));
		} else {
			scope.addVar(id,
					new VarDecl(t, id, idNode.getLine(), idNode.getColumn()));
		}

		// Rule 4
		// TODO: This gets caught by the parser... need better error messages at
		// parser level or let it get handled here.
		/*
		 * if (node.getVarTypeNode().getNumberOfChildren() == 1) { assert
		 * node.getVarTypeNode().getFirstChild() instanceof INT_LITERALNode;
		 * INT_LITERALNode intNode = (INT_LITERALNode) node.getVarTypeNode()
		 * .getFirstChild(); if (intNode.getValue() < 1) {
		 * ErrorCenter.reportError(intNode.getLine(), intNode.getColumn(),
		 * "The dimension of array " + id + " must be greater than 0."); } }
		 */

	}

	static public void apply(IDNode node, Scope scope) {
		// Rule 2
		String id = node.getText();
		if (!scope.seesVar(node.getText())) {
			ErrorCenter.reportError(node.getLine(), node.getColumn(),
					String.format(ID_BEFORE_DECLARATION_ERROR, id));
		}
	}

	static public void apply(BranchNode node, Scope scope) {
		// Rule 18.
		Scope currentScope = scope;
		while (currentScope != null) {
			if (currentScope.getBlockType() == BlockType.WHILE
					|| currentScope.getBlockType() == BlockType.FOR) {
				break;
			} else {
				currentScope = currentScope.getParent();
			}
		}
		if (currentScope == null) {
			ErrorCenter.reportError(node.getLine(), node.getColumn(),
					String.format(UNALLOWED_JUMP_ERROR, node.getText()));
		}
	}

	static public void apply(METHOD_DECLNode node, Scope scope) {
		VarType returnType = node.getReturnType();
		String id = node.getId();
		List<VarType> params = node.getParams();
		if (scope.getMethods().containsKey(id)) {
			ErrorCenter.reportError(node.getLine(), node.getColumn(),
					String.format(REDECLARE_METHOD_ERROR, id));
		} else {
			scope.getMethods().put(
					id,
					new MethodDecl(returnType, id, params, node.getLine(), node
							.getColumn()));
		}
	}

	static public void apply(CLASSNode node, Scope scope) {
		DecafNode child = node.getFirstChild();
		if (!child.getText().equals("Program")){
			ErrorCenter.reportError(child.getLine(), child.getColumn(), "The class must be named `Program`. It is currently `"+child.getText()+"`");
		}
	}
	
	static public void finalApply(CLASSNode node, Scope scope){
		// Rule 3.
		if (!scope.getMethods().containsKey("main")) {
			ErrorCenter.reportError(1, 1, MISSING_MAIN_ERROR);
		} else {
			MethodDecl mainDecl = scope.getMethods().get("main");
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
				ErrorCenter.reportError(
						mainDecl.getLine(),
						mainDecl.getColumn(),
						String.format(INCORRECT_MAIN_ERROR,
								paramsStringBuilder.toString()));
			}
		}
	}

	static public void apply(METHOD_CALLNode node, Scope scope) {
		// Rule 2b
		assert node.getNumberOfChildren() > 0;
		assert node.getChild(0) instanceof METHOD_IDNode;

		String methodName = node.getChild(0).getText();

		if (scope.getMethods().containsKey(methodName)) {
			MethodDecl method = scope.getMethods().get(methodName);
			// TODO: finish compare lists.
		} else {
			ErrorCenter
					.reportError(node.getLine(), node.getColumn(),
							"Cannot call method " + methodName
									+ " before declaration.");
		}
	}
}
