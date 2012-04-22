package edu.mit.compilers.codegen.asm;

public class LabelASM extends ASM {

	private String name;
	private String comment;

	public LabelASM(String comment, String name) {
		this.comment = comment;
		this.name = name;
	}

	@Override
	public String toString() {
		if (comment != "") {
			return String.format("\n%-44s ; %s\n", name + ":", comment);
		}
		return String.format("\n%s:\n", name);
	}

}
