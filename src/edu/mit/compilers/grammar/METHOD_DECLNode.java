package edu.mit.compilers.grammar;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.crawler.VarType;

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

}
