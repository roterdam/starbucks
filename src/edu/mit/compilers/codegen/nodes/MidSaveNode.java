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
	
	public MidSaveNode(MidRegisterNode refNode) {
		this.registerNode = refNode;
		this.saveType = MidSaveNodeType.REGISTER;
	}
	
	public MidSaveNode(long decafIntValue) {
		this.decafIntValue = decafIntValue;
		this.saveType = MidSaveNodeType.INT;
	}
	
	public MidSaveNode(boolean decafBooleanValue) {
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
	
	public String toString() {
		String className = getClass().getName();
		int mid = className.lastIndexOf('.') + 1;
		String value = null;
		switch (saveType) {
		case INT:
			value = Long.toString(decafIntValue);
			break;
		case BOOLEAN:
			value = Boolean.toString(decafBooleanValue);
			break;
		case REGISTER:
			value = "reg";
		}
		return "<" + className.substring(mid) + ": " + value + ">";
	}

}
