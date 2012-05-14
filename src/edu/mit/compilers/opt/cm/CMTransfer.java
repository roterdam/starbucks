package edu.mit.compilers.opt.cm;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.jumpops.MidJumpNode;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.Transfer;

public class CMTransfer implements Transfer<CMState> {

	@Override
	public CMState apply(Block b, CMState in) {
		CMState out = in.clone();
		for (MidNode node : b) {
			if (node instanceof MidJumpNode) {
				LogCenter.debug("CM", "Just encountered a level of nesting");
			}
		}
		return in;
	}

}
