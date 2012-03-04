package edu.mit.compilers.crawler;

import edu.mit.compilers.ErrorCenter;
import edu.mit.compilers.crawler.Scope.BlockType;
import edu.mit.compilers.grammar.BranchNode;
import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.grammar.DeclNode;
import edu.mit.compilers.grammar.IDNode;
import edu.mit.compilers.grammar.INT_LITERALNode;

public class SemanticRules {
	
	static public void apply(DecafNode node, Scope scope) {
		if (node instanceof DeclNode) {
			apply((DeclNode) node, scope);
		}
		// TODO: enable this when all rules are done.
		//assert false : "apply on DecafNode should not be called, only its children.";
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
					"Cannot redeclare identifier " + id + ".");
		} else {
			scope.addVar(id, t);
		}

		// Rule 4
		//TODO: This gets caught by the parser... need better error messages at parser level or let it get handled here.
		/*
		if (node.getVarTypeNode().getNumberOfChildren() == 1) {
			assert node.getVarTypeNode().getFirstChild() instanceof INT_LITERALNode;
			INT_LITERALNode intNode = (INT_LITERALNode) node.getVarTypeNode()
					.getFirstChild();
			if (intNode.getValue() < 1) {
				ErrorCenter.reportError(intNode.getLine(), intNode.getColumn(),
						"The dimension of array " + id
								+ " must be greater than 0.");
			}
		}*/

	}

	static public void apply(IDNode node, Scope scope) {
		// Rule 2
		// FIXME: This shouldn't apply to IDNode's of method declarations.
		// TODO: Handle IDNode's that correspond to method calls correctly.
		String id = node.getText();
		if (!scope.seesVar(node.getText())) {
			ErrorCenter.reportError(node.getLine(), node.getColumn(),
					"Cannot access identifier " + id + " before declaration.");
		}
	}
	static public void apply(BranchNode node, Scope scope){
		System.out.println("Rule 18.");
		//Rule 18.
		Scope currentScope = scope;
		while(currentScope != null){
			switch(currentScope.getBlockType()){
			case WHILE: case FOR:
				break;
			default:
				currentScope = currentScope.getParent();
			}
		}
		if(currentScope == null){
			ErrorCenter.reportError(node.getLine(), node.getColumn(),
					"Cannot call " + node.getText() + " from outside a while/for loop.");
		}
	}
}
