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
	int depth;
	
	public CMState() {
		this.nesting = new HashMap<Block, Loop>();
		this.depth = 0;
	}
	
	public CMState(HashMap<Block, Loop> nesting, int depth) {
		this.nesting = nesting;
		this.depth = depth;
	}
	
	public int getDepth() {
		return depth;
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
		return new CMState(in, Math.min(depth, s.getDepth()));
	}
	
	public CMState clone() {
		return new CMState(HashMapUtils.deepClone(nesting), depth);
	}
	
	public Loop getLoop(Block b) {
		return nesting.get(b);
	}
	
	public HashMap<Block, Loop> getNesting() {
		return nesting;
	}

	public void processBlock(Block b, int depth) {
		if (nesting.get(b) != null) {
			return;
		}
		
		Set<Block> visited = new HashSet<Block>();
		Stack<Block> agenda = new Stack<Block>();
		Loop l = new Loop(depth);
		l.addBlock(b);
		agenda.addAll(b.getSuccessors());
		visited.add(b);
		while (agenda.size() > 0) {
			Block current = agenda.pop();
			if (!visited.contains(current)) {
				visited.add(current);
				for (MidNode node : current) {
					if (node instanceof MidLabelNode) {
						MidLabelNode label = (MidLabelNode) node;
						switch (label.getType()) {
						case ELIHW:
						case ROF:
							break;
						case WHILE:
						case FOR:
							LogCenter.debug("CM", "Found a nested loop, current depth is " + depth);
							processBlock(current, depth+1);
							break;
						default:
							LogCenter.debug("CM", "Adding a block to the loop");
							LogCenter.debug("CM", "" + current);
							l.addBlock(current);
							agenda.addAll(current.getSuccessors());
							break;
						}
					}
				}
			}
		}
		LogCenter.debug("CM", "Loop is of depth " + depth);
		LogCenter.debug("CM", "Loop has num blocks: " + l.getBlocks().size());
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
