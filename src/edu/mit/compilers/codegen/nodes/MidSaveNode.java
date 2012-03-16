package edu.mit.compilers.codegen.nodes;

import edu.mit.compilers.codegen.nodes.regops.MidRegisterNode;

/**
 * Saves referenced register node or literal to memory (the stack?).
 */
public class MidSaveNode extends MidNode {

	private MidRegisterNode registerNode;
	private long decafIntValue;
	private boolean decafBooleanValue;

	private static enum MidSaveNodeType {
		REGISTER, INT, BOOLEAN;
	}

	private MidSaveNodeType saveType;
	private MidMemoryNode destination;

	private MidSaveNode(MidMemoryNode dest) {
		assert dest != null;
		this.destination = dest;
	}

	public MidSaveNode(MidRegisterNode refNode, MidMemoryNode dest) {
		this(dest);
		this.registerNode = refNode;
		this.saveType = MidSaveNodeType.REGISTER;
	}

	public MidSaveNode(long decafIntValue, MidMemoryNode dest) {
		this(dest);
		this.decafIntValue = decafIntValue;
		this.saveType = MidSaveNodeType.INT;
	}

	public MidSaveNode(boolean decafBooleanValue, MidMemoryNode dest) {
		this(dest);
		this.decafBooleanValue = decafBooleanValue;
		this.saveType = MidSaveNodeType.BOOLEAN;
	}

	public MidRegisterNode getRefNode() {
		assert saveType == MidSaveNodeType.REGISTER;
		return registerNode;
	}

	public long getDecafIntValue() {
		assert saveType == MidSaveNodeType.INT;
		return decafIntValue;
	}

	public boolean getDecafBooleanValue() {
		assert saveType == MidSaveNodeType.BOOLEAN;
		return decafBooleanValue;
	}

	public MidMemoryNode getDestinationNode() {
		return this.destination;
	}

	public String toString() {
		String className = getClass().getName();
		int mid = className.lastIndexOf('.') + 1;
		String value = "wtf value is this";
		switch (saveType) {
		case INT:
			value = Long.toString(decafIntValue);
			break;
		case BOOLEAN:
			value = Boolean.toString(decafBooleanValue);
			break;
		case REGISTER:
			value = registerNode.getName();
		}
		return "<" + className.substring(mid) + ": " + value + ">";
	}

	@Override
	public String toDotSyntax() {
		String out = super.toDotSyntax() + hashCode() + " -> "
				+ destination.hashCode() + " [style=dotted,color=green];\n";
		if (saveType == MidSaveNodeType.REGISTER) {
			out += registerNode.hashCode() + " -> " + hashCode() + " [style=dotted,color=green];\n";
		}
		return out;
	}
}
