package edu.mit.compilers.codegen.nodes.regops;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpCode;

public class MidOrNode extends MidBinaryRegNode {

	public MidOrNode(MidLoadNode leftOperand, MidLoadNode rightOperand) {
		super(leftOperand, rightOperand);
	}

	public List<ASM> toASM() {
		return super.toASM(OpCode.OR);
	}

	@Override
	public long applyOperation(long left, long right) {
		return left | right;
	}
	
	@Override
	public List<Identity> getIdentities() {
		List<Identity> ids = new ArrayList<Identity>();
		ids.add(new Identity(0,1, Identity.IdType.BOTH, Identity.OutType.OTHER));
		ids.add(new Identity(1,0, Identity.IdType.BOTH, Identity.OutType.IN));
		return ids;
	}
	
}
