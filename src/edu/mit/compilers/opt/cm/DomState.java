package edu.mit.compilers.opt.cm;

import java.util.LinkedHashSet;
import java.util.Set;

import edu.mit.compilers.opt.Analyzer;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.State;

public class DomState implements State<DomState> {

	private Set<Block> domSet;

	public DomState() {
		domSet = new LinkedHashSet<Block>();
	}

	private DomState(Set<Block> otherSet) {
		domSet = otherSet;
	}

	@Override
	public DomState getInitialState(Block b) {
		DomState out = new DomState();
		out.addBlock(b);
		return out;
	}

	public DomState clone() {
		Set<Block> newSet = new LinkedHashSet<Block>(domSet);
		return new DomState(newSet);
	}

	@Override
	public DomState getBottomState() {
		DomState out = new DomState();
		for (Block b : Analyzer.getReferenceBlocks()) {
			out.addBlock(b);
		}
		return out;
	}

	@Override
	public DomState join(DomState s) {
		if (s == null) {
			return this.clone();
		}
		Set<Block> otherSet = new LinkedHashSet<Block>(s.getDomSet());
		otherSet.retainAll(domSet);
		return new DomState(otherSet);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof DomState)) {
			return false;
		}
		DomState oDomState = (DomState) o;
		return domSet.equals(oDomState.getDomSet());
	}

	private Set<Block> getDomSet() {
		return domSet;
	}

	@Override
	public int hashCode() {
		// Look at equals instead.
		return 0;
	}

	public void addBlock(Block b) {
		domSet.add(b);
	}

	@Override
	public String toString() {
		String out = "[";
		for (Block b : domSet) {
			out += b.getBlockNum() + " (" + b.getHead() + "), ";
		}
		out += "]";
		return out;
	}

}
