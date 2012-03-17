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

	public LabelASM getFieldLabelASM() {
		return new LabelASM("", rawLocationReference);
	}

	public OpASM getFieldDeclarationASM() {
		// TOOD: resw of different sizes
		return new OpASM(String.format("placeholder for `%s`", getName()),
				OpCode.RESW, "1");
	}

	@Override
	public String getFormattedLocationReference() {
		return getFormattedLocationReference(false);
	}

	/**
	 * Get the location reference.
	 * 
	 * @param asPointer
	 *            Set to true if you want a pointer, not the actual data.
	 * @return
	 */
	public String getFormattedLocationReference(boolean asPointer) {
		String format;
		assert rawLocationReference != null : "rawLocationReference is null!";
		// Adding brackets evaluates to the _data at that address_
		if (asPointer) {
			format = "%s";
		} else {
			format = "[ %s ]";
		}
		return String.format(format, rawLocationReference);
	}

}
