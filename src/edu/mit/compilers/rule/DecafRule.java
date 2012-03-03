package edu.mit.compilers.rule;

import edu.mit.compilers.grammar.DecafNode;


public abstract class DecafRule {

	/**
	 * Runs a given action on the AST.
	 * 
	 * @param root
	 *            Node to run action on.
	 * @return Object storing extra information about the node, null if none
	 *         available.
	 */
	abstract public Object run(DecafNode root);

}
