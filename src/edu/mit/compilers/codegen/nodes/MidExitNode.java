package edu.mit.compilers.codegen.nodes;

import java.util.List;

import edu.mit.compilers.codegen.AsmVisitor;
import edu.mit.compilers.codegen.asm.ASM;

public class MidExitNode extends MidNode {
	int exitCode;

	public MidExitNode(int exitCode) {
		this.exitCode = exitCode;
	}
	
	@Override
	public List<ASM> toASM(){
		return AsmVisitor.exitCall(exitCode);
	}
}
