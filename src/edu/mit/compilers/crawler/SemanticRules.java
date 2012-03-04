package edu.mit.compilers.crawler;

import edu.mit.compilers.ErrorCenter;
import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.grammar.DeclNode;
import edu.mit.compilers.grammar.IDNode;

public class SemanticRules {
	
	static public void apply(DecafNode node, Scope scope) {
		// TODO: enable this when all rules are done.
		//assert false : "apply on DecafNode should not be called, only its children.";
		return;
	}

	static public void apply(DeclNode node, Scope scope) {
		// Rule 1
		IDNode idNode = node.getIDNode();
		String id = idNode.getText();
		VarType t = node.getVarType();
		
		System.out.println("Rule-checking DeclNode: " + id + " at line " + idNode.getLine());

		if (scope.hasVar(id)) {
			// TODO: Also store where the original ID was declared.
			ErrorCenter.reportError(idNode.getLine(), idNode.getColumn(),
					"Cannot redeclare identifier " + id + ".");
		} else {
			scope.addVar(id, t);
		}
	}
	
}
