package edu.mit.compilers.codegen.nodes;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;

public class MidFieldDeclNode extends MidMemoryNode {
	private String name;

	// TODO: When we get to implementing space allocation, array subclass knows
	// to do it differently.

	public MidFieldDeclNode(String name) {
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
	
}
