package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.VarType;
import edu.mit.compilers.grammar.ExpressionNode;

/**
 * Careful, this is "3" not "int".
 */
@SuppressWarnings("serial")
public class INT_LITERALNode extends ExpressionNode {

	/**
	 * SURPRISE! INT_LITERALs store Java 64-bit longs.
	 */
	public boolean isWithinBounds() {
		try {
			System.out.println("trying to be within bounds.");
			Long.parseLong(getText());
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Simply checks for negative sign to determine positive or not. Does not
	 * check for integers too large.
	 */
	public boolean isPositive() {
		return !(getText().equals("0") || getText().startsWith("-"));
	}

	@Override
	public VarType getReturnType(Scope scope) {
		// TODO Auto-generated method stub
		return VarType.INT;
	}

}