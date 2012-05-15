package edu.mit.compilers.opt.cm;

import edu.mit.compilers.codegen.MidLabelManager.LabelType;
import edu.mit.compilers.codegen.nodes.MidLabelNode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
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
					out.processBlock(b, 1);
				}
			}
			else if (node instanceof MidSaveNode) {
				MidSaveNode save = (MidSaveNode) node;
				out.processDef(save, b);
			}
		}
		
		return out;
	}

}
