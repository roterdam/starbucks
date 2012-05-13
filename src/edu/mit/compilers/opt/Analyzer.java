package edu.mit.compilers.opt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.nodes.MidMethodDeclNode;
import edu.mit.compilers.opt.cse.CSETransfer;

public class Analyzer<S extends State<S>, T extends Transfer<S>> implements
		DataflowAnalysis<S> {

	protected S startState;
	private T transferFunction;
	private HashMap<Block, S> outHash;

	public Analyzer(S s, T t) {
		startState = s;
		transferFunction = t;
		outHash = new HashMap<Block, S>();
	}

	public void analyze(MidSymbolTable symbolTable) {
		Map<String, MidMethodDeclNode> methods = symbolTable.getMethods();
		for (String methodName : methods.keySet()) {
			LogCenter.debug("OPT", "Analyzing " + methodName);
			analyzeMidNodeList(methods.get(methodName).getNodeList());
		}
	}

	private void analyzeMidNodeList(MidNodeList nodeList) {
		// Get all the blocks
		List<Block> worklist = Block.getAllBlocks(nodeList);
		LogCenter
				.debug("OPT", "BLOCKS:\n"
						+ Block.recursiveToString(worklist.get(0), new ArrayList<Block>(), 2));

		// Set all the outs to bottom
		for (Block block : worklist) {
			outHash.put(block, startState.getBottomState());
		}

		// Do the first node
		Block n0 = worklist.get(0);
		LogCenter.debug("OPT", "Process " + n0);
		outHash.put(n0, transferFunction.apply(n0, startState.getInitialState()));
		worklist.remove(n0);

		while (!worklist.isEmpty()) {
			Block currentBlock = worklist.remove(0);
			LogCenter.debug("OPT", "");
			LogCenter.debug("OPT", "######################");
			LogCenter.debug("OPT", "######################");
			LogCenter.debug("OPT", "ANALYZER IS LOOKING AT " + currentBlock);
			LogCenter.debug("OPT", "WL: " + worklist);
			if (currentBlock.getBlockNum() == 37) {
				CSETransfer.shouldPrint = true;
			}
			S in = getInState(currentBlock);
			S out = transferFunction.apply(currentBlock, in);
			if (!out.equals(outHash.get(currentBlock))) {
				CSETransfer.print("OLD: " + outHash.get(currentBlock));
				CSETransfer.print("NEW: " + out);
				if (currentBlock.getBlockNum() == 37) {
					CSETransfer.shouldPrint = false;
				}
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

	private S getInState(Block b) {
		S out = null;
		CSETransfer.print("");
		CSETransfer.print("");
		CSETransfer.print("");
		CSETransfer.print("Getting in state for " + b);
		for (Block m : b.getPredecessors()) {
			LogCenter.debug("OPT", "Using state from " + m);
			out = outHash.get(m).join(out);
			CSETransfer.print("Update: " + out);
		}
		CSETransfer.print("");
		CSETransfer.print("");
		return out;
	}

	@Override
	public S getAnalyzedState(Block block) {
		return getInState(block);
	}

}
