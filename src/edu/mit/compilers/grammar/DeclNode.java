package edu.mit.compilers.grammar;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.SemanticRules;
import edu.mit.compilers.crawler.VarType;
import edu.mit.compilers.grammar.tokens.IDNode;
import edu.mit.compilers.grammar.tokens.INT_LITERALNode;

@SuppressWarnings("serial")
public class DeclNode extends DecafNode {

	public IDNode getIDNode() {
		assert getNumberOfChildren() == 2 : toStringTree();
		assert getFirstChild().getNextSibling() instanceof IDNode;

		return (IDNode) this.getFirstChild().getNextSibling();
	}

	public VarTypeNode getVarTypeNode() {
		assert getNumberOfChildren() == 2;
		assert getFirstChild() instanceof VarTypeNode;
		return (VarTypeNode) this.getFirstChild();
	}

	public VarType getVarType() {
		assert getNumberOfChildren() == 2;
		assert getFirstChild() instanceof VarTypeNode;
		
		VarTypeNode childVarTypeNode = ((VarTypeNode) getFirstChild());
		VarType childVarType = childVarTypeNode.getVarType();
		assert childVarType == VarType.BOOLEAN || childVarType == VarType.INT;
		
		// Indicate if it's an array.
		assert childVarTypeNode.getNumberOfChildren() <= 1;
		if (childVarTypeNode.getNumberOfChildren() == 1) {
			assert childVarTypeNode.getFirstChild() instanceof INT_LITERALNode;
			switch (childVarType) {
			case INT:
				return VarType.INT_ARRAY;
			case BOOLEAN:
				return VarType.BOOLEAN_ARRAY;
			}
		}
		
		return childVarType;
	}
	public long getArrayLength(){
		switch(getVarType()){
		case INT_ARRAY : case BOOLEAN_ARRAY:
			VarTypeNode childVarTypeNode = ((VarTypeNode) getFirstChild());
			return ((INT_LITERALNode)childVarTypeNode.getFirstChild()).getValue();
		default:
			return -1;
		}
	}
	
	@Override
	public void applyRules(Scope scope) {
		SemanticRules.apply(this, scope);
	}

}
