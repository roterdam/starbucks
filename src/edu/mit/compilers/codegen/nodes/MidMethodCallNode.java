package edu.mit.compilers.codegen.nodes;

import java.util.List;

import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;

public class MidMethodCallNode extends MidCallNode {

	MidMethodDeclNode methodDecl;
	boolean starbucksCall;

	public MidMethodCallNode(MidMethodDeclNode methodDecl,
			List<MidMemoryNode> params) {
		this(methodDecl, params, false);
	}

	public MidMethodCallNode(MidMethodDeclNode methodDecl,
			List<MidMemoryNode> params, boolean starbucksCall) {
		super(methodDecl.getName(), params);
		this.methodDecl = methodDecl;
		this.starbucksCall = starbucksCall;
	}

	@Override
	public String toDotSyntax() {
		String out = super.toDotSyntax();
		for (MidMemoryNode paramNode : getParams()) {
			out += paramNode.hashCode() + " -> " + hashCode()
					+ " [style=dotted,color=green];\n";
		}
		return out;
	}

	public boolean isStarbucksCall() {
		return starbucksCall;
	}

}
