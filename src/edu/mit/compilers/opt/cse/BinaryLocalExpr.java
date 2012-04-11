package edu.mit.compilers.opt.cse;

import edu.mit.compilers.codegen.nodes.regops.MidArithmeticNode;
import edu.mit.compilers.opt.Value;

/**
 * This expression class maps values, so v1 + v2 = expr. See SymbolicExpr for
 * the Global CSE class.
 */
public class BinaryLocalExpr extends LocalExpr {

	private Value v1;
	private Value v2;
	private MidArithmeticNode node;

	public BinaryLocalExpr(Value v1, Value v2, MidArithmeticNode node) {
		this.v1 = v1;
		this.v2 = v2;
		this.node = node;
	}

	/**
	 * Canonicalization happens here.
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof BinaryLocalExpr)) {
			return false;
		}
		BinaryLocalExpr e = (BinaryLocalExpr) o;
		if (!e.getNodeClass().equals(getNodeClass())) {
			return false;
		}
		boolean equalArgs = false;
		if (node.isCommutative()) {
			equalArgs = (e.getV1() == v2 && e.getV2() == v1);
		}
		return (equalArgs || (e.getV1() == v1 && e.getV2() == v2));
	}

	@Override
	public int hashCode() {
		return (getNodeClass().hashCode() * 7919 + v1.hashCode()) * 17389
				+ v2.hashCode();
	}
	
	@Override
	public String toString() {
		return String.format("[OPT] (%s, %s, %s)", getNodeClass(), v1, v2); 
	}

	public Value getV1() {
		return v1;
	}

	public Value getV2() {
		return v2;
	}

	public String getNodeClass() {
		return node.getNodeClass();
	}

}
