package edu.mit.compilers.codegen.asm;

public class OpASM extends ASM {
	
	public enum OpCode {
		ADD,
		AND,
		CMOVE,
		CMOVGE,
		CMOVG,
		CMOVLE,
		CMOVL,
		CMOVNE,
		CMP,
		CALL,
		ENTER,
		IDIV,
		IMUL,
		JE,
		JG,
		JGE,
		JL,
		JLE,
		JMP,
		JNE,
		LEA,
		LEAVE,
		MOV,
		NEG,
		NOP,
		POP,
		PUSH,
		RET,
		SHL,
		SAR,
		SUB,
		XCHG,
		XOR;
		;
	}
	
	private OpCode op;
	private String[] args;
	private String comment;
	
	public OpASM(OpCode op, String[] args, String comment) {
		this.op = op;
		this.args = args;
		this.comment = comment;
	}
	
	@Override
	public String toString(){
		StringBuilder arguments = new StringBuilder();
		for (String a : this.args){
			arguments.append(a + " ");
		}
		if (comment != ""){
			return String.format("   %-10s %-10s ; %s\n", op.toString(), arguments.toString(), comment);
		} else {
			return String.format("   %-10s %-10s\n", op.toString(), arguments.toString());
		}
		
	}
	
}
