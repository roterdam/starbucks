package edu.mit.compilers.opt.regalloc;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;
import edu.mit.compilers.codegen.nodes.MidCallNode;

/**
 * Same as {@link MidSaveRegLaterNode}, but undos the work.
 * 
 */
public class MidRestoreRegLaterNode extends MidCallerSavedNode {

	public MidRestoreRegLaterNode(MidCallNode callNode) {
		super(callNode);
	}

	@Override
	public List<ASM> toASM() {
		List<ASM> out = new ArrayList<ASM>();

		LinkedHashSet<Reg> needToSaveRegs = getRegConflicts();
		for (Reg r : needToSaveRegs) {
			out.add(0, new OpASM("Restore live reg.", OpCode.POP, r.name()));
		}

		return out;
	}

}
