package edu.mit.compilers.grammar;

import edu.mit.compilers.crawler.VarType;

@SuppressWarnings("serial")
public abstract class VarTypeNode extends DecafNode {
	
	abstract public VarType getVarType();

}
