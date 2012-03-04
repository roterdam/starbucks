package edu.mit.compilers.crawler;

import antlr.debug.misc.ASTFrame;
import edu.mit.compilers.crawler.Scope.BlockType;
import edu.mit.compilers.grammar.CLASSNode;

public class DecafSemanticChecker {
	Scope scope;

	public void crawl(CLASSNode root) {
		// Clean tree, manipulating nodes as necessary.
		root.clean();
		
		// For debugging.
		System.out.println(root.toStringTree());
		ASTFrame frame = new ASTFrame("6.035", root);
		frame.setVisible(true);

		scope = new Scope(BlockType.CLASS);
		root.validate(scope);
		SemanticRules.finalApply(root, scope);
	}

}
