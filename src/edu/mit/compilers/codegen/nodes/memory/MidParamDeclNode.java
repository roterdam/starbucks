package edu.mit.compilers.codegen.nodes.memory;

import edu.mit.compilers.codegen.AsmVisitor;

public class MidParamDeclNode extends MidLocalMemoryNode {
	
	private int paramOffset;

	public MidParamDeclNode(String name, int paramOffset) {
		super(name);
		this.paramOffset = paramOffset;
	}

	@Override
	public String getFormattedLocationReference() {
		return AsmVisitor.paramAccess(paramOffset);
	}

}
