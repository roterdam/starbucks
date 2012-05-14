package edu.mit.compilers.codegen.nodes;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;
import edu.mit.compilers.opt.Block;

public abstract class MidNode {
	private MidNode nextNode;
	private MidNode prevNode;
	public boolean isOptimization;

	public void setNextNode(MidNode node) {
		nextNode = node;
		// Also set a backpointer.
		if(nextNode != null) {
			nextNode.setPrevNode(this);
		}
	}

	public MidNode getNextNode() {
		return nextNode;
	}
	
	public void setPrevNode(MidNode node) {
		prevNode = node;
	}
	
	public MidNode getPrevNode() {
		return prevNode;
	}
	
	public String toString() {
		return "<" + getNodeClass() + ">";
	}

	public String getNodeClass() {
		String className = getClass().getName();
		int mid = className.lastIndexOf('.') + 1;
		return className.substring(mid);
	}

	public List<ASM> toASM() {
		List<ASM> out = new ArrayList<ASM>();
		out.add(new OpASM("Error: IMPLEMENT toASM for " + this.getNodeClass(), OpCode.NOP));
		return out;
	}

	/**
	 * Declares node. Override in child class and add on to return string to
	 * make other connections.
	 */
	public String toDotSyntax() {
		return hashCode() + " [label=\"" + toString() + "\"];\n";
	}
	
	public void insertAfter(MidNode n) {
		MidNode oldNext = n.getNextNode();
		n.setNextNode(this);
		this.setNextNode(oldNext);
	}
	
	//FIXME what if this is the first node?
	public void delete() {
		LogCenter.debug("OPT", "DELETING " + this + " (" + hashCode() + ")");
		this.getPrevNode().setNextNode(this.getNextNode());
	}
}
