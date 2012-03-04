package edu.mit.compilers.grammar;

import edu.mit.compilers.crawler.VarType;

@SuppressWarnings("serial")
public class VOIDNode extends VarTypeNode {

	@Override
	public VarType getVarType() {
		return VarType.VOID;
	}

}