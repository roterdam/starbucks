package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.VarType;
import edu.mit.compilers.grammar.ExpressionNode;

@SuppressWarnings("serial")
public class IDNode extends ExpressionNode {

	/**
	 * Returns INT or BOOLEAN (If INT_ARRAY or BOOLEAN_ARRAY, returns INT and
	 * BOOLEAN respectively.) 
	 * 
	 * Returns UNDECLARED if variable has not been declared.
	 */
	@Override
	public VarType getReturnType(Scope scope) {
		
		VarType returnType = scope.getType(getText());
		assert getNumberOfChildren() <= 1;
		// If accessing an array, return the type of the array. However, if
		// there's no children this ends up returning INT_ARRAY or BOOLEAN_ARRAY
		// so whoever is calling it will get an error (as desired).
		// (i.e. 5 + array will error)
		if (getNumberOfChildren() == 1) {
			switch (returnType) {
			case INT_ARRAY:
				return VarType.INT;
			case BOOLEAN_ARRAY:
				return VarType.BOOLEAN;
			}
		}
		return returnType;
	}

}