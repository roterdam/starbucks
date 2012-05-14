package edu.mit.compilers.codegen.nodes.regops;

import java.util.ArrayList;
import java.util.List;

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

	public MidParamLoadNode(MidMemoryNode memoryNode) {
		super(memoryNode);
	}

	@Override
	public List<ASM> toASM() {
		Reg fromReg = getAllocatedRegister();
		if (fromReg != null) {
			Reg destReg = getRegister();
			if (MidPreserveParamsNode.regWillBeOverwritten(fromReg, destReg)) {
				List<ASM> out = new ArrayList<ASM>();
				out.add(new OpASM(
						String.format("Phew, reg was saved for us. (%s <- %s)", destReg
								.name(), fromReg.name()), OpCode.POP, destReg
								.name()));
				return out;
			}
		}
		return super.toASM();
	}

}
