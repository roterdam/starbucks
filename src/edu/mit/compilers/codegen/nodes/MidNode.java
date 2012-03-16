package edu.mit.compilers.codegen.nodes;

// TODO: make abstract
public class MidNode {
	MidNode nextNode;

	public void setNextNode(MidNode node) {
		nextNode = node;
	}

	public MidNode getNextNode() {
		return nextNode;
	}

	public String toString() {
		return "<" + getNodeClass() + ">";
	}

	public String getNodeClass() {
		String className = getClass().getName();
		int mid = className.lastIndexOf('.') + 1;
		return className.substring(mid);
	}

	/**
	 * Declares node. Override in child class and add on to return string to
	 * make other connections.
	 */
	public String toDotSyntax() {
		return hashCode() + " [label=\"" + toString() + "\"];\n";
	}
}
