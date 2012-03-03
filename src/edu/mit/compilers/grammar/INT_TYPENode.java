package edu.mit.compilers.grammar;

import edu.mit.compilers.crawler.VarType;

/**
 * Careful, this is "int" not "3".
 * 
 * @author joshma
 * 
 */
@SuppressWarnings("serial")
public class INT_TYPENode extends VarTypeNode {
	
	@Override
	public VarType getVarType() {
		return VarType.INT;		
	}

}