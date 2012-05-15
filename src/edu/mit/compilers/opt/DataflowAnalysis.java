package edu.mit.compilers.opt;

import java.util.List;

public interface DataflowAnalysis<S> {
	
	public S getAnalyzedState(Block block);

	public List<Block> getProcessedBlocks();
	
}
