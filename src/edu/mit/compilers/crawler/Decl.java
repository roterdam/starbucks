package edu.mit.compilers.crawler;


abstract public class Decl {
	
	private VarType returnType;
	private String id;
	private int line;
	private int column;

	public Decl(VarType returnType, String id, int line, int column) {
		this.returnType = returnType;
		this.id = id;
		this.line = line;
		this.column = column;
	}

	public VarType getReturnType() {
		return returnType;
	}

	public String getId() {
		return id;
	}
	
	public int getLine() {
		return line;
	}
	
	public int getColumn() {
		return column;
	}

}
