package edu.mit.compilers.codegen.nodes.regops;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.nodes.memory.MidConstantNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;

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
	
	@Override
	public List<MidMemoryNode> getUsedMemoryNodes() {
		return new ArrayList<MidMemoryNode>();
	}

}
