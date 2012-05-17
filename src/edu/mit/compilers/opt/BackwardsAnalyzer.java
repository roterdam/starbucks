package edu.mit.compilers.opt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.nodes.MidMethodDeclNode;

public class BackwardsAnalyzer<S extends State<S>, T extends Transfer<S>>
		implements DataflowAnalysis<S> {

	protected S startState;
	private T transferFunction;
	private HashMap<Block, S> inStates;

	public BackwardsAnalyzer(S s, T t) {
		startState = s;
		transferFunction = t;
		inStates = new HashMap<Block, S>();
	}

	public void analyze(MidSymbolTable symbolTable) {
		Map<String, MidMethodDeclNode> methods = symbolTable.getMethods();
		for (String methodName : methods.keySet()) {
			LogCenter.debug("RA", "Analyzing " + methodName);
			analyzeMidNodeList(methods.get(methodName).getNodeList());
		}
	}

	private void analyzeMidNodeList(MidNodeList nodeList) {
		// Get all the blocks
		List<Block> worklist = Block.getAllBlocks(nodeList);

		// Don't forget that since we're working backwards the "in state" is the
		// output of the block!
		
		LogCenter.debug("RA", "analzying shit");
		
		Block exit = findTail(worklist.get(0), new ArrayList<Block>());
		assert exit != null : "Found null tail.";
		for (Block b : worklist) {
			if (b != exit) {
				inStates.put(b, startState.getBottomState());
			}
		}
		inStates.put(exit,
				transferFunction.apply(exit, startState.getInitialState(exit)));
		worklist.remove(exit);

		while (!worklist.isEmpty()) {
			Block currentBlock = worklist.remove(0);
			S out = getOutState(currentBlock);
			S in = transferFunction.apply(currentBlock, out);
			if (!in.equals(inStates.get(currentBlock))) {
				inStates.put(currentBlock, in);
				for (Block s : currentBlock.getPredecessors()) {
					if (!worklist.contains(s)) {
						worklist.add(s);
					}
				}
			}
			LogCenter.debug("RA", "Done looking at block.");
		}
		LogCenter.debug("RA", "Done with this analyze thing");
	}

	private S getOutState(Block b) {
		assert b != null;
		S out = null;
		for (Block m : b.getSuccessors()) {
			assert inStates.get(m) != null : "Block not found in inStates: "
					+ m;
			out = inStates.get(m).join(out);
		}
		return out;
	}

	private Block findTail(Block head, List<Block> visitedBlocks) {
		assert head != null;
		if (visitedBlocks.contains(head)) {
			return null;
		}
		visitedBlocks.add(head);
		List<Block> successors = head.getSuccessors();
		if (successors.size() == 0) {
			return head;
		}
		for (Block b : successors) {
			Block tail = findTail(b, new ArrayList<Block>(visitedBlocks));
			if (tail != null) {
				return tail;
			}
		}
		return head;
	}

	@Override
	public S getAnalyzedState(Block block) {
		return getOutState(block);
	}

	@Override
	public List<Block> getProcessedBlocks() {
		List<Block> todoBlocks = new ArrayList<Block>();
		for (Block b : inStates.keySet()) {
			todoBlocks.add(b);
		}
		return todoBlocks;
	}

}
