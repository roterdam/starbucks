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
import edu.mit.compilers.opt.regalloc.Allocatable;

/**
 * Loads value from memory.
 */
public class MidLoadNode extends MidRegisterNode implements ArrayReferenceNode,
		Allocatable {

	private MidMemoryNode memoryNode;
	private List<RegisterOpNode> registerOpNodes;
	private MidMemoryNode oldMemoryNode;
	private Reg sourceReg;

	public MidLoadNode(MidMemoryNode memoryNode) {
		super();
		this.memoryNode = memoryNode;
		registerOpNodes = new ArrayList<RegisterOpNode>();
	}

	public MidMemoryNode getMemoryNode() {
		assert sourceReg == null : "Shouldn't be getting this memory node after allocating it to a register!";
		return memoryNode;
	}

	public String toString() {
		String className = getClass().getName();
		int mid = className.lastIndexOf('.') + 1;
		String isArray = usesArrayRegister() ? "[A]" : "";
		String prefix = isOptimization ? "[OPT] " : "";
		String oldRef = (oldMemoryNode == null) ? "" : " ("
				+ oldMemoryNode.getName() + ")";
		return prefix + "<" + className.substring(mid) + ": "
				+ memoryNode.getName() + oldRef + " -> " + getName() + " "
				+ isArray + ">";
	}

	@Override
	public String toDotSyntax() {
		return super.toDotSyntax() + memoryNode.hashCode() + " -> "
				+ hashCode() + " [style=dotted,color=orange];\n";
	}

	@Override
	public List<ASM> toASM() {
		List<ASM> out = new ArrayList<ASM>();
		if (sourceReg == null) {
			out.add(new OpASM(toString(), OpCode.MOV, getRegister().name(),
					memoryNode.getFormattedLocationReference()));
		} else {
			// If the load node has instead been given a register, load from
			// that instead.
			out.add(new OpASM(toString(), OpCode.MOV, getRegister().name(),
					sourceReg.name()));
		}
		return out;
	}

	public void recordRegisterOp(RegisterOpNode opNode) {
		registerOpNodes.add(opNode);
	}

	@Override
	public boolean usesArrayRegister() {
		return (memoryNode instanceof MidArrayElementNode)
				&& !((MidArrayElementNode) memoryNode).isConstant();
	}

	@Override
	public MidArrayElementNode getMidArrayElementNode() {
		assert usesArrayRegister();
		return (MidArrayElementNode) memoryNode;
	}

	@Override
	public Reg getArrayRegister() {
		assert usesArrayRegister();
		return ((MidArrayElementNode) memoryNode).getLoadRegister();
	}

	public void updateMemoryNode(MidMemoryNode tempReplacement,
			boolean isOptimization) {
		assert tempReplacement != null : tempReplacement + " is null.";
		oldMemoryNode = memoryNode;
		memoryNode = tempReplacement;
		if (isOptimization) {
			this.isOptimization = true;
		}
	}

	public void allocateRegister(Reg allocatedReg) {
		this.sourceReg = allocatedReg;
	}

	@Override
	public Reg getAllocatedRegister() {
		return sourceReg;
	}

}
