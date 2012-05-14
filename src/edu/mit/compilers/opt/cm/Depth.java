package edu.mit.compilers.opt.cm;

public class Depth {
	
	int depth;

	public Nesting() {
		depth = 0;
	}
	
	public void increase() {
		depth++;
	}
}
