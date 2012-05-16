package edu.mit.compilers.codegen.nodes.regops;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpCode;

public class MidPlusNode extends MidArithmeticNode {

	public MidPlusNode(MidLoadNode leftOperand, MidLoadNode rightOperand) {
		super(leftOperand, rightOperand);
	}

	@Override
	public List<ASM> toASM() {
		return super.toASM(OpCode.ADD);
	}

	@Override
	public boolean isCommutative() {
		return true;
	}

	@Override
	public long applyOperation(long left, long right) {
		LogCenter.debug("MAS",String.format("%d + %d = %d", left, right, left+right));
		return left + right;
	}
	
	@Override
	public List<Identity> getIdentities() {
		List<Identity> ids = new ArrayList<Identity>();
		ids.add(new Identity(0,1, Identity.IdType.BOTH, Identity.OutType.OTHER));
		//ids.add(new Identity(1,0, Identity.IdType.BOTH, Identity.OutType.IN));
		return ids;
	}

}
