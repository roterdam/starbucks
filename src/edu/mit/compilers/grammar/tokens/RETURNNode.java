package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.ValidReturnChecker;
import edu.mit.compilers.crawler.VarType;
import edu.mit.compilers.grammar.DecafNode;
import edu.mit.compilers.grammar.ExpressionNode;


/**
 * Careful, this is the "return x;" statement NOT the method return type.
 * @author joshma
 *
 */
@SuppressWarnings("serial")
public class RETURNNode extends DecafNode {

	public VarType getReturnType(Scope scope) {
		if (getFirstChild() != null){
			assert getFirstChild() instanceof ExpressionNode;
			return ((ExpressionNode) getFirstChild()).getReturnType(scope);
		} else {
			return VarType.VOID;
		}
	}
	
	@Override
	public boolean hasValidReturn(ValidReturnChecker returnChecker) {
		return returnChecker.visit(this);
	}
	
}