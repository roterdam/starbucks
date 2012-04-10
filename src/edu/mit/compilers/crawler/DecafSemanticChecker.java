package edu.mit.compilers.crawler;

import edu.mit.compilers.crawler.Scope.BlockType;
import edu.mit.compilers.grammar.tokens.CLASSNode;

public class DecafSemanticChecker {
	Scope scope;

	public void crawl(CLASSNode root) {
		// Clean tree, manipulating nodes as necessary.
		root.clean();
		
		scope = new Scope(BlockType.CLASS);
		root.validate(scope); // FIXME: this is calling decaf node's
		SemanticRules.finalApply(root, scope);
		
	}
}
