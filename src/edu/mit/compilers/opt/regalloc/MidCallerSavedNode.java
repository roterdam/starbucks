package edu.mit.compilers.opt.regalloc;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import edu.mit.compilers.codegen.AsmVisitor;
import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.nodes.MidCallNode;
import edu.mit.compilers.codegen.nodes.MidNode;

public class MidCallerSavedNode extends MidNode {

	private final MidCallNode callNode;

	public MidCallerSavedNode(MidCallNode callNode) {
		this.callNode = callNode;
	}

	LinkedHashSet<Reg> getRegConflicts() {
		int numParams = callNode.getParamCount();

		LinkedHashSet<Reg> out = new LinkedHashSet<Reg>();
		for (int i = 0; i < Math
				.min(numParams, AsmVisitor.paramRegisters.length); i++) {
			out.add(AsmVisitor.paramRegisters[i]);
		}
		Set<Reg> liveRegs = new HashSet<Reg>(callNode.getNeedToSaveRegisters());
		out.retainAll(liveRegs);

		return out;
	}

}
