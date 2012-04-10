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
		// Get all the blocks
		List<Block> worklist = Block.getAllBlocks(nodeList);
		LogCenter.debug("[BLOCK]\n"
						+ Block.recursiveToString(worklist.get(0), new ArrayList<Block>(), 2));

		// Set all the outs to bottom
		for (Block block : worklist) {
			outHash.put(block, startState.getBottomState());
		}

		// Do the first node
		Block n0 = Block.makeBlock(nodeList.getHead());
		outHash.put(n0, this.transferFunction.apply(n0, startState));
		worklist.remove(n0);

		while (!worklist.isEmpty()) {
			Block currentBlock = worklist.remove(0);
			S in = getInState(currentBlock);
			S out = this.transferFunction.apply(currentBlock, in);
			if (out != outHash.get(out)) {
				outHash.put(currentBlock, out);
				// worklist.addAll(currentBlock.getSuccessors());
			}
			// TODO: return with less perfect result if it takes a really long
			// time?
		}
	}

	public S getInState(Block b) {
		S out = null;
		for (Block m : b.getPredecessors()) {
			if (out == null) {
				out = outHash.get(m);
			} else {
				out = out.join(outHash.get(m));
			}
		}
		return out;
	}

}
