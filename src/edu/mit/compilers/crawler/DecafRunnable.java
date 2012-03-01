package edu.mit.compilers.crawler;

import edu.mit.compilers.grammar.DecafAST;


public abstract class DecafRunnable {

	/**
	 * Runs a given action on the AST.
	 * 
	 * @param root
	 *            Node to run action on.
	 * @return Object storing extra information about the node, null if none
	 *         available.
	 */
	abstract public Object run(DecafAST root);

}
