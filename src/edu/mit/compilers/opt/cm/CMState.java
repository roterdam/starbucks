package edu.mit.compilers.opt.cm;

import java.util.HashMap;

import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.State;

public class CMState implements State<CMState> {
	
	HashMap<Block, Depth> nesting;
	
	public CMState() {
		this.nesting = new HashMap<Block, Depth>();
	}

	@Override
	public CMState getInitialState() {
		return new CMState();
	}

	@Override
	public CMState getBottomState() {
		return new CMState();
	}

	@Override
	public CMState join(CMState s) {
		// Should be equal whenever you join (?)
		return s;
	}
	
	public CMState clone() {
		return new CMState();
	}
	
}
