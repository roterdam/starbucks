package edu.mit.compilers.codegen.nodes.memory;

import edu.mit.compilers.codegen.MemoryManager;
import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;

public class MidArrayElementNode extends MidMemoryNode {
	MidFieldArrayDeclNode arrayNode;
	MidLoadNode loadNode;
	private MidConstantNode constantNode;

	public MidArrayElementNode(MidFieldArrayDeclNode arrayNode,
			MidLoadNode loadNode) {
		super(arrayNode.getName() + "[" + loadNode + "]");
		this.arrayNode = arrayNode;
		this.loadNode = loadNode;
	}

	@Override
	public void setRawLocationReference(String rawLocationReference) {
		assert false : "Array elements do not get a location. The array does.";
	}

	@Override
	public String getFormattedLocationReference() {
		String memoryLocation;
		if (constantNode == null) {
			memoryLocation = loadNode.getRegister().name();
		} else {
			memoryLocation = constantNode.getFormattedLocationReference();
		}
		return String
				.format("[ %s + %d*%s ]", arrayNode.getRawLocationReference(), MemoryManager.ADDRESS_SIZE, memoryLocation);
	}

	public MidLoadNode getLoadNode() {
		return loadNode;
	}

	public Reg getLoadRegister() {
		return loadNode.getRegister();
	}

	public void setConstantNode(MidConstantNode constantNode) {
		this.constantNode = constantNode;
	}

}