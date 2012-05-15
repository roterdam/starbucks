package edu.mit.compilers.codegen.nodes;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.asm.ASM;


public class FillerMidNode extends MidNode {
	
	@Override
	public List<ASM> toASM() {
		return new ArrayList<ASM>();
	}

}
