package edu.mit.compilers.opt;

import edu.mit.compilers.opt.cse.CSEState;
import edu.mit.compilers.opt.cse.CSETransfer;

public class Optimizer {

	/**
	 * Runs transfer function over mid level IR.
	 */
	public static void midLevelOptimize(Analyzer<CSEState, CSETransfer> analyzer) {
		System.out.println("Optimizing!");
	}

}
