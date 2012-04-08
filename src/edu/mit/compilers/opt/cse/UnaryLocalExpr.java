package edu.mit.compilers.opt.cse;

import edu.mit.compilers.codegen.nodes.regops.MidUnaryRegNode;
import edu.mit.compilers.opt.Value;

/**
 * This expression class maps values, so v1 + v2 = expr. See SymbolicExpr for
 * the Global CSE class.
 */
public class UnaryLocalExpr extends LocalExpr {

	private Value v1;
	private MidUnaryRegNode node;

	public UnaryLocalExpr(Value v1, MidUnaryRegNode node) {
		this.v1 = v1;
		this.node = node;
	}

	/**
	 * Canonicalization happens here.
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof UnaryLocalExpr)) {
			return false;
		}
		UnaryLocalExpr e = (UnaryLocalExpr) o;
		if (!e.getNodeClass().equals(this.getNodeClass())) {
			return false;
		}
		return e.getV1() == v1;
	}

	@Override
	public int hashCode() {
		return node.hashCode() + v1.hashCode();
	}

	public Value getV1() {
		return v1;
	}

	public String getNodeClass() {
		return node.getNodeClass();
	}

}
