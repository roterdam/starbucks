package edu.mit.compilers.codegen.asm;

public class OpASM extends ASM {

	public enum OpCode {
		ADD, AND, CMOVE, CMOVGE, CMOVG, CMOVLE, CMOVL, CMOVNE, CMP, CALL, ENTER, IDIV, IMUL, JE, JG, JGE, JL, JLE, JMP, JNE, LEA, LEAVE, MOV, NEG, NOP, POP, PUSH, RET, SHL, SAR, SUB, XCHG, XOR, GLOBAL;
		;
	}

	private String comment;
	private OpCode op;
	private String[] args;

	public OpASM(String comment, OpCode op, String... args) {
		this.comment = comment;
		this.op = op;
		this.args = args;
	}
	
	public OpASM(OpCode op, String... args) {
		this.comment = "";
		this.op = op;
		this.args = args;
	}

	@Override
	public String toString() {
		StringBuilder arguments = new StringBuilder();
		String prefix = "";
		for (String a : this.args) {
			arguments.append(prefix + a);
			prefix = ", ";
		}
		if (comment != "") {
			return String.format("   %-10s %-10s ; %s\n", op.name(), arguments
					.toString(), comment);
		} else {
			return String.format("   %-10s %-10s\n", op.name(), arguments
					.toString());
		}

	}

}
