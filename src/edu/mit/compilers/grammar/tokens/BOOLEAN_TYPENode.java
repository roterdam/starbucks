package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.crawler.VarType;
import edu.mit.compilers.grammar.VarTypeNode;

@SuppressWarnings("serial")
public class BOOLEAN_TYPENode extends VarTypeNode {

	@Override
	public VarType getVarType() {
		return VarType.BOOLEAN;
	}

}