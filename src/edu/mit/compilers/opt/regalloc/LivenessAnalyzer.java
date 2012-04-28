package edu.mit.compilers.opt.regalloc;

import java.util.List;

import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.opt.Block;

/**
 * Processes a MidNodeList to produce a set of definitions and its corresponding
 * uses.
 * 
 * @author joshma
 */
public class LivenessAnalyzer {
	
	public static void analyze(MidNodeList midNodeList) {
		List<Block> blocks = Block.getAllBlocks(midNodeList);
	}

}
