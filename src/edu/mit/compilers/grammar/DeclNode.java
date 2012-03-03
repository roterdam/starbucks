package edu.mit.compilers.grammar;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.SemanticRules;
import edu.mit.compilers.crawler.VarType;

@SuppressWarnings("serial")
public class DeclNode extends DecafNode {
	
	@Override
	public void checkRule(Scope scope) {
		SemanticRules.apply(this, scope);
	}
	
	public IDNode getIDNode() {
		assert this.getNumberOfChildren() == 2;
		assert this.getFirstChild().getNextSibling() instanceof IDNode;
		
		return (IDNode) this.getFirstChild().getNextSibling();
	}
	
	public VarType getVarType() {
		assert this.getNumberOfChildren() == 2;
		assert this.getFirstChild() instanceof VarTypeNode;
		
		return ((VarTypeNode) this.getFirstChild()).getVarType();
	}

}
