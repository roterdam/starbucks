package edu.mit.compilers.grammar.tokens;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.SemanticRules;
import edu.mit.compilers.grammar.DecafNode;

@SuppressWarnings("serial")
public class CLASSNode extends DecafNode {

	@Override
	public void applyRules(Scope scope) {
		SemanticRules.apply(this, scope);
	}

	@Override
	public void validateChildren(Scope scope) {
		assert getNumberOfChildren() >= 1; // must have a class name
		assert getNumberOfChildren() <= 3; // no more than id, field_decls, and
											// methods
		assert getChild(0) instanceof IDNode;

		for (FIELD_DECLNode fieldDeclNode : getFieldNodes()) {
			fieldDeclNode.validate(scope);
		}
		for (METHOD_DECLNode methodDeclNode : getMethodNodes()) {
			methodDeclNode.validate(scope);
		}
	}

	public IDNode getIdNode() {
		assert getChild(0) instanceof IDNode;
		return (IDNode) getChild(0);
	}

	public List<FIELD_DECLNode> getFieldNodes() {
		assert getChild(1) instanceof FIELDSNode;
		List<FIELD_DECLNode> output = new ArrayList<FIELD_DECLNode>();
		DecafNode child = getChild(1).getFirstChild();
		if (child != null) {
			output.add((FIELD_DECLNode) child);
			while (child.getNextSibling() != null) {
				child = child.getNextSibling();
				output.add((FIELD_DECLNode) child);
			}
		}
		return output;
	}

	public List<METHOD_DECLNode> getMethodNodes() {
		List<METHOD_DECLNode> output = new ArrayList<METHOD_DECLNode>();
		assert getChild(2) instanceof METHODSNode;
		METHODSNode methods = (METHODSNode) getChild(2);
		for (int i = 0; i < methods.getNumberOfChildren(); i++) {
			assert methods.getChild(i) instanceof METHOD_DECLNode;
			output.add((METHOD_DECLNode) methods.getChild(i));
		}
		return output;
	}
}