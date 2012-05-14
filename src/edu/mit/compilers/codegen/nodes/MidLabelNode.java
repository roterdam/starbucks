package edu.mit.compilers.codegen.nodes;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.MidLabelManager.LabelType;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.LabelASM;

public class MidLabelNode extends MidNode {
	
	private final LabelType type;
	private final String name;
	
	public MidLabelNode(LabelType type, String name) {
		this.type = type;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public LabelType getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return "<" + getNodeClass() + ":" + getName()+">";
	}
	
	@Override
	public List<ASM> toASM() {
		List<ASM> out = new ArrayList<ASM>();
		out.add(new LabelASM("", name));
		return out;
	}
}
