package edu.mit.compilers.grammar.expressions;

import java.util.List;


import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.grammar.ExpressionNode;

@SuppressWarnings("serial")
public abstract class CallNode extends ExpressionNode {
	public abstract List<DecafNode> getParameters();
	public abstract String getName();
}
