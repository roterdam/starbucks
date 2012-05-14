package edu.mit.compilers.opt.regalloc.nodes;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;
import edu.mit.compilers.codegen.nodes.MidCallNode;
import edu.mit.compilers.codegen.nodes.MidNode;

/**
 * Same as {@link MidSaveRegLaterNode}, but undos the work.
 * 
 */
public class MidRestoreRegLaterNode extends MidNode {

	private final MidCallNode callNode;

	public MidRestoreRegLaterNode(MidCallNode callNode) {
		this.callNode = callNode;
	}

	@Override
	public List<ASM> toASM() {
		List<ASM> out = new ArrayList<ASM>();

		List<Reg> needToSaveRegs = callNode.getNeedToSaveRegisters();
		for (Reg r : needToSaveRegs) {
			out.add(0, new OpASM("Restore live reg.", OpCode.POP, r.name()));
		}

		return out;
	}

}
