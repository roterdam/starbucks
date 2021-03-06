package edu.mit.compilers.opt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.nodes.MidMethodDeclNode;

public class Analyzer<S extends State<S>, T extends Transfer<S>> implements
		DataflowAnalysis<S> {

	protected S startState;
	private T transferFunction;
	private HashMap<Block, S> outHash;
	private static List<Block> referenceBlocks;

	public Analyzer(S s, T t) {
		startState = s;
		transferFunction = t;
		outHash = new HashMap<Block, S>();
	}
	
	public static List<Block> getReferenceBlocks() {
		return referenceBlocks;
	}

	public void analyze(MidSymbolTable symbolTable) {
		Map<String, MidMethodDeclNode> methods = symbolTable.getMethods();
		for (String methodName : methods.keySet()) {
			LogCenter.debug("DCE", "Analyzing " + methodName);
			analyzeMidNodeList(methods.get(methodName).getNodeList());
		}
	}

	private void analyzeMidNodeList(MidNodeList nodeList) {
		// Get all the blocks
		List<Block> worklist = Block.getAllBlocks(nodeList);
		referenceBlocks = new ArrayList<Block>(worklist);

		// Set all the outs to bottom
		for (Block block : worklist) {
			outHash.put(block, startState.getBottomState());
		}

		// Do the first node
		Block n0 = worklist.get(0);
		LogCenter.debug("OPT", "Process " + n0);
		outHash.put(n0, transferFunction.apply(n0, startState.getInitialState(n0)));
		worklist.remove(n0);

		while (!worklist.isEmpty()) {
			Block currentBlock = worklist.remove(0);
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

	private S getInState(Block b) {
		S out = null;
		for (Block m : b.getPredecessors()) {
			//LogCenter.debug("OPT", "Using state from " + m);
			out = outHash.get(m).join(out);
		}
		return out;
	}

	@Override
	public S getAnalyzedState(Block block) {
		return getInState(block);
	}

	@Override
	public List<Block> getProcessedBlocks() {
		return new ArrayList<Block>(outHash.keySet());
	}

}
