package edu.mit.compilers.grammar.tokens;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.SemanticRules;
import edu.mit.compilers.crawler.ValidReturnChecker;
import edu.mit.compilers.crawler.VarType;
import edu.mit.compilers.crawler.Scope.BlockType;
import edu.mit.compilers.grammar.DecafNode;

@SuppressWarnings("serial")
public class METHOD_DECLNode extends DecafNode {

	public String getId() {
		return getText();
	}

	public VarType getReturnType() {
		assert getFirstChild() instanceof METHOD_RETURNNode;
		return ((METHOD_RETURNNode) getFirstChild()).getReturnType();
	}

	public List<VarType> getParams() {
		List<VarType> o = new ArrayList<VarType>();
		assert getChild(1) instanceof BLOCKNode;
		BLOCKNode block = (BLOCKNode) getChild(1);
		// Assumes that the PARAM_DECL nodes are the first nodes (if applicable) in the BLOCK.
		for (int i = 0; i < block.getNumberOfChildren(); i++) {
			DecafNode n = block.getChild(i);
			if (!(n instanceof PARAM_DECLNode)) {
				break;
			}
			o.add(((PARAM_DECLNode) n).getVarType());
		}
		return o;
	}
	
	@Override
	public void validateChildren(Scope scope) {
		assert getNumberOfChildren() == 2; //Must have return and block
		assert getChild(0) instanceof METHOD_RETURNNode;
			
		assert getChild(1) instanceof BLOCKNode;
		getChild(0).validate(scope);
		((BLOCKNode) getChild(1)).validate(scope, BlockType.METHOD, this.getReturnType());		
		
	}
	
	@Override
	public boolean hasValidReturn(ValidReturnChecker returnChecker) {
		return returnChecker.visit(this);
	}
	
	@Override
	public void applyRules(Scope scope) {
		SemanticRules.apply(this, scope);
	}

}
