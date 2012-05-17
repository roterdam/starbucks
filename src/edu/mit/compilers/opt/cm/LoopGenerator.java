package edu.mit.compilers.opt.cm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.opt.Block;

/**
 * Response for: - DFS starting from each entry block. - Maps each MidNode to
 * its parent block. - Creates a map from blocks to Loops (defined by loop end
 * and start)
 */
public class LoopGenerator {

	private final DominanceRecord record;
	private final Map<MidNode, Block> nodeToBlockMap;
	private final Map<Block, Set<Loop>> blockToLoopMap;
	private final Set<Loop> loops;
	private final Map<Loop, Set<Block>> loopToBlockMap;

	public LoopGenerator(DominanceRecord record) {
		this.record = record;
		nodeToBlockMap = new HashMap<MidNode, Block>();
		blockToLoopMap = new HashMap<Block, Set<Loop>>();
		loopToBlockMap = new HashMap<Loop, Set<Block>>();
		loops = new LinkedHashSet<Loop>();
	}

	public void run() {
		for (Block b : record.getBlocks()) {
			if (isEntry(b)) {
				process(b);
			}
		}
		for (Loop loop : loops) {
			populateLoopMap(loop);
		}
		// Generate reverse index.
		for (Entry<Block, Set<Loop>> entry : blockToLoopMap.entrySet()) {
			Block block = entry.getKey();
			for (Loop loop : entry.getValue()) {
				Set<Block> blockSet = loopToBlockMap.get(loop);
				if (blockSet == null) {
					blockSet = new LinkedHashSet<Block>();
					loopToBlockMap.put(loop, blockSet);
				}
				blockSet.add(block);
			}
		}
	}

	/**
	 * Runs backwards DFS from end of loop.
	 * 
	 * @param loop
	 */
	private void populateLoopMap(Loop loop) {
		List<Block> visited = new ArrayList<Block>();
		Set<Loop> loopStartSet = new LinkedHashSet<Loop>();
		loopStartSet.add(loop);
		blockToLoopMap.put(loop.getStart(), loopStartSet);
		visited.add(loop.getStart());

		Stack<Block> stack = new Stack<Block>();
		stack.push(loop.getEnd());
		while (!stack.isEmpty()) {
			Block next = stack.pop();
			visited.add(next);

			Set<Loop> loopNextSet = blockToLoopMap.get(next);
			if (loopNextSet == null) {
				loopNextSet = new LinkedHashSet<Loop>();
				blockToLoopMap.put(next, loopNextSet);
			}
			loopNextSet.add(loop);

			for (Block pred : next.getPredecessors()) {
				if (!visited.contains(pred)) {
					stack.push(pred);
				}
			}
		}
	}

	private Set<Block> partiallyVisited = new LinkedHashSet<Block>();
	private Set<Block> fullyVisited = new LinkedHashSet<Block>();

	/**
	 * Runs DFS through b.
	 */
	private void process(Block next) {
		registerNodesWithBlock(next);
		partiallyVisited.add(next);

		for (Block succ : next.getSuccessors()) {
			if (partiallyVisited.contains(succ)) {
				if (!fullyVisited.contains(succ)) {
					// Discovered a loop.
					LogCenter.debug("CM", "Found a new loop from "
							+ succ.getHead() + " to " + next.getHead());
					Loop loop = new Loop(next, succ);
					loops.add(loop);
				}
			} else {
				process(succ);
			}
		}
		fullyVisited.add(next);
	}

	private void registerNodesWithBlock(Block first) {
		for (MidNode node : first) {
			nodeToBlockMap.put(node, first);
		}
	}

	private boolean isEntry(Block b) {
		return (b.getPredecessors().isEmpty());
	}

	public Set<Block> getAllBlocks() {
		return blockToLoopMap.keySet();
	}

	public Set<Loop> getLoops(Block block) {
		Set<Loop> loops = blockToLoopMap.get(block);
		if (loops == null) {
			loops = new HashSet<Loop>();
		}
		return loops;
	}

	public DominanceRecord getRecord() {
		return record;
	}

	public Block getBlock(MidNode midNode) {
		return nodeToBlockMap.get(midNode);
	}

}
