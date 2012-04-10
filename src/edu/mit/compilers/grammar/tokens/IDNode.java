package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.MidShortCircuitVisitor;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.MidVisitor;
import edu.mit.compilers.codegen.nodes.MidLabelNode;
import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.SemanticRules;
import edu.mit.compilers.crawler.VarType;
import edu.mit.compilers.grammar.ExpressionNode;
import edu.mit.compilers.opt.AlgebraicSimplifier;

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
		if (isArray()) {
			switch (returnType) {
			case INT_ARRAY:
				return VarType.INT;
			case BOOLEAN_ARRAY:
				return VarType.BOOLEAN;
			}
		}
		return returnType;
	}

	public Boolean isArray(){
		return getNumberOfChildren() == 1;
	}
	
	/**
	 * returns null if not an array;
	 */
	public ExpressionNode getExpressionNode(){
		if (isArray()){
			return (ExpressionNode) getFirstChild();
		}
		assert false;
		return null;
	}
	
	public void setExpressionNode(ExpressionNode x){
		if (isArray()){
			replaceChild(0, x);
			return;
		}
		assert false;
	}
	
	/**
	 * Returns the ID name if just an INT or BOOLEAN, but includes the index if
	 * it's an array element. i.e. "a[5]" if applicable.
	 */
	public String getRepresentation() {
		String out = getText();
		if (getNumberOfChildren() == 1) {
			out += "[" + getFirstChild().getText() + "]";
		}
		return out;
	}
	
	@Override
	public void applyRules(Scope scope) {
		SemanticRules.apply(this, scope);
	}
	
	@Override
	public MidNodeList convertToMidLevel(MidSymbolTable symbolTable) {
		return MidVisitor.visit(this, symbolTable);
	}
	
	// FIXME: HACK. Pretend the variable is int, since we can just load it.
	@Override
	public VarType getMidVarType(MidSymbolTable symbolTable){
		return VarType.INT;
	}
	
	@Override
	public MidNodeList shortCircuit(MidSymbolTable symbolTable, MidLabelNode trueLabel, MidLabelNode falseLabel){
		return MidShortCircuitVisitor.shortCircuit(this, symbolTable, trueLabel, falseLabel);
	}
	
	@Override
	public ExpressionNode simplify(MidSymbolTable symbolTable) {
		return AlgebraicSimplifier.simplifyExpression(this, symbolTable);
	}
	
	@Override
	public boolean hasMethodCalls() {
		return false;
	}

	@Override
	public void simplifyExpressions(){
		AlgebraicSimplifier.visit(this);
	}

}