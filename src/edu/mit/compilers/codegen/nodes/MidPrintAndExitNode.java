package edu.mit.compilers.codegen.nodes;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.AsmVisitor;
import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;

/**
 * Prints a given error message (taking in the method name) and exits. Used for
 * run-time exceptions.
 */
public class MidPrintAndExitNode extends MidNode {

	List<MidLoadNode> params;

	public MidPrintAndExitNode(MidMemoryNode errorString,
			MidMemoryNode methodNameNode) {
		params = new ArrayList<MidLoadNode>();
		params.add(new MidLoadNode(errorString));
		params.add(new MidLoadNode(methodNameNode));
	}

	@Override
	public List<ASM> toASM() {
		List<ASM> out = new ArrayList<ASM>();
		// Save output to any old register.
		out.addAll(AsmVisitor.methodCall(AsmVisitor.PRINTF, params, Reg.RAX,
				true, new ArrayList<Reg>()));
		out.addAll(AsmVisitor.exitCall(0));
		return out;
	}

}
