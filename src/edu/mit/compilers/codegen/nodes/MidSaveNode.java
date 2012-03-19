package edu.mit.compilers.codegen.nodes;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;
import edu.mit.compilers.codegen.nodes.memory.MidArrayElementNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadImmNode;
import edu.mit.compilers.codegen.nodes.regops.MidRegisterNode;

/**`
 * Saves referenced register node or literal to memory (the stack?).
 */
public class MidSaveNode extends MidNode {

	private MidRegisterNode registerNode;
	private long decafIntValue;
	private boolean decafBooleanValue;

	private static enum MidSaveNodeType {
		REGISTER, INT, BOOLEAN;
	}

	private MidSaveNodeType saveType;
	private MidMemoryNode destination;

	private MidSaveNode(MidMemoryNode dest) {
		assert dest != null;
		this.destination = dest;
	}

	public MidSaveNode(MidRegisterNode refNode, MidMemoryNode dest) {
		this(dest);
		this.registerNode = refNode;
		this.saveType = MidSaveNodeType.REGISTER;
	}
	
	public static MidNodeList storeValueInMemory(long decafIntValue, MidMemoryNode dest){
		//TODO: Optimize by using two mov's instead of a load and mov.
		MidNodeList nodeList = new MidNodeList();
		MidLoadImmNode loadNode = new MidLoadImmNode(decafIntValue);
		MidSaveNode saveNode = new MidSaveNode(loadNode, dest);
		nodeList.add(loadNode);
		nodeList.add(saveNode);
		return nodeList;
		
	}
	/*
	public MidSaveNode(long decafIntValue, MidMemoryNode dest) {
		this(dest);
		this.decafIntValue = decafIntValue;
		this.saveType = MidSaveNodeType.INT;
	}*/

	public MidSaveNode(boolean decafBooleanValue, MidMemoryNode dest) {
		this(dest);
		this.decafBooleanValue = decafBooleanValue;
		this.saveType = MidSaveNodeType.BOOLEAN;
	}

	public MidRegisterNode getRefNode() {
		assert saveType == MidSaveNodeType.REGISTER;
		return registerNode;
	}
	
	public boolean savesRegister() {
		return saveType == MidSaveNodeType.REGISTER;
	}
	
	public boolean savesToArray() {
		return destination instanceof MidArrayElementNode;
	}
	public Reg getArrayRegister(){
		assert savesToArray();
		return ((MidArrayElementNode)destination).getRegisters().get(0);
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
		return "<" + className.substring(mid) + ": " + value + ">";
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

		out.add(new OpASM(toString(), OpCode.MOV, destination
				.getFormattedLocationReference(), rightOperand));

		return out;
	}
}
