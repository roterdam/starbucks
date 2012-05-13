package edu.mit.compilers.opt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.nodes.MidMethodDeclNode;

public abstract class Transformer<S> {

	public void analyze(DataflowAnalysis<S> analysis, MidSymbolTable symbolTable) {
		Map<String, MidMethodDeclNode> methods = symbolTable.getMethods();
		for (String methodName : methods.keySet()) {
			LogCenter.debug("OPT", "Analyzing " + methodName);
			List<Block> blocks = Block.getAllBlocks(methods.get(methodName)
					.getNodeList());
			LogCenter
					.debug("OPT", "Blocks: "
							+ Block.recursiveToString(blocks.get(0), new ArrayList<Block>(), 0));
			for (Block b : blocks) {
				transform(b, analysis.getAnalyzedState(b));
			}
		}
	}

	protected abstract void transform(Block block, S state);

}
