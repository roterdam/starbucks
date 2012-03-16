package edu.mit.compilers.codegen.asm;

public class SectionASM extends ASM {
	
	private String sectionName;
	
	public SectionASM(String sectionName) {
		this.sectionName = sectionName;
	}
	
	public String toString() {
		return String.format("section .%s\n", sectionName);
	}

}
