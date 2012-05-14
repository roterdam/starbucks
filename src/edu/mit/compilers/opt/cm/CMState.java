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

		// Do first block manually to avoid recursive case
		Loop l = new Loop(depth);
		l.addBlock(b);
		agenda.addAll(b.getSuccessors());
		visited.add(b);
		
		while (agenda.size() > 0) {
			for (Block d : agenda) {
				LogCenter.debug("CM", "Agenda " + d.getHead());
			}
			Block current = agenda.pop();
			LogCenter.debug("CM", "Current " + current.getHead());
			for (Block c : current.getSuccessors()) {
				LogCenter.debug("CM", "Successors " + c.getHead());
			}
			
			if (!visited.contains(current)) {
				visited.add(current);
				MidNode node = current.getHead();
				if (node instanceof MidLabelNode) {
					MidLabelNode label = (MidLabelNode) node;
					LogCenter.debug("CM", "Label " + label.getType().toString());
					switch (label.getType()) {
					case ELIHW:
					case ROF:
						l.addBlock(current);
						break;
					case WHILE:
					case FOR:
						LogCenter.debug("CM", "Found a nested loop, current depth is " + depth);
						processBlock(current, depth+1);
						break;
					default:
						LogCenter.debug("CM", label.toString());
						LogCenter.debug("CM", "Adding a block to the loop");
						LogCenter.debug("CM", "" + current.getHead());
						l.addBlock(current);
						for (Block c : current.getSuccessors()) {
							LogCenter.debug("CM", "Adding to the AGENDA " + c.getHead());
						}
						agenda.addAll(current.getSuccessors());
						break;
					}
				} else {
					LogCenter.debug("CM", "Adding a block to the loop");
					LogCenter.debug("CM", "" + current.getHead());
					l.addBlock(current);
					for (Block c : current.getSuccessors()) {
						LogCenter.debug("CM", "Adding to the AGENDA " + c.getHead());
					}
					agenda.addAll(current.getSuccessors());
				}
			}
		}
		LogCenter.debug("CM", "Loop is of depth " + depth);
		LogCenter.debug("CM", "Loop has num blocks: " + l.getBlocks().size());
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
