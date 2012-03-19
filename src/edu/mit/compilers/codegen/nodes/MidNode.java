package edu.mit.compilers.codegen.nodes;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;

// TODO: make abstract
public abstract class MidNode {
	MidNode nextNode;

	public void setNextNode(MidNode node) {
		nextNode = node;
	}

	public MidNode getNextNode() {
		return nextNode;
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

	public List<Reg> getOperandRegisters() {
		return new ArrayList<Reg>();
	}
}
