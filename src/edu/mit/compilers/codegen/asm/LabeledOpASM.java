package edu.mit.compilers.codegen.asm;

public class LabeledOpASM extends OpASM {
	private String label;
	public LabeledOpASM(String label, OpCode op, String... args) {
		super(op, args);
		this.label = label;
	}
	
	@Override 
	public String toString(){
		return String.format("%s:     %s", label, super.toString());
	}
}
