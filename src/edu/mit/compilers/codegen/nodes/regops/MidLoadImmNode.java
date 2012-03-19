package edu.mit.compilers.codegen.nodes.regops;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;

public class MidLoadImmNode extends MidRegisterNode {
	private long value;
	
	public MidLoadImmNode(long decafIntValue) {
		super();
		this.value = decafIntValue;
	}

	public long getValue() {
		return value;
	}

	public String toString() {
		String className = getClass().getName();
		int mid = className.lastIndexOf('.') + 1;
		return "<" + className.substring(mid) + ": " + getName() + ","
				+ Long.toString(value) + ">";
	}

	@Override
	public String toDotSyntax() {
		return super.toDotSyntax() + Long.toString(value) + " -> "
				+ hashCode() + " [style=dotted,color=orange];\n";
	}

	public List<ASM> toASM() {
		List<ASM> out = new ArrayList<ASM>();
		out.add(new OpASM(toString(), OpCode.MOV, getRegister().name(),
				Long.toString(value)));
		return out;
	}
	
	@Override
	public List<Reg> getOperandRegisters() {
		// LoadImm doesn't use registers
		return new ArrayList<Reg>();
	}
}
