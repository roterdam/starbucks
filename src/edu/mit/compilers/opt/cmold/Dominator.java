package edu.mit.compilers.opt.cmold;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.mit.compilers.opt.Block;

public class Dominator {
	
	List<Block> blocks;
	Map<Block, Set<Block>> dom;
	
	public Dominator(List<Block> blocks) {
		this.blocks = blocks;
		this.dom = new LinkedHashMap<Block, Set<Block>>();
		
		// dom(entry) = {entry}
		for (Block b : blocks) {
			if (b.getPredecessors().size() == 0) {
				Set<Block> entryDom = new LinkedHashSet<Block>();
				entryDom.add(b);
				this.dom.put(b, entryDom);
			}
		}
	}
	
	public Set<Block> dom(Block b) {
		// Entry case
		if (this.dom.get(b) != null) {
			return this.dom.get(b);
		}
		
		Set<Block> d = new LinkedHashSet<Block>();
		d.addAll(this.blocks);
		boolean changed = true;
		while (changed) {
			changed = false;
			for (Block n : new ArrayList<Block>(d)) {
				if (n.getPredecessors().isEmpty()) {
					continue;
				}
				Set<Block> old = new LinkedHashSet<Block>();
				old.addAll(d);
				d.clear();
				d.add(n);
				Set<Block> pDom = new LinkedHashSet<Block>();
				for (Block p : n.getPredecessors()) {
					if (pDom.isEmpty()) {
						pDom.addAll(dom(p));
					} else {
						pDom.retainAll(dom(p));
					}
				}
				if (!(d.equals(old))) {
					changed = true;
				}
			}
		}
		return d;
	}
	
	public HashMap<Block, HashSet<Block>> idom(List<Block> blocks) {
		Set<Block> d = new LinkedHashSet<Block>();
		/*Set<Block> dCopy = new LinkedHashSet<Block>();
		for (Block x : dCopy) {
			for (Block y : dCopy) {
				if (x.equals(y)) {
					continue;
				}
				Set<Block> sdomx = dom(x);
				if (sdomx.contains(y)) {
					
				}
			}
		}*/
		return null;
	}

}
