package edu.mit.compilers.codegen.nodes.regops;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.MemoryManager;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.LocAndASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.nodes.MidMemoryNode;

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
		return "<" + className.substring(mid) + ": " + getName() + ">";
	}

	@Override
	public String toDotSyntax() {
		return super.toDotSyntax() + memoryNode.hashCode() + " -> "
				+ hashCode() + " [style=dotted,color=orange];\n";
	}
	
	@Override
	public List<ASM> toASM() {

		List<ASM> out = new ArrayList<ASM>();
		
		// TODO: replace with actual getRegisterId() defined in MidRegisterNode.
		
		LocAndASM mem = MemoryManager.getRegister(getMemoryNode().getName());
		// TODO: replace 16 with actual offset from memoryNode
		this.setRegisterId(mem.location);
		out.addAll(mem.ASM);
		//out.add(new OpASM(toString(), OpASM.OpCode.MOV, mem.location, "[rbp-16]"));

		return out;
	}
	
	
	
}
