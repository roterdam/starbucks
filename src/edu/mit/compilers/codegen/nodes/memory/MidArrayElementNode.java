package edu.mit.compilers.codegen.nodes.memory;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.MemoryManager;
import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;

public class MidArrayElementNode extends MidMemoryNode {
	MidFieldArrayDeclNode arrayNode;
	MidLoadNode loadNode;

	public MidArrayElementNode(MidFieldArrayDeclNode arrayNode,
			MidLoadNode loadNode) {
		super(arrayNode.getName() + "[" + loadNode + "]");
		this.arrayNode = arrayNode;
		this.loadNode = loadNode;
	}

	@Override
	public List<Reg> getRegisters(){
		List<Reg> regList = new ArrayList<Reg>();
		regList.add(loadNode.getRegister());
		return regList;
	}
	
	@Override
	public void setRawLocationReference(String rawLocationReference) {
		assert false : "Array elements do not get a location. The array does.";
	}
	
	@Override
	public String getFormattedLocationReference() {
		return String.format("[ %s + %d*%s]",
				arrayNode.getRawLocationReference(),
				MemoryManager.ADDRESS_SIZE, loadNode.getRegister().name());
	}


}