package edu.mit.compilers.codegen.asm;

public class OpASM extends ASM {

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
	
	public OpCode getOpCode() {
		return op;
	}
	
	public String[] getArgs() {
		return args;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
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
			return String.format("   %-10s %-30s ; %s\n", op.name(), arguments
					.toString(), comment);
		}
		return String.format("   %-10s %-30s\n", op.name(), arguments
				.toString());
	}
	
	@Override
	public boolean isRet() {
		return op == OpCode.RET;
	}
	
}
