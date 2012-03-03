package edu.mit.compilers.grammar;

import antlr.CommonAST;
import antlr.Token;

@SuppressWarnings("serial")
public class DecafNode extends CommonAST {
	private int line = 0;
	private int col = 0;

	@Override
	public void initialize(Token t) {
		super.initialize(t);
		line = t.getLine();
		col = t.getColumn();
	}

	@Override
	public int getLine() {
		return line;
	}

	@Override
	public int getColumn() {
		return col;
	}

	@Override
	public DecafNode getFirstChild() {
		return (DecafNode) super.getFirstChild();
	}
	
	@Override
	public DecafNode getNextSibling() {
		return (DecafNode) super.getNextSibling();
	}
}
