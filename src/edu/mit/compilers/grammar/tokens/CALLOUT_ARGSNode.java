package edu.mit.compilers.grammar.tokens;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.grammar.ExpressionNode;


@SuppressWarnings("serial")
public class CALLOUT_ARGSNode extends DecafNode {

	public List<DecafNode> getArgs() {
		List<DecafNode> out = new ArrayList<DecafNode>();
		DecafNode child = getFirstChild();
		while (child != null) {
			assert child instanceof STRING_LITERALNode || child instanceof ExpressionNode;
			out.add(child);
			child = child.getNextSibling();
		}
		return out;
	}

}