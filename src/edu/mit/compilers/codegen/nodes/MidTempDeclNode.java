package edu.mit.compilers.codegen.nodes;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.AsmVisitor;
import edu.mit.compilers.codegen.MidLabelManager;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;

public class MidTempDeclNode extends MidMemoryNode {
	private String name;

	static int nodeNum = 0;

	public MidTempDeclNode() {
		super();
		// generate a random temp id
		this.name = "t" + MidLabelManager.getNewId();
	}

	public String getName() {
		return name;
	}

	public String toString() {
		String className = getClass().getName();
		int mid = className.lastIndexOf('.') + 1;
		return "<" + className.substring(mid) + ": " + name + " >";
	}

	public List<ASM> toASM() {

		List<ASM> out = new ArrayList<ASM>();

		out.add(new OpASM(name, OpASM.OpCode.MOV, "rsp", "[rsp-"
				+ AsmVisitor.ADDRESS_SIZE_STRING + "]"));

		return out;
	}
}