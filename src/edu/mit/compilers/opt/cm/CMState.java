package edu.mit.compilers.opt.cm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.MidLabelManager.LabelType;
import edu.mit.compilers.codegen.nodes.MidLabelNode;
import edu.mit.compilers.codegen.nodes.MidNode;
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
	
	public Loop getLoop(Block b) {
		return nesting.get(b);
	}
	
	public HashMap<Block, Loop> getNesting() {
		return nesting;
	}

	public void processBlock(Block b, int depth) {
		
		// Already processed block, small hack to avoid
		// rewriting an analyzer function
		if (nesting.get(b) != null) {
			return;
		}

		Set<Block> visited = new HashSet<Block>();
		Stack<Block> agenda = new Stack<Block>();

		// Initial case
		Loop l = new Loop(depth);
		l.addBlock(b);
		agenda.addAll(b.getSuccessors());
		visited.add(b);
		
		while (agenda.size() > 0) {
			Block current = agenda.pop();
			if (!visited.contains(current)) {
				visited.add(current);
				MidNode node = current.getHead();
				if (node instanceof MidLabelNode) {
					MidLabelNode label = (MidLabelNode) node;
					if (label.getType() == LabelType.WHILE || label.getType() == LabelType.FOR) {
						processBlock(current, depth+1);
						for (Block c : nesting.get(current).getBlocks()) {
							l.addBlock(c);
						}
					} else if (label.getType() != LabelType.ELIHW && label.getType() != LabelType.ROF) {
						agenda.addAll(current.getSuccessors());
					}
				} else {
					agenda.addAll(current.getSuccessors());
				}
				l.addBlock(current);
			}
		}
		LogCenter.debug("CM", "Depth " + depth);
		LogCenter.debug("CM", "Num Blocks " + l.getBlocks().size());
		for (Block e : l.getBlocks()) {
			LogCenter.debug("CM", "Block " + e.getHead());
		}
		nesting.put(b, l);
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
