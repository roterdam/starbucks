package edu.mit.compilers.codegen.nodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.compilers.codegen.MemoryManager;
import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;
import edu.mit.compilers.codegen.nodes.memory.ArrayReferenceNode;
import edu.mit.compilers.codegen.nodes.memory.MidArrayElementNode;
import edu.mit.compilers.codegen.nodes.memory.MidConstantNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.memory.MidTempDeclNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidRegisterNode;
import edu.mit.compilers.codegen.nodes.regops.RegisterOpNode;
import edu.mit.compilers.opt.meta.Optimizer;
import edu.mit.compilers.opt.regalloc.nodes.Allocatable;

/**
 * ` Saves referenced register node or literal to memory.
 */
public class MidSaveNode extends MidNode implements RegisterOpNode,
		ArrayReferenceNode, Allocatable {

	private MidRegisterNode registerNode;
	private long decafIntValue;
	private boolean decafBooleanValue;
	private Map<Integer, Reg> allocatedRegs;

	private static enum MidSaveNodeType {
		REGISTER, INT, BOOLEAN;
	}

	private MidSaveNodeType saveType;
	private MidMemoryNode destination;

	private MidSaveNode(MidMemoryNode dest) {
		assert dest != null;
		this.destination = dest;
		allocatedRegs = new HashMap<Integer, Reg>();
	}

	public MidSaveNode(MidRegisterNode refNode, MidMemoryNode dest) {
		this(dest);
		this.registerNode = refNode;
		this.saveType = MidSaveNodeType.REGISTER;
		if (registerNode instanceof MidLoadNode) {
			((MidLoadNode) registerNode).recordRegisterOp(this);
		}
		// registerNode.record(this);
	}

	public static MidNodeList storeValueInMemory(long decafIntValue,
			MidTempDeclNode dest) {
		// TODO: Optimize by using two mov's instead of a load and mov.
		MidNodeList nodeList = new MidNodeList();
		MidLoadNode loadNode = new MidLoadNode(new MidConstantNode(decafIntValue));
		MidSaveNode saveNode = new MidSaveNode(loadNode, dest);
		dest.setConstantValue(decafIntValue);
		nodeList.add(loadNode);
		nodeList.add(saveNode);
		return nodeList;
	}

	public static MidNodeList storeValueInMemory(long decafIntValue,
			MidMemoryNode dest) {
		MidNodeList nodeList = new MidNodeList();
		MidTempDeclNode tempNode = new MidTempDeclNode();

		MidNodeList getValList = storeValueInMemory(decafIntValue, tempNode);
		MidLoadNode loadNode = new MidLoadNode(getValList.getMemoryNode());
		MidSaveNode saveNode = new MidSaveNode(loadNode, dest);

		nodeList.add(tempNode);
		nodeList.addAll(getValList);
		nodeList.add(loadNode);
		nodeList.add(saveNode);
		return nodeList;
	}

	public MidSaveNode(boolean decafBooleanValue, MidMemoryNode dest) {
		this(dest);
		this.decafBooleanValue = decafBooleanValue;
		this.saveType = MidSaveNodeType.BOOLEAN;
	}

	public MidRegisterNode getRegNode() {
		assert saveType == MidSaveNodeType.REGISTER : "Save type is "+saveType;
		return registerNode;
	}

	public boolean savesRegister() {
		return saveType == MidSaveNodeType.REGISTER;
	}

	@Override
	public boolean usesArrayRegister() {
		return destination instanceof MidArrayElementNode
				&& !((MidArrayElementNode) destination).isConstant();
	}

	@Override
	public MidArrayElementNode getMidArrayElementNode() {
		assert usesArrayRegister();
		return (MidArrayElementNode) destination;
	}

	@Override
	public Reg getArrayRegister() {
		assert usesArrayRegister();
		return ((MidArrayElementNode) destination).getLoadRegister();
	}

	public long getDecafIntValue() {
		assert saveType == MidSaveNodeType.INT;
		return decafIntValue;
	}

	public boolean getDecafBooleanValue() {
		assert saveType == MidSaveNodeType.BOOLEAN;
		return decafBooleanValue;
	}

	public MidMemoryNode getDestinationNode() {
		return this.destination;
	}

	public String toString() {
		String className = getClass().getName();
		int mid = className.lastIndexOf('.') + 1;
		String value = "wtf value is this";
		switch (saveType) {
		case INT:
			value = Long.toString(decafIntValue);
			break;
		case BOOLEAN:
			value = Boolean.toString(decafBooleanValue);
			break;
		case REGISTER:
			value = registerNode.getName();
		}
		String isArray = usesArrayRegister() ? "[A]" : "";
		return "<" + className.substring(mid) + ": " + value + " -> "
				+ getDestinationNode().getName() + " " + isArray + ">";
	}

	@Override
	public String toDotSyntax() {
		String out = super.toDotSyntax() + hashCode() + " -> "
				+ destination.hashCode() + " [style=dotted,color=green];\n";
		if (saveType == MidSaveNodeType.REGISTER) {
			out += registerNode.hashCode() + " -> " + hashCode()
					+ " [style=dotted,color=green];\n";
		}
		return out;
	}

	@Override
	public List<ASM> toASM() {

		List<ASM> out = new ArrayList<ASM>();

		String rightOperand;
		switch (saveType) {
		case REGISTER:
			rightOperand = registerNode.getRegister().name();
			break;
		case INT:
			rightOperand = Long.toString(decafIntValue);
			break;
		case BOOLEAN:
			rightOperand = ((decafBooleanValue) ? "1" : "0");
			break;
		default:
			rightOperand = null;
			assert false : "invalid saveType";
		}

		String comment = (isOptimization ? "[OPT] " : "") + toString();
		String destinationString;
		if (!allocatedRegs.containsKey(Optimizer.getIterID())) {
			destinationString = destination.getFormattedLocationReference();
		} else {
			destinationString = allocatedRegs.get(Optimizer.getIterID()).name();
		}
		out.add(new OpASM(comment, OpCode.MOV, destinationString, rightOperand));

		return out;
	}

	@Override
	public void updateLoadNode(MidLoadNode oldNode, MidLoadNode newNode) {
		registerNode = newNode;
	}

	@Override
	public List<Reg> getOperandRegisters() {
		List<Reg> out = new ArrayList<Reg>();
		if (registerNode != null) {
			assert registerNode.getRegister() != null : registerNode + " "
					+ registerNode.hashCode() + " -> " + MemoryManager.temp
					+ " " + MemoryManager.temp.hashCode();
			out.add(registerNode.getRegister());
		}
		return out;
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
