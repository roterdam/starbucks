package edu.mit.compilers.codegen.nodes.jumpops;

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
}
