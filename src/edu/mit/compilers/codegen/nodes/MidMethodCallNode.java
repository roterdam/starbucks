package edu.mit.compilers.codegen.nodes;


public class MidMethodCallNode extends MidCallNode {

	MidMethodDeclNode methodDecl;
	boolean starbucksCall;

	public MidMethodCallNode(MidMethodDeclNode methodDecl, int paramCount) {
		this(methodDecl, paramCount, false);
	}

	public MidMethodCallNode(MidMethodDeclNode methodDecl, int paramCount,
			boolean starbucksCall) {
		super(methodDecl.getName(), paramCount);
		this.methodDecl = methodDecl;
		this.starbucksCall = starbucksCall;
	}

	public boolean isStarbucksCall() {
		return starbucksCall;
	}

}
