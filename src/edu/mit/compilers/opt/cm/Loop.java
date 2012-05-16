package edu.mit.compilers.opt.cm;

import java.util.HashSet;
import java.util.Set;

import edu.mit.compilers.opt.Block;

public class Loop {
	
	Set<Block> blocks;
	Set<Loop> parents, children;
	int depth, num;
	
	public Loop() {
		this.blocks = new HashSet<Block>();
		this.parents = new HashSet<Loop>();
		this.children = new HashSet<Loop>();
		this.depth = -1;
	}
	
	public int getDepth() {
		return this.depth;
	}
	
	public void setDepth(int i) {
		this.depth = i;
	}
	
	public void addParent(Loop l) {
		this.parents.add(l);
	}
	
	public Set<Loop> getParents() {
		return this.parents;
	}
	
	public void addChild(Loop l) {
		this.children.add(l);
	}
	
	public Set<Loop> getChildren() {
		return this.children;
	}
	
	public void addBlock(Block b) {
		this.blocks.add(b);
	}
	
	public Set<Block> getBlocks() {
		return this.blocks;
	}
	
	public void setNum(int i) {
		this.num = i;
	}
	
	public int getNum()  {
		return this.num;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Loop)) {
			return false;
		}
		Loop d = (Loop) o;
		return d.getDepth() == this.getDepth() &&
				this.blocks.equals(d.getBlocks()) &&
				this.children.equals(d.getChildren()) &&
				this.parents.equals(d.getParents());
	}
	
	
	
}
