package edu.mit.compilers.codegen.nodes.memory;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;

/**
 * Represents memory on the stack that would be deallocated after a method.
 */
abstract public class MidLocalMemoryNode extends MidMemoryNode {
	
	public MidLocalMemoryNode(String name) {
		super(name);
	}
	
	@Override
	public String getFormattedLocationReference() {
		// qword gives it 64 bits.
		return String.format("qword [ %s - %s ]", Reg.RBP.name(), rawLocationReference);
	}
	
	@Override
	public List<ASM> toASM() {
		// Don't return any ASM code!
		// Because we've allocated this in MidMethodDeclNode.
		return new ArrayList<ASM>();
	}
	
}
