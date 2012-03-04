package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.crawler.VarType;
import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.grammar.VarTypeNode;


@SuppressWarnings("serial")
public class METHOD_RETURNNode extends DecafNode {
	
	public VarType getReturnType() {
		assert getFirstChild() instanceof VarTypeNode : getFirstChild().getClass();
		return ((VarTypeNode) this.getFirstChild()).getVarType();
	}

}