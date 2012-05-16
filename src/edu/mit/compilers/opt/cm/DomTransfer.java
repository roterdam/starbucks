package edu.mit.compilers.opt.cm;

import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.Transfer;

public class DomTransfer implements Transfer<DomState> {

	@Override
	public DomState apply(Block b, DomState s) {
		DomState out = s.clone();
		out.addBlock(b);
		return out;
	}

}
