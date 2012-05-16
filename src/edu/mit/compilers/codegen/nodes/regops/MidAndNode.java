package edu.mit.compilers.codegen.nodes.regops;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpCode;

public class MidAndNode extends MidBinaryRegNode {

	public MidAndNode(MidLoadNode leftOperand, MidLoadNode rightOperand) {
		super(leftOperand, rightOperand);
	}

	public List<ASM> toASM() {
		return super.toASM(OpCode.AND);
	}

	@Override
	public long applyOperation(long left, long right) {
		return left & right;
	}

	@Override
	public List<Identity> getIdentities() {
		List<Identity> ids = new ArrayList<Identity>();
		ids.add(new Identity(0,1, Identity.IdType.BOTH, Identity.OutType.IN));
		ids.add(new Identity(1,0, Identity.IdType.BOTH, Identity.OutType.OTHER));
		return ids;
	}
	

}
