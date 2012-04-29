package edu.mit.compilers.opt;

public interface Transfer<S> {

	public S apply(Block b, S s);
	
	public void cleanUp(Block b, S s);
}
