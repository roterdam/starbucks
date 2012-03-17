package edu.mit.compilers.codegen.nodes;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.MemoryManager;
import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidRegisterNode;

public class MidMethodNode extends MidRegisterNode {

	MidMethodDeclNode methodDecl;
	List<MidMemoryNode> params;

	public MidMethodNode(MidMethodDeclNode methodDecl,
			List<MidMemoryNode> params) {
		this.methodDecl = methodDecl;
		this.params = params;
	}

	@Override
	public String toDotSyntax() {
		String out = super.toDotSyntax();
		for (MidMemoryNode paramNode : params) {
			out += paramNode.hashCode() + " -> " + hashCode()
					+ " [style=dotted,color=green];\n";
		}
		return out;
	}

	@Override
	public List<ASM> toASM() {
		List<ASM> out = new ArrayList<ASM>();
		// Begin calling convention, place as many nodes in registers as
		// possible.
		Reg[] paramRegisters = new Reg[] { Reg.RDI, Reg.RSI, Reg.RDX, Reg.RCX,
				Reg.R8, Reg.R9 };

		for (int i = 0; i < params.size(); i++) {
			MidLoadNode paramNode = new MidLoadNode(params.get(i));
			if (i < paramRegisters.length) {
				// Want to set the register.
				paramNode.setRegister(paramRegisters[i]);
				out.addAll(paramNode.toASM());
			} else {
				paramNode.setRegister(MemoryManager.getTempRegister());
				out.addAll(paramNode.toASM());
				out.add(new OpASM(String.format("push param %d onto stack", i),
						OpCode.PUSH, paramNode.getRegister().name()));
			}
		}
		out.add(new OpASM(OpCode.CALL, methodDecl.getName()));
		int stackParams = params.size() - paramRegisters.length;
		if (stackParams > 0) {
			out.add(new OpASM("clean up params", OpCode.MOV, Reg.RSP.name(),
					String.format("[ %s - %d ]", Reg.RSP.name(), stackParams
							* MemoryManager.ADDRESS_SIZE)));
		}
		return out;
	}
}
