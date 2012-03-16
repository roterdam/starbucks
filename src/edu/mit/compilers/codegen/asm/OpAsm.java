package edu.mit.compilers.codegen.asm;

import java.util.List;

public class OpAsm {
	
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
	private List<String> args;
	private String comment;
	
	public OpAsm(OpCode op, List<String> args, String comment) {
		this.op = op;
		this.args = args;
		this.comment = comment;
	}
	
	@Override
	public String toString(){
		return op.toString() + args.toString() + " // " + comment;
	}
	
}
