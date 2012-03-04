package edu.mit.compilers.crawler;

import edu.mit.compilers.ErrorCenter;
import edu.mit.compilers.grammar.DeclNode;

public class SemanticRules {

	static public void apply(DeclNode node, Scope scope) {
		// Rule 1
		String id = node.getIDNode().getText();
		VarType t = node.getVarType();

		if (scope.hasVar(id)) {
			// TODO: Also store where the original ID was declared.
			ErrorCenter.reportError(node.getIDNode().getLine(), node.getIDNode().getColumn(),
					"Cannot redeclare identifier " + id + ".");
		} else {
			scope.addVar(id, t);
		}
	}
	
}
