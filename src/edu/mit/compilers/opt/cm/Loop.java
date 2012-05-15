package edu.mit.compilers.opt.cm;

import java.util.HashSet;
import java.util.Set;

import edu.mit.compilers.opt.Block;

public class Loop implements Comparable<Loop> {
	
	Set<Block> blocks;
	int depth;
	
	public Loop(int depth) {
		this.blocks = new HashSet<Block>();
		this.depth = depth;
	}
	
	public void setDepth(int i) {
		this.depth = i;
	}
	
	public int getDepth() {
		return this.depth;
	}
	
	public void addBlock(Block b) {
		this.blocks.add(b);
	}
	
	public Set<Block> getBlocks() {
		return this.blocks;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Loop)) {
			return false;
		}
		Loop d = (Loop) o;
		return d.getDepth() == this.getDepth() &&
				this.blocks.equals(d.getBlocks());
	}

	@Override
	public int compareTo(Loop d) {
		int depth = this.getDepth();
		int other = d.getDepth();
		if (depth == other) { 
			return 0;
		} else if (depth > other) {
			return 1;
		} else {
			return -1;
		}
	}
	
	
	
}
