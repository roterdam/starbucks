package edu.mit.compilers.codegen.nodes.memory;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.MemoryManager;
import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.RegisterOpNode;

public class MidArrayElementNode extends MidMemoryNode implements RegisterOpNode {
	MidFieldArrayDeclNode arrayNode;
	MidLoadNode loadNode;

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
		return String.format("[ %s + %d*%s]",
				arrayNode.getRawLocationReference(),
				MemoryManager.ADDRESS_SIZE, loadNode.getRegister().name());
	}

	@Override
	public List<Reg> getOperandRegisters() {
		List<Reg> out = new ArrayList<Reg>();
		out.add(loadNode.getRegister());
		return out;
	}
}