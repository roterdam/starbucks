package edu.mit.compilers.grammar.tokens;

import antlr.Token;
import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.VarType;
import edu.mit.compilers.grammar.ExpressionNode;

/**
 * Careful, this is "3" not "int".
 */
@SuppressWarnings("serial")
public class INT_LITERALNode extends ExpressionNode {

	private long value;
	private boolean isWithinBounds;

	
	public boolean isWithinBounds() {
		return isWithinBounds;
	}
	
	/**
	 * SURPRISE! INT_LITERALs are Java 64-bit longs.
	 */
	public long getValue() {
		return value;
	}

	@Override
	public void initialize(Token t) {
		super.initialize(t);
		// Strip the negative sign to figure out the base.
		boolean isNegative = getText().startsWith("-");
		String text = getText();
		if (isNegative) {
			text = text.substring(1);
		}
		int base = 10;
		if (text.startsWith("0x")) {
			base = 16;
			text = text.substring(2);
		} else if (text.startsWith("0b")) {
			base = 2;
			text = text.substring(2);
		}
		if (isNegative) {
			text = "-" + text;
		}
		try {
			value = Long.parseLong(text, base);
			isWithinBounds = true;
		} catch (NumberFormatException e) {
			isWithinBounds = false;
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