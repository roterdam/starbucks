package edu.mit.compilers.codegen.nodes.memory;

/**
 * Same as MidFielDeclNode, except since strings are referred to by pointer the
 * memory location is not evaluated, i.e. not wrapped in brackets.
 */
public class MidStringDeclNode extends MidFieldDeclNode {

	public MidStringDeclNode(String name) {
		super(name);
	}

	/**
	 * Get the location reference. Returns reference to pointer, not data.
	 */
	@Override
	public String getFormattedLocationReference() {
		assert rawLocationReference != null : "rawLocationReference is null!";
		return rawLocationReference;
	}

}
