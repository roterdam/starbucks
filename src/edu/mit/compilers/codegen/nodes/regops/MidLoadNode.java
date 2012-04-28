package edu.mit.compilers.codegen.nodes.regops;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;
import edu.mit.compilers.codegen.nodes.memory.ArrayReferenceNode;
import edu.mit.compilers.codegen.nodes.memory.MidArrayElementNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;

/**
 * Loads value from memory.
 */
public class MidLoadNode extends MidRegisterNode implements ArrayReferenceNode {

	private MidMemoryNode memoryNode;
	private List<RegisterOpNode> registerOpNodes;
	private MidMemoryNode oldMemoryNode;

	public MidLoadNode(MidMemoryNode memoryNode) {
		super();
		this.memoryNode = memoryNode;
		registerOpNodes = new ArrayList<RegisterOpNode>();
	}

	public MidMemoryNode getMemoryNode() {
		return memoryNode;
	}

	public String toString() {
		String className = getClass().getName();
		int mid = className.lastIndexOf('.') + 1;
		String isArray = usesArrayReference() ? "[A]" : "";
		String prefix = isOptimization ? "[OPT] " : "";
		String oldRef = (oldMemoryNode == null) ? "" : " (" + oldMemoryNode.getName() + ")";
		return prefix + "<" + className.substring(mid) + ": " + memoryNode.getName() + oldRef
				+ " -> " + getName() + " " + isArray + ">";
	}

	@Override
	public String toDotSyntax() {
		return super.toDotSyntax() + memoryNode.hashCode() + " -> "
				+ hashCode() + " [style=dotted,color=orange];\n";
	}

	public List<ASM> toASM() {
		List<ASM> out = new ArrayList<ASM>();
		out.add(new OpASM(toString(), OpCode.MOV, getRegister().name(),
				memoryNode.getFormattedLocationReference()));
		return out;
	}

	public void recordRegisterOp(RegisterOpNode opNode) {
		registerOpNodes.add(opNode);
	}

	public void replaceThisReferences(MidLoadNode newNode) {
		for (RegisterOpNode opNode : registerOpNodes) {
			opNode.updateLoadNode(this, newNode);
		}
	}

	public void replace(MidLoadNode node) {
		insertAfter(node);
		node.delete();
		node.replaceThisReferences(this);
	}

	public boolean usesArrayReference() {
		return memoryNode instanceof MidArrayElementNode;
	}

	public Reg getArrayRegister() {
		assert usesArrayReference();
		return ((MidArrayElementNode) memoryNode).getLoadRegister();
	}

	public void updateMemoryNode(MidMemoryNode tempReplacement, boolean isOptimization) {
		oldMemoryNode = memoryNode;
		memoryNode = tempReplacement;
		if (isOptimization) {
			this.isOptimization = true;
		}
	}
	
}
