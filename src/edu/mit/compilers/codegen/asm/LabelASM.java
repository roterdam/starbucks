package edu.mit.compilers.codegen.asm;

public class LabelASM extends ASM{

	private String name;
	private String comment;
	
	public LabelASM(String name, String comment) {
		this.name = name;
		this.comment = comment;
	}
	
	@Override
	public String toString(){
		if (comment != ""){
			return String.format("%-24s ; %s\n", name + ":", comment);
		} else {
			return String.format("   %s:\n", name);
		}
	}
	
	
	
}
