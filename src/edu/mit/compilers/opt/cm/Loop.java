package edu.mit.compilers.opt.cm;

import java.util.List;

import edu.mit.compilers.codegen.nodes.FillerMidNode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.opt.Block;

public class Loop {

	private Block preheader;
	private final Block end;
	private final Block start;

	public Loop(Block end, Block start) {
		this.end = end;
		this.start = start;
	}

	public Block getStart() {
		return start;
	}
	
	public Block getEnd() {
		return end;
	}
	
	public Block getPreheaderBlock() {
		if (this.preheader == null) {
			MidNode fillerNode = new FillerMidNode();
			preheader = new Block(fillerNode, Block.blockNumCounter++);
			preheader.setTail(fillerNode);
			
			MidNode startHead = start.getHead();
			MidNode oldPrev = startHead.getPrevNode();
			oldPrev.setNextNode(fillerNode);
			fillerNode.setNextNode(startHead);
			
			for (Block pred : start.getPredecessors()) {
				List<Block> successors = pred.getSuccessors();
				successors.remove(start);
				successors.add(preheader);
			}
			
			start.setPredecessor(preheader);
		}
		return preheader;
	}

}
