package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.VarType;
import edu.mit.compilers.grammar.ExpressionNode;

@SuppressWarnings("serial")
public class CHAR_LITERALNode extends ExpressionNode {

	@Override
	public VarType getReturnType(Scope scope) {
		// Cast to INT!
		return VarType.INT;
	}

}