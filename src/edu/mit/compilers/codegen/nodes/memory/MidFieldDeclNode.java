package edu.mit.compilers.codegen.nodes.memory;

import edu.mit.compilers.codegen.asm.LabelASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;

public class MidFieldDeclNode extends MidMemoryNode {

	// Size of fields in terms of 16-bit "words".
	final int FIELD_SIZE = 4;
	final long FIELD_DEFAULT = 0;

	public MidFieldDeclNode(String name) {
		super(name);
	}

	public LabelASM getFieldLabelASM() {
		return new LabelASM("", rawLocationReference);
	}

	public OpASM getFieldDeclarationASM() {
		return new OpASM(
				String.format("placeholder for `%s`", getName()),
				OpCode.TIMES,
				String.format("%ld %s %d", getSize() * FIELD_SIZE, OpCode.DW, FIELD_DEFAULT));
	}

	/**
	 * Get the location reference. Returns reference to data, not pointer.
	 */

	@Override
	public String getFormattedLocationReference() {
		assert rawLocationReference != null : "rawLocationReference is null!";
		// Adding brackets evaluates to the _data at that address_
		return String.format("[ %s ]", rawLocationReference);
	}

	public long getSize() {
		return 1;
	}

}
