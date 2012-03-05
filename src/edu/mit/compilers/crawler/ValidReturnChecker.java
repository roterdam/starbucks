package edu.mit.compilers.crawler;

import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.grammar.tokens.BLOCKNode;
import edu.mit.compilers.grammar.tokens.ELSENode;
import edu.mit.compilers.grammar.tokens.IFNode;
import edu.mit.compilers.grammar.tokens.METHOD_DECLNode;
import edu.mit.compilers.grammar.tokens.RETURNNode;

public class ValidReturnChecker {

	VarType returnType;

	public ValidReturnChecker(VarType returnType) {
		this.returnType = returnType;
	}
	
	public boolean visit(DecafNode node) {
		return false;
	}

	public boolean visit(METHOD_DECLNode node) {
		assert node.getChild(1) instanceof BLOCKNode;

		BLOCKNode block = (BLOCKNode) node.getChild(1);

		return block.hasValidReturn(this);
	}

	public boolean visit(BLOCKNode node) {
		// At least one statement in block of METHOD_DECL should return the
		// right type.
		DecafNode blockChild = node.getFirstChild();
		while (blockChild != null) {
			if (blockChild.hasValidReturn(this)) {
				return true;
			}
			blockChild = blockChild.getNextSibling();
		}
		return false;
	}

	public boolean visit(IFNode node) {
		// Check IF branch.
		assert node.getChild(1) instanceof BLOCKNode;
		BLOCKNode ifBlock = (BLOCKNode) node.getChild(1);

		if (!ifBlock.hasValidReturn(this)) {
			return false;
		}
		// Check ELSE branch if possible.
		if (node.getNumberOfChildren() == 3) {
			assert node.getChild(2) instanceof ELSENode;
			ELSENode elseNode = (ELSENode) node.getChild(2);
			return elseNode.hasValidReturn(this);
		}
		return false;
	}

	public boolean visit(ELSENode node) {
		assert node.getChild(0) instanceof BLOCKNode;
		BLOCKNode elseBlock = (BLOCKNode) node.getChild(0);
		return elseBlock.hasValidReturn(this);
	}
	
	public boolean visit(RETURNNode node) {
		return true;
	}
	
}
