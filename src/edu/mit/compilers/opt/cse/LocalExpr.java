package edu.mit.compilers.opt.cse;

import edu.mit.compilers.opt.Value;

/**
 * This expression class maps values, so v1 + v2 = expr. See SymbolicExpr for
 * the Global CSE class.
 */
public class LocalExpr {

	private Value v1;
	private Value v2;
	private String nodeClass;

	public LocalExpr(Value v1, Value v2, String nodeClass) {
		this.v1 = v1;
		this.v2 = v2;
		this.nodeClass = nodeClass;
	}

	/**
	 * Canonicalization happens here.
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof LocalExpr)) {
			return false;
		}
		LocalExpr e = (LocalExpr) o;
		// TODO: THIS IS INCORRECT. Non-commutative operations can't assume
		// swapping order doesn't matter, for example.
		boolean equalArgs = (e.getV1() == v1 && e.getV2() == v2)
				|| (e.getV1() == v2 && e.getV2() == v1);
		return (equalArgs && e.getNodeClass().equals(nodeClass));
	}

	@Override
	public int hashCode() {
		return v1.hashCode() + v2.hashCode();
	}

	public Value getV1() {
		return v1;

	}

	public Value getV2() {
		return v2;
	}

	public String getNodeClass() {
		return nodeClass;
	}

}
