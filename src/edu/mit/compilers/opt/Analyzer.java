package edu.mit.compilers.opt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.nodes.MidMethodDeclNode;

public class Analyzer<S extends State<S>, T extends Transfer<S>> {

	private S startState;
	private T transferFunction;
	private HashMap<Block, S> outHash;

	public Analyzer(S s, T t) {
		startState = s;
		transferFunction = t;
		initialize();
	}

	private void initialize() {
		outHash = new HashMap<Block, S>();
	}

	public void analyze(MidSymbolTable symbolTable) {
		Map<String, MidMethodDeclNode> methods = symbolTable.getMethods();
		for (String methodName : methods.keySet()) {
			LogCenter.debug("[OPT] Analyzing " + methodName);
			analyzeMidNodeList(methods.get(methodName).getNodeList());
		}
	}

	private void analyzeMidNodeList(MidNodeList nodeList) {
		initialize();
		// Get all the blocks
		List<Block> worklist = Block.getAllBlocks(nodeList);
		LogCenter
				.debug("[OPT] BLOCKS:\n[OPT] "
						+ Block.recursiveToString(worklist.get(0), new ArrayList<Block>(), 2));

		// Set all the outs to bottom
		for (Block block : worklist) {
			outHash.put(block, startState.getBottomState());
		}

		// Do the first node
		Block n0 = worklist.get(0);
		LogCenter.debug("[OPT] Process " + n0);
		outHash.put(n0, transferFunction.apply(n0, startState.getInitialState()));
		worklist.remove(n0);

		while (!worklist.isEmpty()) {
			Block currentBlock = worklist.remove(0);
			LogCenter.debug("[OPT]");
			LogCenter.debug("[OPT] ######################");
			LogCenter.debug("[OPT] ######################");
			LogCenter.debug("[OPT] ANALYZER IS LOOKING AT " + currentBlock);
			LogCenter.debug("[OPT] WL: " + worklist);
			S in = getInState(currentBlock);
			S out = transferFunction.apply(currentBlock, in);
			if (!out.equals(outHash.get(currentBlock))) {
				outHash.put(currentBlock, out);
				for (Block s : currentBlock.getSuccessors()) {
					if (!worklist.contains(s)) {
						worklist.add(s);
					}
				}
			}
			// TODO: return with less perfect result if it takes a really long
			// time?
		}
	}
	
	public void analyzeBackwards(MidSymbolTable symbolTable) {
		Map<String, MidMethodDeclNode> methods = symbolTable.getMethods();
		for (String methodName : methods.keySet()) {
			LogCenter.debug("[OPT] Analyzing " + methodName);
			analyzeMidNodeListBackwards(methods.get(methodName).getNodeList());
		}
	}
	
	private void analyzeMidNodeListBackwards(MidNodeList nodeList) {
		initialize();
		// Get all the blocks
		List<Block> worklist = Block.getAllBlocks(nodeList);
		LogCenter
				.debug("[OPT] BLOCKS:\n[OPT] "
						+ Block.recursiveToString(worklist.get(0), new ArrayList<Block>(), 2));

		// Set all the outs to bottom
		for (Block block : worklist) {
			outHash.put(block, startState.getBottomState());
		}

		// Do the first node
		Block n0 = worklist.get(worklist.size()-1);
		LogCenter.debug("[OPT] Process " + n0);
		outHash.put(n0, transferFunction.apply(n0, startState.getInitialState()));
		worklist.remove(n0);

		while (!worklist.isEmpty()) {
			Block currentBlock = worklist.remove(worklist.size()-1);
			LogCenter.debug("[OPT]");
			LogCenter.debug("[OPT] ######################");
			LogCenter.debug("[OPT] ######################");
			LogCenter.debug("[OPT] ANALYZER IS LOOKING AT " + currentBlock);
			LogCenter.debug("[OPT] WL: " + worklist);
			S in = getInState(currentBlock);
			S out = transferFunction.apply(currentBlock, in);
			if (!out.equals(outHash.get(currentBlock))) {
				outHash.put(currentBlock, out);
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
	

	public S getInState(Block b) {
		S out = null;
		LogCenter.debug("[OPT] Getting in-state.");
		for (Block m : b.getPredecessors()) {
			LogCenter.debug("[OPT] Using state from " + m);
			out = outHash.get(m).join(out);
		}
		return out;
	}

}
