package edu.mit.compilers.opt;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.opt.cse.CSEGlobalState;
import edu.mit.compilers.opt.cse.CSETransfer;

public class Optimizer {

	/**
	 * Runs transfer function over mid level IR.
	 */
	public static void midLevelOptimize(Analyzer<CSEGlobalState, CSETransfer> analyzer) {
		LogCenter.debug("OPT","Optimizing!");
	}

}
