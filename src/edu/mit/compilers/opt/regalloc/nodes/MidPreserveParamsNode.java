package edu.mit.compilers.opt.regalloc.nodes;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.AsmVisitor;
import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.regops.MidParamLoadNode;
import edu.mit.compilers.opt.meta.Optimizer;

/**
 * At ASM time, will check if the referenced parameters are moving overwritten
 * register values (already used for earlier params) into a later param
 * register. If so, then this class pre-emptively pushes the to-be-overwritten
 * value onto the stack so it can be popped off later by the param register.
 * 
 */
public class MidPreserveParamsNode extends MidNode {

	private final List<MidParamLoadNode> paramNodes;
	private int pushedParamCount;

	public MidPreserveParamsNode(List<MidParamLoadNode> paramNodes) {
		this.paramNodes = paramNodes;
		for (MidParamLoadNode paramNode : paramNodes) {
			paramNode.registerPreserveNode(this);
		}
		pushedParamCount = 0;
	}

	@Override
	public List<ASM> toASM() {
		List<ASM> out = new ArrayList<ASM>();

		List<ASM> regParams = new ArrayList<ASM>();
		List<ASM> stackParams = new ArrayList<ASM>();
		for (MidParamLoadNode paramNode : paramNodes) {
			Reg fromReg = paramNode.getAllocatedRegister();
			if (fromReg == null) {
				continue;
			}
			Reg destReg = paramNode.getRegister();
			if (regWillBeOverwritten(fromReg, destReg)) {
				OpASM opASM = new OpASM("Save from overwriting.", OpCode.PUSH,
						fromReg.name());
				if (findRegisterIndex(destReg) == -1) {
					stackParams.add(opASM);
				} else {
					regParams.add(0, opASM);
				}
				pushedParamCount++;
			}
		}

		out.addAll(stackParams);
		out.addAll(regParams);

		return out;
	}

	/**
	 * If true, means that the register we're loading from already has been
	 * overwritten! For example, if we're loading a web value RDI into RSI, RDI
	 * has already been overwritten by then.
	 */
	public static boolean regWillBeOverwritten(Reg fromReg, Reg destReg) {
		int destRegIndex = findRegisterIndex(destReg);
		int fromRegIndex = findRegisterIndex(fromReg);
		if (fromRegIndex == -1) {
			// Not a param reg.
			return false;
		}
		if (destRegIndex == -1) {
			// Definitely saving FROM a param reg, and since the destination
			// register isn't a param reg we can only assume that it's something
			// like %r10 or %r11, being pushed onto the stack. In that case the
			// from register has most definitely been overwritten.
			return true;
		}
		return (fromRegIndex < destRegIndex);
	}

	public static int findRegisterIndex(Reg reg) {
		for (int i = 0; i < AsmVisitor.paramRegisters.length; i++) {
			if (AsmVisitor.paramRegisters[i] == reg) {
				return i;
			}
		}
		return -1;
	}

	private int stackOffset;
	private int iterID = -1;

	public void shiftOffset() {
		if (iterID == -1 || iterID != Optimizer.getIterID()) {
			iterID = Optimizer.getIterID();
			stackOffset = 0;
		}
		stackOffset += 8;
		LogCenter.debug("JM", "Offset increased to " + stackOffset);
	}

	public int getOffset() {
		if (iterID == -1 || iterID != Optimizer.getIterID()) {
			iterID = Optimizer.getIterID();
			stackOffset = 0;
		}
		return stackOffset;
	}

	public int getPushedParamCount() {
		return pushedParamCount;
	}

}
