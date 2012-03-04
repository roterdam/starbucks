package edu.mit.compilers.grammar;

import edu.mit.compilers.crawler.VarType;


@SuppressWarnings("serial")
public class METHOD_RETURNNode extends DecafNode {
	
	public VarType getReturnType() {
		assert getFirstChild() instanceof VarTypeNode : getFirstChild().getClass();
		return ((VarTypeNode) this.getFirstChild()).getVarType();
	}

}