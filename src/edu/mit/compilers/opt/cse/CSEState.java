package edu.mit.compilers.opt.cse;

import edu.mit.compilers.opt.State;

public class CSEState implements State<CSEState> {

	@Override
	public CSEState getInitialState() {
		return new CSEState();
	}

	@Override
	public CSEState getBottomState() {
		return new CSEState();
	}

	@Override
	public CSEState join(CSEState s) {
		// TODO Auto-generated method stub
		return null;
	}

}
