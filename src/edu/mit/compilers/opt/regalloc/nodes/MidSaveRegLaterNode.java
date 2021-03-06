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
 * Links to a method and, while in the mid node list, doesn't explicitly say
 * which registers to save because it doesn't know what will be allocated during
 * register allocation. During toASM() time, it then checks the method to see if
 * the method is using any registers that conflict with live webs.
 * 
 */
public class MidSaveRegLaterNode extends MidNode {

	private final MidCallNode callNode;

	public MidSaveRegLaterNode(MidCallNode callNode) {
		this.callNode = callNode;
	}

	@Override
	public List<ASM> toASM() {
		List<ASM> out = new ArrayList<ASM>();

		//out.add(new OpASM("MidSaveRegLaterNode", OpCode.NOP));
		
		List<Reg> needToSaveRegs = callNode.getNeedToSaveRegisters();
		for (Reg r : needToSaveRegs) {
			out.add(new OpASM("Save live reg.", OpCode.PUSH, r.name()));
		}

		return out;
	}

}
