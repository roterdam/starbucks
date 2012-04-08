package edu.mit.compilers.opt.cse;


/**
 * This expression class maps values, so v1 + v2 = expr. See SymbolicExpr for
 * the Global CSE class.
 */
public abstract class LocalExpr {

	/**
	 * Canonicalization happens here.
	 */
	@Override
	public abstract boolean equals(Object o);

	@Override
	public abstract int hashCode();

	public abstract String getNodeClass();
}
