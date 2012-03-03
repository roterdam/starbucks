package edu.mit.compilers.crawler;

import antlr.debug.misc.ASTFrame;
import edu.mit.compilers.grammar.CLASSNode;

public class DecafSemanticChecker {
	Scope scope;
	
	public void crawl(CLASSNode root) {
		// For debugging.
		System.out.println(root.toStringTree());
		ASTFrame frame = new ASTFrame("6.035", root);
		frame.setVisible(true);
		
		scope = new Scope();
		root.validate(scope);
	}
	
}
