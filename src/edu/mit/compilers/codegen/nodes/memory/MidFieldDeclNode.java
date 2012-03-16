package edu.mit.compilers.codegen.nodes.memory;

import edu.mit.compilers.codegen.asm.LabelASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;

public class MidFieldDeclNode extends MidMemoryNode {

	// TODO: When we get to implementing space allocation, array subclass knows
	// to do it differently.

	public MidFieldDeclNode(String name) {
		super(name);
	}

	public LabelASM getFieldLabel() {
		return new LabelASM("", rawLocationReference);
	}

	public OpASM getFieldDeclaration() {
		// TOOD: resw of different sizes
		return new OpASM(String.format("placeholder for `%s`", getName()),
				OpCode.RESW, "1");
	}

	@Override
	public String getFormattedLocationReference() {
		return String.format("[ %s ]", rawLocationReference);
	}

}
