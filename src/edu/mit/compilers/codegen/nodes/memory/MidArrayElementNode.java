package edu.mit.compilers.codegen.nodes.memory;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.MemoryManager;
import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.ValuedMidNodeList;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;

/**
 * Design flaw. MidArrayElementNodes cannot be reused. If it is reused, it's
 * loadNoad gets reused, which causes memory allocation issues.
 * 
 * @author saif
 * 
 */
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

	/*
	@Override
	public ValuedMidNodeList generateReference(){
		MidNodeList nodeList = new MidNodeList();
		MidLoadNode loadNode = new MidLoadNode(this.loadNode.getMemoryNode());
		MidArrayElementNode elementNode = new MidArrayElementNode(arrayNode, loadNode);
		nodeList.add(loadNode);
		return new ValuedMidNodeList(nodeList, elementNode);
	}*/
	
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
		return String.format("[ %s + %d*%s ]",
				arrayNode.getRawLocationReference(),
				MemoryManager.ADDRESS_SIZE, memoryLocation);
	}

	public MidLoadNode getLoadNode() {
		return loadNode;
	}

	public Reg getLoadRegister() {
		return loadNode.getRegister();
	}

	public void setConstantNode(MidConstantNode constantNode) {
		LogCenter.debug("CPJ", "Setting array constant node: " + constantNode);
		this.constantNode = constantNode;
	}

	@Override
	public boolean isConstant() {
		return constantNode != null;
	}

}