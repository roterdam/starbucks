package edu.mit.compilers.opt;

import java.util.List;
import java.util.Map;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.nodes.MidMethodDeclNode;

public abstract class Transformer<S> {

	public void analyze(DataflowAnalysis<S> analysis, MidSymbolTable symbolTable) {
		Map<String, MidMethodDeclNode> methods = symbolTable.getMethods();
		for (String methodName : methods.keySet()) {
			List<Block> blocks = analysis.getProcessedBlocks();
			LogCenter.debug("DCE", "Analyzing " + methodName + " ("
					+ blocks.size() + " blocks)");
			for (Block b : blocks) {
				transform(b, analysis.getAnalyzedState(b));
			}
		}
	}

	protected abstract void transform(Block block, S state);

}
