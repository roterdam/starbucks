package edu.mit.compilers.opt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.nodes.MidMethodDeclNode;

public class BackwardsAnalyzer<S extends State<S>, T extends Transfer<S>> implements DataflowAnalysis<S> {

	protected S startState;
	private T transferFunction;
	private HashMap<Block, S> inStates;

	public BackwardsAnalyzer(S s, T t) {
		startState = s;
		transferFunction = t;
		initialize();
	}

	private void initialize() {
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
		initialize();
		// Get all the blocks
		List<Block> worklist = Block.getAllBlocks(nodeList);
		LogCenter
				.debug("RA", "BLOCKS:\n"
						+ Block.recursiveToString(worklist.get(0), new ArrayList<Block>(), 2));

		// Don't forget that since we're working backwards the "in state" is the
		// output of the block!
		Block exit = findTail(worklist.get(0), new ArrayList<Block>());
		for (Block b : worklist) {
			if (b != exit) {
				inStates.put(b, startState.getInitialState());
			}
		}
		inStates.put(exit, transferFunction.apply(exit, startState
				.getInitialState()));
		worklist.remove(exit);

		while (!worklist.isEmpty()) {
			Block currentBlock = worklist.remove(0);
			LogCenter.debug("RA", "");
			LogCenter.debug("RA", "######################");
			LogCenter.debug("RA", "######################");
			LogCenter.debug("RA", "REVERSE ANALYZER IS LOOKING AT "
					+ currentBlock);
			LogCenter.debug("RA", "WL: " + worklist);
			S out = getOutState(currentBlock);
			S in = transferFunction.apply(currentBlock, out);
			if (!in.equals(inStates.get(currentBlock))) {
				LogCenter.debug("DCE", "Putting instate for b" + currentBlock.getBlockNum());
				inStates.put(currentBlock, in);
				for (Block s : currentBlock.getPredecessors()) {
					if (!worklist.contains(s)) {
						worklist.add(s);
					}
				}
			}
			// TODO: return with less perfect result if it takes a really long
			// time?
		}
	}

	private S getOutState(Block b) {
		assert b != null;
		S out = null;
		LogCenter.debug("RA", String
				.format("Getting in-state of %s\nWith %s predecessors.", b, b
						.getSuccessors().size()));
		for (Block m : b.getSuccessors()) {
			assert inStates.get(m) != null : m;
			out = inStates.get(m).join(out);
		}
		return out;
	}

	private Block findTail(Block head, List<Block> visitedBlocks) {
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
		return null;
	}

	@Override
	public S getAnalyzedState(Block block) {
		return getOutState(block);
	}
	
	@Override
	public List<Block> getTODOBlocks() {
		List<Block> todoBlocks = new ArrayList<Block>();
		for (Block b : inStates.keySet()) {
			todoBlocks.add(b);
		}
		return todoBlocks;
	}

}
