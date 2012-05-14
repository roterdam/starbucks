package edu.mit.compilers.codegen.nodes;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.AsmVisitor;
import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.MidVisitor;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;

/**
 * Prints a given error message (taking in the method name) and exits. Used for
 * run-time exceptions.
 */
public class MidPrintAndExitNode extends MidNode {

	List<MidMemoryNode> params;

	public MidPrintAndExitNode(MidMemoryNode errorString,
			MidMemoryNode methodNameNode) {
		params = new ArrayList<MidMemoryNode>();
		params.add(errorString);
		params.add(methodNameNode);
	}

	@Override
	public List<ASM> toASM() {
		MidNodeList nodeInstr = MidVisitor
				.makeMethodCall(new MidCalloutNode(AsmVisitor.PRINTF, params
						.size()), new MidNodeList(), new MidNodeList(), new MidNodeList(), params, false);
		return nodeInstr.toASM();
		// return new ArrayList<ASM>();
	}
}
