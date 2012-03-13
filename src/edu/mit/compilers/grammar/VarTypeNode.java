package edu.mit.compilers.grammar;

import edu.mit.compilers.crawler.VarType;
import edu.mit.compilers.grammar.tokens.INT_LITERALNode;

@SuppressWarnings("serial")
public abstract class VarTypeNode extends DecafNode {
	
	public Boolean isArray(){
		return getNumberOfChildren() == 1;
	}
	
	/**
	 * returns the int literal. If it's not an array, then it returns null
	 */
	public INT_LITERALNode getIntLiteralNode(){
		if (isArray()){
			assert getFirstChild() instanceof INT_LITERALNode;
			return (INT_LITERALNode) getFirstChild();
		}
		return null;
	}
	
	abstract public VarType getVarType();

}
