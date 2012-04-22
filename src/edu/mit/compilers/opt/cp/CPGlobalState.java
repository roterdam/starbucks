package edu.mit.compilers.opt.cp;

import edu.mit.compilers.opt.State;

public class CPGlobalState implements State<CPGlobalState> {

	@Override
	public CPGlobalState getInitialState() {
		return new CPGlobalState();
	}

	@Override
	public CPGlobalState getBottomState() {
		return new CPGlobalState();
	}

	@Override
	public CPGlobalState join(CPGlobalState s) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public CPGlobalState clone() {
		// TODO this.
		return null;
	}

}
