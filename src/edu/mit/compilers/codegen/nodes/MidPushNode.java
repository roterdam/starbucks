package edu.mit.compilers.codegen.nodes;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.RegisterOpNode;

//FIXME: hacky. make a second class?
public class MidPushNode extends MidNode implements RegisterOpNode {
	Reg r;
	MidLoadNode node;
	
	public MidPushNode(Reg r){
		this.r = r;
	}
	public MidPushNode(MidLoadNode node){
		this.node = node;
	}
	@Override
	public List<ASM> toASM() {
		List<ASM> out = new ArrayList<ASM>();
		if(this.node != null){
			r = node.getRegister();
		}
		out.add(new OpASM("push param onto stack", OpCode.PUSH, r.name()));
		return out;
	}
	@Override
	public List<Reg> getOperandRegisters() {
		List<Reg> out = new ArrayList<Reg>();
		out.add(node.getRegister());
		return out;
	}
	
	@Override
	public void updateLoadNode(MidLoadNode oldNode, MidLoadNode newNode) {
		node =  newNode;
	}
}
