package edu.mit.compilers.grammar;

import edu.mit.compilers.crawler.MethodDecl;
import edu.mit.compilers.crawler.Scope;
import edu.mit.compilers.crawler.VarType;


@SuppressWarnings("serial")
public class METHOD_CALLNode extends ExpressionNode {
	public String getMethodName(){
		assert this.getFirstChild() instanceof METHOD_IDNode;
		return ((METHOD_IDNode)this.getFirstChild()).getText();
	}
	@Override
	public VarType getReturnType(Scope scope) {
		assert scope.getMethods().containsKey(getMethodName());
		MethodDecl method = scope.getMethods().get(getMethodName());
		return method.getReturnType();
	}

}