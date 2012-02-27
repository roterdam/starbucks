package edu.mit.compilers.crawler;

import antlr.collections.AST;

public abstract class ASTRunnable {

	/**
	 * Runs a given action on the AST.
	 * 
	 * @param root
	 *            Node to run action on.
	 * @return Object storing extra information about the node, null if none
	 *         available.
	 */
	abstract public Object run(AST root);

}
