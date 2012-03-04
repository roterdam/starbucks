package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.VarType;
import edu.mit.compilers.grammar.ExpressionNode;

/**
 * Careful, this is "3" not "int".
 * 
 * @author joshma
 * 
 */
@SuppressWarnings("serial")
public class INT_LITERALNode extends ExpressionNode {
	public int getValue(){
		return Integer.parseInt(this.getText());
	}

	@Override
	public VarType getReturnType(Scope scope) {
		return VarType.INT;
	}
}