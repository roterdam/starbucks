package edu.mit.compilers.codegen.nodes.memory;



public class MidFieldArrayDeclNode extends MidFieldDeclNode {
	private long length;

	public MidFieldArrayDeclNode(String name, long length) {
		super(name);
		this.length = length;
	}

	public long getLength() {
		return length;
	}
	
	/**
	 * Get the location reference. Returns reference to data, not pointer.
	 */
	
	@Override
	public String getFormattedLocationReference(){
		assert false : "Arrays are accessed by element: " + this;
		return null;
	}
	
	@Override
	public long getSize(){
		return length;
	}

	
}