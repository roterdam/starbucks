package edu.mit.compilers.codegen.nodes;

import java.util.List;

import edu.mit.compilers.codegen.AsmVisitor;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.regops.MidRegisterNode;

public class MidMethodNode extends MidRegisterNode {

	MidMethodDeclNode methodDecl;
	List<MidMemoryNode> params;

	public MidMethodNode(MidMethodDeclNode methodDecl,
			List<MidMemoryNode> params) {
		this.methodDecl = methodDecl;
		this.params = params;
	}

	@Override
	public String toDotSyntax() {
		String out = super.toDotSyntax();
		for (MidMemoryNode paramNode : params) {
			out += paramNode.hashCode() + " -> " + hashCode()
					+ " [style=dotted,color=green];\n";
		}
		return out;
	}

	@Override
	public List<ASM> toASM() {
		return AsmVisitor.methodCall(methodDecl.getName(), params, getRegister(), false);
	}
}
