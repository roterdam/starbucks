package edu.mit.compilers.codegen.nodes.memory;

import edu.mit.compilers.codegen.nodes.MidNode;

abstract public class MidMemoryNode extends MidNode {

	String rawLocationReference;

	private String name;

	public MidMemoryNode(String name) {
		super();
		this.name = name;
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

	/**
	 * Returns location reference, formatted if necessary.
	 */
	abstract public String getFormattedLocationReference();

	public String toString() {
		String className = getClass().getName();
		int mid = className.lastIndexOf('.') + 1;
		return "<" + className.substring(mid) + ": " + getName() + " >";
	}

}