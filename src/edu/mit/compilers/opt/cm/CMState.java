package edu.mit.compilers.opt.cm;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.HashMapUtils;
import edu.mit.compilers.opt.State;

public class CMState implements State<CMState> {
	
	HashMap<Block, Loop> nesting;
	
	public CMState() {
		this.nesting = new HashMap<Block, Loop>();
	}
	
	public CMState(HashMap<Block, Loop> nesting) {
		this.nesting = nesting;
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
		if (s == null) {
			return this.clone();
		}
		HashMap<Block, Loop> in = new HashMap<Block, Loop>();
		in.putAll(nesting);
		in.putAll(s.getNesting());
		return new CMState(in);
	}
	
	public CMState clone() {
		return new CMState(HashMapUtils.deepClone(nesting));
	}
	
	public int getMinDepth(Block b) {
		// switch to comparator when not lazy
		int minDepth = 0;
		for (Block p : b.getSuccessors()) {
			if (nesting.get(p) != null) {
				minDepth = Math.min(minDepth, nesting.get(p).getDepth());
			}
		}
		return minDepth;
	}
	
	public Loop getDepth(Block b) {
		return nesting.get(b);
	}
	
	public HashMap<Block, Loop> getNesting() {
		return nesting;
	}

	public void processBlock(Block b, Loop d) {
		Loop current = nesting.get(b);
		if (current == null || current.compareTo(d) == -1) {
			nesting.put(b, d);
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CMState)) {
			return false;
		}
		CMState global = (CMState) o;
		return nesting.equals(global.getNesting());
	}
	
}
