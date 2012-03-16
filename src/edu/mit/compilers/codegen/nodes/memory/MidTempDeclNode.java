package edu.mit.compilers.codegen.nodes.memory;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.MidLabelManager;
import edu.mit.compilers.codegen.StorageVisitor;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;

public class MidTempDeclNode extends MidLocalMemoryNode {
	static int nodeNum = 0;

	public MidTempDeclNode() {
		// generate a random temp id
		super("t" + MidLabelManager.getNewId());
	}

	public List<ASM> toASM() {

		List<ASM> out = new ArrayList<ASM>();

		out.add(new OpASM(getName(), OpCode.MOV, "rsp", "[rsp-"
				+ StorageVisitor.ADDRESS_SIZE_STRING + "]"));

		return out;
	}

}