package edu.mit.compilers.opt.cmold;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
	int counter;
	
	public CMState() {
		this.nesting = new HashMap<Block, Loop>();
		this.defBlock = new HashMap<MidSaveNode, Block>();
		this.counter = 0;
	}
	
	public CMState(HashMap<Block, Loop> nesting, HashMap<MidSaveNode, Block> defBlock) {
		this.nesting = nesting;
		this.defBlock = defBlock;
		this.counter = 0;
	}
	
	@Override
	public CMState getInitialState(Block b) {
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
	
	public void processBlock(Block b) {
		if (nesting.get(b) != null) {
			return;
		}

		Set<Block> visited = new HashSet<Block>();
		ArrayList<Block> agenda = new ArrayList<Block>();

		Loop l = new Loop();
		if (b.getPredecessors().size() == 0) {
			l.setDepth(0);
			l.setNum(0);
		}
		l.addBlock(b);
		agenda.addAll(b.getSuccessors());
		visited.add(b);
		
		while (agenda.size() > 0) {
			Block current = agenda.remove(0);
			if (!visited.contains(current)) {
				visited.add(current);
				MidNode node = current.getHead();
				if (node instanceof MidLabelNode) {
					MidLabelNode label = (MidLabelNode) node;
					if (label.getType() == LabelType.WHILE || label.getType() == LabelType.FOR) {
						processBlock(current);
						Loop child = nesting.get(current);
						l.addChild(child);
						child.addParent(l);
					} else if (label.getType() != LabelType.ELIHW && label.getType() != LabelType.ROF) {
						agenda.addAll(current.getSuccessors());
					}
					l.addBlock(current);
				} else {
					l.addBlock(current);
					agenda.addAll(current.getSuccessors());
				}
			}
		}
		for (Block lb : l.getBlocks()) {
			nesting.put(lb, l);
		}
	}
	
	public void updateDepth() {
		for (Loop nested : nesting.values()) {
			if (nested.getDepth() == 0) {
				updateDepth(nested);
			}
		}
	}
	
	private void updateDepth(Loop l) {
		for (Loop child : l.getChildren()) {
			child.setDepth(l.getDepth() + 1);
			updateDepth(child);
		}
	}
	
	public void updateCounter() {
		for (Loop nested : nesting.values()) {
			if (nested.getDepth() == 0) {
				counter = 0;
				updateCounter(nested);
				break;
			}
		}
	}
	
	private void updateCounter(Loop l) {
		for (Loop child : l.getChildren()) {
			counter++;
			child.setNum(counter);
			updateCounter(child);
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
