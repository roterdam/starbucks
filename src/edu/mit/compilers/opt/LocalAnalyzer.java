package edu.mit.compilers.opt;

import java.util.List;
import java.util.Map;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.nodes.MidMethodDeclNode;

public abstract class LocalAnalyzer {

	public void analyze(MidSymbolTable symbolTable) {
		Map<String, MidMethodDeclNode> methods = symbolTable.getMethods();
		for (String methodName : methods.keySet()) {
			LogCenter.debug("OPT", "Analyzing " + methodName);
			List<Block> blocks = Block.getAllBlocks(methods.get(methodName)
					.getNodeList());
			for (Block b : blocks) {
				transform(b);
			}
		}
	}

	protected abstract void transform(Block b);

}
