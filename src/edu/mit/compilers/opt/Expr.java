package edu.mit.compilers.opt;

public class Expr {

	private Value v1;
	private Value v2;
	private String nodeClass;

	public Expr(Value v1, Value v2, String nodeClass) {
		this.v1 = v1;
		this.v2 = v2;
		this.nodeClass = nodeClass;
	}
	
	/**
	 *	This is basically canonicalization. 
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Expr)) {
			return false;
		}
		Expr e = (Expr) o;
		return (e.getV1() == v1 && e.getV2() == v2 && e.getNodeClass().equals(nodeClass));
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
