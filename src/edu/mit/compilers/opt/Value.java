package edu.mit.compilers.opt;


public class Value {
	
	private static int globalCount = 0;
	private int count;

	public Value() {
		count = globalCount++;
	}
	
	public String toString() {
		return String.format("V%d", count);
	}
}
