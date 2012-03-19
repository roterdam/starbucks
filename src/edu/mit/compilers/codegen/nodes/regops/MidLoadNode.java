package edu.mit.compilers.codegen.nodes.regops;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;

/**
 * Loads value from memory.
 */
public class MidLoadNode extends MidRegisterNode {

	private MidMemoryNode memoryNode;

	public MidLoadNode(MidMemoryNode memoryNode) {
		super();
		this.memoryNode = memoryNode;
	}

	public MidMemoryNode getMemoryNode() {
		return memoryNode;
	}

	public String toString() {
		String className = getClass().getName();
		int mid = className.lastIndexOf('.') + 1;
		return "<" + className.substring(mid) + ": " + getName() + ","
				+ memoryNode.toString() + ">";
	}

	@Override
	public String toDotSyntax() {
		return super.toDotSyntax() + memoryNode.hashCode() + " -> "
				+ hashCode() + " [style=dotted,color=orange];\n";
	}

	public List<ASM> toASM() {
		List<ASM> out = new ArrayList<ASM>();
		out.add(new OpASM(toString(), OpCode.MOV, getRegister().name(),
				memoryNode.getFormattedLocationReference()));
		return out;
	}
	
	@Override
	public List<Reg> getOperandRegisters() {
		// Load doesn't use registers, but memory node might.
		return memoryNode.getRegisters();
	}

}
