package edu.mit.compilers.codegen.nodes.memory;

import edu.mit.compilers.codegen.nodes.MidNode;

abstract public class MidMemoryNode extends MidNode {

	String rawLocationReference;
	
	// Only used for toString
	private String name;

	private long constantValue;
	private boolean isConstant;

	public MidMemoryNode(String name) {
		super();
		this.name = name;
		isConstant = false;
	}

	public String getName() {
		return name;
	}

	/**
	 * Sets the location reference for a memory node. Note that this is
	 * different depending on the type of node.
	 * Fields: `labelname`
	 * Registers: `r10`
	 * Stack: `8` (formatted as [RBP - 8])
	 * 
	 * @param rawLocationReference
	 */
	public void setRawLocationReference(String rawLocationReference) {
		this.rawLocationReference = rawLocationReference;
	}

	public String getRawLocationReference() {
		return rawLocationReference;
	}

	/**
	 * Returns location reference, formatted if necessary.
	 */
	abstract public String getFormattedLocationReference();

	public String toString() {
		String className = getClass().getName();
		int mid = className.lastIndexOf('.') + 1;
		return "<" + className.substring(mid) + ": " + getName() + " >";
	}

	public void setConstantValue(long decafIntValue) {
		this.constantValue = decafIntValue;
		this.isConstant = true;
	}
	
	@Override
	public int hashCode() {
		if (isConstant) {
			return (int) constantValue;
		}
		return super.hashCode();
	}
	
	public boolean isConstant() {
		return isConstant;
	}
	
	public long getConstant() {
		return constantValue;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MidMemoryNode)) {
			return false;
		}
		MidMemoryNode oMem = (MidMemoryNode) o;
		if (isConstant && oMem.isConstant()) {
			return constantValue == oMem.getConstant();
		}
		return super.equals(o);
	}
	
}