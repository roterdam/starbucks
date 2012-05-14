package edu.mit.compilers.opt.cm;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.MidLabelManager.LabelType;
import edu.mit.compilers.codegen.nodes.MidLabelNode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.Transfer;

public class CMTransfer implements Transfer<CMState> {

	@Override
	public CMState apply(Block b, CMState in) {
		CMState out = in.clone();

		for (MidNode node : b) {
			if (node instanceof MidLabelNode) {
				MidLabelNode label = (MidLabelNode) node;
				if (label.getType() == LabelType.FOR || label.getType() == LabelType.WHILE) {
					LogCenter.debug("CM", "Found a loop at zero depth");
					out.processBlock(b, 1);
				}
			}
		}
		
		return out;
	}

}
