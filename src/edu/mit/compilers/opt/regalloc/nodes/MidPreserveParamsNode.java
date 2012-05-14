package edu.mit.compilers.opt.regalloc.nodes;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.AsmVisitor;
import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.regops.MidParamLoadNode;

/**
 * At ASM time, will check if the referenced parameters are moving overwritten
 * register values (already used for earlier params) into a later param
 * register. If so, then this class pre-emptively pushes the to-be-overwritten
 * value onto the stack so it can be popped off later by the param register.
 * 
 */
public class MidPreserveParamsNode extends MidNode {

	private final List<MidParamLoadNode> paramNodes;

	public MidPreserveParamsNode(List<MidParamLoadNode> paramNodes) {
		this.paramNodes = paramNodes;
	}

	@Override
	public List<ASM> toASM() {
		List<ASM> out = new ArrayList<ASM>();

		for (MidParamLoadNode paramNode : paramNodes) {
			Reg fromReg = paramNode.getAllocatedRegister();
			if (fromReg == null) {
				continue;
			}
			Reg destReg = paramNode.getRegister();
			if (regWillBeOverwritten(fromReg, destReg)) {
				out.add(0, new OpASM("Save from overwriting.", OpCode.PUSH,
						fromReg.name()));
			}
		}

		return out;
	}

	/**
	 * If true, means that the register we're loading from already has been
	 * overwritten! For example, if we're loading a web value RDI
	 * into RSI, RDI has already been overwritten by then.
	 */
	public static boolean regWillBeOverwritten(Reg fromReg, Reg destReg) {
		int fromRegIndex = findRegisterIndex(fromReg);
		int destRegIndex = findRegisterIndex(destReg);
		return (fromRegIndex < destRegIndex);
	}

	private static int findRegisterIndex(Reg reg) {
		for (int i = 0; i < AsmVisitor.paramRegisters.length; i++) {
			if (AsmVisitor.paramRegisters[i] == reg) {
				return i;
			}
		}
		return AsmVisitor.paramRegisters.length;
	}

}
