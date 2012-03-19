package edu.mit.compilers.codegen.nodes;

import java.util.List;

import edu.mit.compilers.codegen.AsmVisitor;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.regops.MidRegisterNode;

public class MidCalloutNode extends MidRegisterNode {

	String name;
	List<MidMemoryNode> params;

	public MidCalloutNode(String name, List<MidMemoryNode> params) {
		this.name = name;
		this.params = params;
	}

	public List<MidMemoryNode> getParams() {
		return params;
	}

	public List<ASM> toASM() {
		return AsmVisitor.methodCall(name, params, getRegister(), true);
	}

}
