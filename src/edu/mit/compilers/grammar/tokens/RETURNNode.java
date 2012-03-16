package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.MidVisitor;
import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.SemanticRules;
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
	
	public ExpressionNode getReturnExpression() {
		assert getChild(0) instanceof ExpressionNode;
		return (ExpressionNode) getChild(0);
	}

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
	
	@Override
	public void applyRules(Scope scope) {
		SemanticRules.apply(this, scope);
	}
	
	@Override
	public MidNodeList convertToMidLevel(MidSymbolTable symbolTable) {
		return MidVisitor.visit(this, symbolTable);
	}
}