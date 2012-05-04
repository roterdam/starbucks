package edu.mit.compilers.codegen.nodes;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.AsmVisitor;
import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.nodes.memory.MemoryUser;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;

/**
 * Prints a given error message (taking in the method name) and exits. Used for
 * run-time exceptions.
 */
public class MidPrintAndExitNode extends MidNode implements MemoryUser {
	
	List<MidMemoryNode> params;
	
	public MidPrintAndExitNode(MidMemoryNode errorString, MidMemoryNode methodNameNode) {
		params = new ArrayList<MidMemoryNode>();
		params.add(errorString);
		params.add(methodNameNode);
	}
	
	@Override
	public List<ASM> toASM() {
		List<ASM> out = new ArrayList<ASM>();
		// Save output to any old register.
		out.addAll(AsmVisitor.methodCall(AsmVisitor.PRINTF, params, Reg.RAX, true));
		out.addAll(AsmVisitor.exitCall(0));
		return out;
	}

	@Override
	public List<MidMemoryNode> getUsedMemoryNodes() {
		return new ArrayList<MidMemoryNode>(params);
	}

}
