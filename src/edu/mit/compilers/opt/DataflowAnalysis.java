package edu.mit.compilers.opt;

public interface DataflowAnalysis<S> {
	
	public S getAnalyzedState(Block block);
	
}
