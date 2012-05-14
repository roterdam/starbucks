package edu.mit.compilers.codegen.nodes.regops;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;
import edu.mit.compilers.opt.regalloc.MidRDXOverwriter;

public class MidDivideNode extends MidRDXOverwriter {

	public MidDivideNode(MidLoadNode leftOperand, MidLoadNode rightOperand) {
		super(leftOperand, rightOperand);
	}

	@Override
	public List<ASM> toASM() {
		List<ASM> out = new ArrayList<ASM>();
		// a/b -> a is dividend, b is divisor (i always forget :[)
		String RAX = Reg.RAX.name();
		if (shouldPreserveRDX()) {
			out.add(new OpASM(toString() + " (save)", OpCode.PUSH, Reg.RDX
					.name()));
		}
		out.add(new OpASM(toString(), OpCode.MOV, RAX, this.getLeftOperand()
				.getRegister().name()));
		out.add(new OpASM(toString(), OpCode.CQO));
		out.add(new OpASM(toString(), OpCode.IDIV, this.getRightOperand()
				.getRegister().name()));
		out.add(new OpASM(toString(), OpCode.MOV, this.getRegister().name(),
				RAX));
		if (shouldPreserveRDX()) {
			out.add(new OpASM(toString() + " (restore)", OpCode.POP, Reg.RDX
					.name()));
		}
		return out;
	}
	
	@Override
	public long applyOperation(long left, long right) {
		return left / right;
	}

}
