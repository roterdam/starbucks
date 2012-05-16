package edu.mit.compilers.codegen.nodes.regops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;
import edu.mit.compilers.codegen.nodes.memory.ArrayReferenceNode;
import edu.mit.compilers.codegen.nodes.memory.MidArrayElementNode;
import edu.mit.compilers.codegen.nodes.memory.MidFieldArrayDeclNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.opt.meta.Optimizer;
import edu.mit.compilers.opt.regalloc.nodes.Allocatable;

/**
 * Loads value from memory.
 */
public class MidLoadNode extends MidRegisterNode implements ArrayReferenceNode,
		Allocatable, MidUseNode {

	private MidMemoryNode memoryNode;
	private List<RegisterOpNode> registerOpNodes;
	private MidMemoryNode oldMemoryNode;
	private Map<Integer, Reg> allocatedRegs;

	public MidLoadNode(MidMemoryNode memoryNode) {
		super();
		assert !(memoryNode instanceof MidFieldArrayDeclNode) : "Tried to set memoryNode of load node to a MidFieldArrayDeclNode.";
		this.memoryNode = memoryNode;
		registerOpNodes = new ArrayList<RegisterOpNode>();
		allocatedRegs = new HashMap<Integer, Reg>();
	}

	public MidMemoryNode getMemoryNode() {
		assert allocatedRegs.isEmpty() : "Shouldn't be getting this memory node after allocating it to a register!";
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
		if (!allocatedRegs.containsKey(Optimizer.getIterID())) {
			out.add(new OpASM(toString(), OpCode.MOV, getRegister().name(),
					memoryNode.getFormattedLocationReference()));
		} else {
			// If the load node has instead been given a register, load from
			// that instead.
			out.add(new OpASM(toString(), OpCode.MOV, getRegister().name(),
					allocatedRegs.get(Optimizer.getIterID()).name()));
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

	@Override
	public void allocateRegister(Reg allocatedReg) {
		allocatedRegs.put(Optimizer.getIterID(), allocatedReg);
	}

	@Override
	public Reg getAllocatedRegister() {
		return allocatedRegs.get(Optimizer.getIterID());
	}

}
