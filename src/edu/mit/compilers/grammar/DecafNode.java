package edu.mit.compilers.grammar;

import antlr.CommonAST;
import antlr.Token;
import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.SemanticRules;

@SuppressWarnings("serial")
abstract public class DecafNode extends CommonAST {
	private int line = 0;
	private int column = 0;

	@Override
	public void initialize(Token t) {
		super.initialize(t);
		line = t.getLine();
		column = t.getColumn();
	}

	@Override
	public int getLine() {
		return line;
	}

	@Override
	public int getColumn() {
		return column;
	}

	public void copyFromNode(DecafNode n) {
		this.line = n.getLine();
		this.column = n.getColumn();
	}

	@Override
	public DecafNode getFirstChild() {
		return (DecafNode) super.getFirstChild();
	}

	@Override
	public DecafNode getNextSibling() {
		return (DecafNode) super.getNextSibling();
	}

	public DecafNode getChild(int i) {
		DecafNode currentNode = getFirstChild();
		for (int j = 0; j < i; j++) {
			currentNode = currentNode.getNextSibling();
		}
		return currentNode;
	}
	
	public DecafNode[] getAllChildren() {
		int numChildren = this.getNumberOfChildren();
		DecafNode[] children = new DecafNode[numChildren];
		DecafNode child = this.getFirstChild();
		for (int i = 0; i < numChildren; i++) {
			children[i] = child;
			child.getNextSibling();
		}
		return children;
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

	/**
	 * Manipulates tree as necessary.
	 * 
	 * @return Node to replace this with, "this" if no change.
	 */
	public DecafNode clean() {
		// By default does nothing and propagates call to children.
		DecafNode child = this.getFirstChild();
		if (child != null) {
			this.setFirstChild(child.clean());
			DecafNode prevChild = child;
			DecafNode nextChild = child.getNextSibling();
			while (nextChild != null) {
				prevChild.setNextSibling(nextChild.clean());
				prevChild = nextChild;
				nextChild = nextChild.getNextSibling();
			}
		}
		return this;
	}
}
