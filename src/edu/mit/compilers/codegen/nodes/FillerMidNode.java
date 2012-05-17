package edu.mit.compilers.codegen.nodes;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.asm.ASM;


public class FillerMidNode extends MidNode {
	
	private String comment;

	public FillerMidNode() {
		this.comment = "";
	}
	
	public FillerMidNode(String comment) {
		this.comment = comment;
	}
	
	@Override
	public List<ASM> toASM() {
		return new ArrayList<ASM>();
	}
	
	@Override
	public String toString() {
		return "<FillerMidNode: " + comment + ">";
	}

}
