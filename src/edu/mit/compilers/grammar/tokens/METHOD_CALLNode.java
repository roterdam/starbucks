package edu.mit.compilers.grammar.tokens;

import edu.mit.compilers.crawler.MethodDecl;
import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.VarType;
import edu.mit.compilers.grammar.ExpressionNode;

@SuppressWarnings("serial")
public class METHOD_CALLNode extends ExpressionNode {
	public String getMethodName() {
		assert this.getFirstChild() instanceof METHOD_IDNode;
		return ((METHOD_IDNode) this.getFirstChild()).getText();
	}

	@Override
	public VarType getReturnType(Scope scope) {
		System.out.println("I was asked for my return type! " + toStringTree());
		
		assert scope.getMethods().containsKey(getMethodName());
		MethodDecl method = scope.getMethods().get(getMethodName());
		System.out.println(method.getReturnType());
		return method.getReturnType();
	}

}