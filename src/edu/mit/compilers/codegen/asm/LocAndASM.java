package edu.mit.compilers.codegen.asm;

import java.util.List;

public class LocAndASM {
	
	public String location;
	public List<ASM> ASM;
	
	public LocAndASM(String location, List<ASM> asm){
		this.location = location;
		this.ASM = asm;
	}
}