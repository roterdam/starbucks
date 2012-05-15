package edu.mit.compilers.opt.regalloc.nodes;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.MemoryManager;
import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;
import edu.mit.compilers.codegen.nodes.MidNode;

/**
 * Cleans up the stack pushing that MidPreserveParamsNode did.
 */
public class MidUndoPreserveParamsNode extends MidNode {

	private MidPreserveParamsNode preserveParamsNode;

	public MidUndoPreserveParamsNode(MidPreserveParamsNode preserveParamsNode) {
		this.preserveParamsNode = preserveParamsNode;
	}

	@Override
	public List<ASM> toASM() {
		List<ASM> out = new ArrayList<ASM>();

		int offset = preserveParamsNode.getPushedParamCount()
				* MemoryManager.ADDRESS_SIZE;
		out.add(new OpASM("Fix up preserve params.", OpCode.ADD, Reg.RSP.name(), Integer.toString(offset)));

		return out;
	}

}
