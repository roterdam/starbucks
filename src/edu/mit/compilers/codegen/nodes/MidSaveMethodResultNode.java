package edu.mit.compilers.codegen.nodes;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;

public class MidSaveMethodResultNode extends MidNode {
	MidCallNode node;
	public MidSaveMethodResultNode(MidCallNode node){
		this.node = node;
	}
	
	@Override
	public List<ASM> toASM() {
		List<ASM> out = new ArrayList<ASM>();
		out.add(new OpASM("Saving results of " + node.getName(), OpCode.MOV,
				node.getRegister().name(), Reg.RAX.name()));
		return out;
	}
}
