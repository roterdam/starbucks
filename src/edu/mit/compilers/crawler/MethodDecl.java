package edu.mit.compilers.crawler;

import java.util.List;

public class MethodDecl extends Decl {

	private List<VarType> params;

	/**
	 * Stores the representation of a signature in memory, void a(int b, boolean c).
	 * @param returnType Type like "void".
	 * @param id The identifier for the method, "a".
	 * @param params A list of parameters by type, "int, boolean".
	 * @param line Line number identifier occurs on.
	 * @param col Column number identifier occurs on.
	 */
	public MethodDecl(VarType returnType, String id, List<VarType> params, int line, int col) {
		super(returnType, id, line, col);
		this.params = params;
	}

	public List<VarType> getParams() {
		return params;
	}

}
