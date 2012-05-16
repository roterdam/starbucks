package edu.mit.compilers.grammar;

import java.lang.reflect.InvocationTargetException;

import antlr.CommonAST;
import antlr.Token;
import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.MidVisitor;
import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.SemanticRules;
import edu.mit.compilers.crawler.ValidReturnChecker;
import edu.mit.compilers.opt.algebra.AlgebraicSimplifier;
import edu.mit.compilers.opt.forunroll.Unroller;

@SuppressWarnings("serial")
public abstract class DecafNode extends CommonAST {
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

	public DecafNode getTail() {
		DecafNode currentNode = this;
		while (currentNode.getNextSibling() != null) {
			currentNode = currentNode.getNextSibling();
		}
		return currentNode;
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

	public void replaceChildWithList(int i, DecafNode node) {
		DecafNode tailNode = node;
		while (tailNode.getNextSibling() != null) {
			tailNode = tailNode.getNextSibling();
		}

		DecafNode predecessorNode = null;
		DecafNode thisNode = getFirstChild();
		for (int j = 0; j < i; j++) {
			predecessorNode = thisNode;
			thisNode = thisNode.getNextSibling();
		}
		DecafNode successorNode = thisNode.getNextSibling();

		if (thisNode == node) {
			// Replacing node with itself, no updates necessary.
			return;
		}
		if (predecessorNode == null) { // Setting firstChild
			setFirstChild(node);
		} else {
			predecessorNode.setNextSibling(node);
		}
		tailNode.setNextSibling(successorNode);
	}

	public void replaceChild(int i, DecafNode node) {
		DecafNode predecessorNode = null;
		DecafNode thisNode = getFirstChild();
		for (int j = 0; j < i; j++) {
			predecessorNode = thisNode;
			thisNode = thisNode.getNextSibling();
		}
		DecafNode successorNode = thisNode.getNextSibling();

		if (thisNode == node) {
			// Replacing node with itself, no updates necessary.
			return;
		}
		if (predecessorNode == null) { // Setting firstChild
			setFirstChild(node);
		} else {
			predecessorNode.setNextSibling(node);
		}
		node.setNextSibling(successorNode);
	}

	public void validate(Scope scope) {
		applyRules(scope);
		validateChildren(scope);
	}

	public void applyRules(Scope scope) {
		SemanticRules.apply(this, scope);
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
			DecafNode nextChild = child.getNextSibling();
			this.setFirstChild(child.clean());
			DecafNode prevChild = this.getFirstChild();
			prevChild.setNextSibling(nextChild);
			while (nextChild != null) {
				DecafNode newNextChild = nextChild.getNextSibling();
				nextChild = nextChild.clean();
				nextChild.setNextSibling(newNextChild);
				prevChild.setNextSibling(nextChild);
				prevChild = nextChild;
				nextChild = newNextChild;
			}
		}
		return this;
	}

	public boolean hasValidReturn(ValidReturnChecker returnChecker) {
		return returnChecker.visit(this);
	}

	public MidNodeList convertToMidLevel(MidSymbolTable symbolTable) {
		// TODO: make this method abstract.
		// assert false;
		return MidVisitor.visit(this, symbolTable);
	}

	public void simplifyExpressions() {

	}

	public DecafNode eliminateUnreachableCode() {
		return this;
	}

	/* If true, all statements after this statement are unreachable */
	public boolean isBlockEnder() {
		return false;
	}

	public DecafNode getLastSibling() {
		DecafNode lastSibling = this;
		while (lastSibling.getNextSibling() != null) {
			lastSibling = lastSibling.getNextSibling();
		}
		return lastSibling;
	}

	/**
	 * Returns true if this node sets the value of variable 'var'.
	 * 
	 * Overridden by ASSIGNNode!
	 * 
	 * @param var
	 * @return
	 */
	public boolean doesAssign(String var) {
		DecafNode childNode = this.getFirstChild();
		while (childNode != null) {
			if (childNode.doesAssign(var)) {
				return true;
			}
			childNode = childNode.getNextSibling();
		}
		return false;
	}

	protected static DecafNode deepCopyHelper(DecafNode n) {
		assert n instanceof DecafNode;
		LogCenter
				.debug("FU", "Deep copying " + n + " ( " + n.hashCode() + " )");
		try {
			LogCenter.debug("FU", "Node class is " + n.getClass());
			DecafNode copyNode = n.getClass().getConstructor().newInstance();
			copyNode.setText(n.getText());

			DecafNode childNode = n.getFirstChild();
			DecafNode prevNode = null;
			for (int i = 0; i < n.getNumberOfChildren(); i++) {
				if (i == 0) {
					copyNode.setFirstChild(childNode.deepCopy());
					prevNode = copyNode.getFirstChild();
				} else {
					prevNode.setNextSibling(childNode.deepCopy());
					prevNode = prevNode.getNextSibling();
				}
				childNode = childNode.getNextSibling();
			}
			return copyNode;

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			LogCenter.debug("FU", "OOPS! " + n);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// Overwritten by ExpressionNode
	public DecafNode deepCopy() {
		return DecafNode.deepCopyHelper(this);
		// FIXME: expressions have pre/post lists.

	}

	public DecafNode unroll() {
		return Unroller.unroll(this);
	}

	public boolean isUnrollable(String var, boolean hasLoopScope) {
		return Unroller.isUnrollable(this, var, hasLoopScope);
	}

	@Override
	public String toString() {
		//assert !this.getClass().getSimpleName().equals("");
		String out = "<" + this.getClass().getName() + " " + getText() + ">[";
		DecafNode childNode = getFirstChild();
		while (childNode != null) {
			out += childNode.toString() + ",";
			childNode = childNode.getNextSibling();
		}
		out += "]";
		return out;
	}
}
