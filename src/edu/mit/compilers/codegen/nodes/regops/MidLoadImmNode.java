package edu.mit.compilers.codegen.nodes.regops;

import edu.mit.compilers.codegen.nodes.memory.MidConstantNode;

public class MidLoadImmNode extends MidLoadNode {
	private long value;

	public MidLoadImmNode(long decafIntValue) {
		super(new MidConstantNode("CONS " + decafIntValue, decafIntValue));
		this.value = decafIntValue;
	}

	public String toString() {
		String className = getClass().getName();
		int mid = className.lastIndexOf('.') + 1;
		return "<" + className.substring(mid) + ": " + getName() + ","
				+ Long.toString(value) + ">";
	}

	@Override
	public String toDotSyntax() {
		return super.toDotSyntax() + Long.toString(value) + " -> " + hashCode()
				+ " [style=dotted,color=orange];\n";
	}

}
