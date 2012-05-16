package edu.mit.compilers.opt.cmold;

import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.Transfer;

public class CMTransfer implements Transfer<CMState> {

	@Override
	public CMState apply(Block b, CMState in) {
		CMState out = in.clone();
		
		out.processBlock(b);
		
		// hackish to call these here, lots of repeated work
		// but shouldn't make a difference
		out.updateDepth();
		out.updateCounter();
		
		for (MidNode node : b) {
			if (node instanceof MidSaveNode) {
				MidSaveNode save = (MidSaveNode) node;
				out.processDef(save, b);
			}
		}
		
		return out;
	}

}
