package edu.mit.compilers.codegen.nodes.regops;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;
import edu.mit.compilers.opt.regalloc.nodes.MidRDXOverwriter;

public class MidDivideNode extends MidRDXOverwriter {

	public MidDivideNode(MidLoadNode leftOperand, MidLoadNode rightOperand) {
		super(leftOperand, rightOperand);
	}

	@Override
	public List<ASM> toASM() {
		List<ASM> out = new ArrayList<ASM>();
		// a/b -> a is dividend, b is divisor (i always forget :[)
		if (shouldPreserveRDX()) {
			out.add(new OpASM(toString() + " (save)", OpCode.PUSH, Reg.RDX
					.name()));
		}
		out.add(new OpASM(toString(), OpCode.MOV, Reg.RAX.name(), this
				.getLeftOperand().getRegister().name()));
		out.add(new OpASM(toString(), OpCode.CQO));
		out.add(new OpASM(toString(), OpCode.IDIV, this.getRightOperand()
				.getRegister().name()));
		out.add(new OpASM(toString(), OpCode.MOV, this.getRegister().name(),
				Reg.RAX.name()));
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
	
	@Override
	public List<Identity> getIdentities() {
		List<Identity> ids = new ArrayList<Identity>();
		ids.add(new Identity(0,1, Identity.IdType.LEFT, Identity.OutType.IN));
		ids.add(new Identity(1,0, Identity.IdType.RIGHT, Identity.OutType.OTHER));
		return ids;
	}

}
