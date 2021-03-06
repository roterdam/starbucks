package edu.mit.compilers.codegen.nodes.regops;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.opt.regalloc.nodes.MidPreserveParamsNode;

/**
 * Loads in a parameter for a MidCallNode.
 */
public class MidParamLoadNode extends MidLoadNode {

	private MidPreserveParamsNode preserveNode;

	public MidParamLoadNode(MidMemoryNode memoryNode) {
		super(memoryNode);
	}

	public void registerPreserveNode(MidPreserveParamsNode preserveNode) {
		this.preserveNode = preserveNode;
	}

	@Override
	public List<ASM> toASM() {
		assert preserveNode != null : "Make sure preserve node is set in MidVisitor.";
		Reg fromReg = getAllocatedRegister();
		Reg destReg = getRegister();

		List<ASM> out;
		if (fromReg != null
				&& MidPreserveParamsNode.regWillBeOverwritten(fromReg, destReg)) {
			out = new ArrayList<ASM>();
			out.add(new OpASM(String
					.format("Phew, reg was saved for us. (%s <- %s)", destReg
							.name(), fromReg.name()), OpCode.MOV, destReg
					.name(), String.format("qword [ RSP + %d ]", preserveNode
					.getOffset())));
			LogCenter.debug("JM", "Requesting we shift offset (" + this + ")");
			preserveNode.shiftOffset();
		} else {
			out = super.toASM();
		}
		int destRegIndex = MidPreserveParamsNode.findRegisterIndex(destReg);
		if (destRegIndex == -1) {
			// We'll be pushing to the stack, so we need to adjust the stack
			// offset for preserved params.
			LogCenter.debug("JM", "Requesting we shift offset (" + this + ")");
			preserveNode.shiftOffset();
		}
		return out;
	}

}
