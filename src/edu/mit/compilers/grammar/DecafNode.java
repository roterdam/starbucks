package edu.mit.compilers.grammar;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.SemanticRules;
import antlr.CommonAST;
import antlr.Token;

@SuppressWarnings("serial")
abstract public class DecafNode extends CommonAST {
	private int line = 0;
	private int col = 0;

	@Override
	public void initialize(Token t) {
		super.initialize(t);
		line = t.getLine();
		col = t.getColumn();
		System.out.println("INITIALIZING " + t.getText() + " AT " + t.getLine());
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
	
	public DecafNode getChild(int i){
		DecafNode currentNode = getFirstChild();
		for(int j=0; j<i; j++){
			currentNode = currentNode.getNextSibling();
		}
		return currentNode;
	}
	
	public void validate(Scope scope) {
		SemanticRules.apply(this, scope);
		validateChildren(scope);
	}
	
	public void validateChildren(Scope scope) {
		DecafNode child = this.getFirstChild(); 	
		while (child != null) {
			child.validate(scope);
			child = child.getNextSibling();
		}
	}
}
