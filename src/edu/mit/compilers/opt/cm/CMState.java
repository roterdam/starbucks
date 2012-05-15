package edu.mit.compilers.opt.cm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.MidLabelManager.LabelType;
import edu.mit.compilers.codegen.nodes.MidLabelNode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.HashMapUtils;
import edu.mit.compilers.opt.State;

public class CMState implements State<CMState> {
	
	HashMap<Block, Loop> nesting;
	HashMap<MidSaveNode, Block> defBlock;
	
	public CMState() {
		this.nesting = new HashMap<Block, Loop>();
		this.defBlock = new HashMap<MidSaveNode, Block>();
	}
	
	public CMState(HashMap<Block, Loop> nesting, HashMap<MidSaveNode, Block> defBlock) {
		this.nesting = nesting;
		this.defBlock = defBlock;
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
		HashMap<Block, Loop> inNesting = new HashMap<Block, Loop>();
		HashMap<MidSaveNode, Block> inDefBlock = new HashMap<MidSaveNode, Block>();
		inNesting.putAll(nesting);
		inNesting.putAll(s.getNesting());
		inDefBlock.putAll(defBlock);
		inDefBlock.putAll(s.getDefBlock());
		return new CMState(inNesting, inDefBlock);
	}
	
	public CMState clone() {
		return new CMState(HashMapUtils.deepClone(nesting), HashMapUtils.deepClone(defBlock));
	}
	
	public Loop getLoop(Block b) {
		return nesting.get(b);
	}
	
	public HashMap<Block, Loop> getNesting() {
		return nesting;
	}
	
	public HashMap<MidSaveNode, Block> getDefBlock() {
		return defBlock;
	}

	public void processBlock(Block b, int depth) {
		
		// Already processed block, small hack to avoid
		// rewriting an analyzer function
		if (nesting.get(b) != null) {
			return;
		}

		Set<Block> visited = new HashSet<Block>();
		Stack<Block> agenda = new Stack<Block>();

		// Do initial case separately to avoid infinite
		// recursion, fix this later
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
			nesting.put(e, l);
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CMState)) {
			return false;
		}
		CMState global = (CMState) o;
		return nesting.equals(global.getNesting()) && defBlock.equals(global.getDefBlock());
	}

	public void processDef(MidSaveNode node, Block b) {
		defBlock.put(node, b);
	}
	
}
