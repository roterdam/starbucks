package edu.mit.compilers.codegen.nodes.jumpops;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;
import edu.mit.compilers.codegen.nodes.MidLabelNode;
import edu.mit.compilers.codegen.nodes.MidNode;

public class MidJumpNode extends MidNode {
	
	private MidLabelNode labelNode;
	
	public MidJumpNode(MidLabelNode labelNode) {
		this.labelNode = labelNode;
	}

	public MidLabelNode getLabelNode() {
		return labelNode;
	}
	public String toString(){
		return "<"+this.getNodeClass()+": "+getLabelNode()+">";
	}
	
	@Override
	public String toDotSyntax() {
		return super.toDotSyntax() + hashCode() + " -> " + labelNode.hashCode() + " [style=dashed,color=red];\n";
	}
	
	public List<ASM> toASM(OpCode op) {
		List<ASM> out = new ArrayList<ASM>();
		out.add(new OpASM(toString(), op, this.getLabelNode().getName()));
		return out;
	}
	public List<ASM> toASM(){
		return toASM(OpCode.JMP);
	}
	
}
