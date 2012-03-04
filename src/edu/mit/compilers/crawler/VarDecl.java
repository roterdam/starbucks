package edu.mit.compilers.crawler;


public class VarDecl extends Decl{
	
	/**
	 * Stores the representation of a variable.
	 * @param returnType Type like "int" and "boolean".
	 * @param id The identifier for the variable, "a".
	 * @param line Line number identifier occurs on.
	 * @param column Column number identifier occurs on.
	 */
	public VarDecl(VarType returnType, String id, int line, int col) {
		super(returnType, id, line, col);
	}

}
