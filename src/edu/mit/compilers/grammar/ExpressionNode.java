package edu.mit.compilers.grammar;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.VarType;

@SuppressWarnings("serial")
public abstract class ExpressionNode extends DecafNode {
	public abstract VarType getReturnType(Scope scope);
}