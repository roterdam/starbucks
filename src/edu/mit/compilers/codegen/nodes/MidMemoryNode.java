package edu.mit.compilers.codegen.nodes;

import java.util.List;

import edu.mit.compilers.codegen.MemoryManager;
import edu.mit.compilers.codegen.asm.ASM;

public class MidMemoryNode extends MidNode {

	private String name;

	
	public MidMemoryNode(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public String toString() {
		String className = getClass().getName();
		int mid = className.lastIndexOf('.') + 1;
		return "<" + className.substring(mid) + ": " + name + ">";
	}
	public List<ASM> toASM() {
		return MemoryManager.LoadDecl(this);
	}
}