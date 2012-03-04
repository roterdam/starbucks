package edu.mit.compilers.grammar;

import edu.mit.compilers.crawler.VarType;

@SuppressWarnings("serial")
public class DeclNode extends DecafNode {

	public IDNode getIDNode() {
		assert this.getNumberOfChildren() == 2;
		assert this.getFirstChild().getNextSibling() instanceof IDNode;

		return (IDNode) this.getFirstChild().getNextSibling();
	}

	public VarTypeNode getVarTypeNode() {
		assert this.getNumberOfChildren() == 2;
		assert this.getFirstChild() instanceof VarTypeNode;
		return (VarTypeNode) this.getFirstChild();
	}

	public VarType getVarType() {
		assert this.getNumberOfChildren() == 2;
		assert this.getFirstChild() instanceof VarTypeNode;

		return ((VarTypeNode) this.getFirstChild()).getVarType();
	}

}
