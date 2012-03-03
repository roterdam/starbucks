package edu.mit.compilers.grammar;

import edu.mit.compilers.crawler.Scope;
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
	
	public void validate(Scope scope) {
		checkRule(scope);
		validateChildren(scope);
	}
	
	public void validateChildren(Scope scope) {
		DecafNode child = this.getFirstChild(); 	
		while (child != null) {
			child.validate(scope);
			child = child.getNextSibling();
		}
	}
	
	// TODO: make this abstract eventually.
	public void checkRule(Scope scope) {
		assert false;
	}
	
}
