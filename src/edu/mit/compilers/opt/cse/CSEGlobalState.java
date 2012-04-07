package edu.mit.compilers.opt.cse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.opt.State;

public class CSEGlobalState implements State<CSEGlobalState> {

	// Note: we use a List<GlobalExpr> here *in case* we want to store multiple
	// ways of representing a memory node
	// i.e. a = b+c, d = a-e could produce d = [a-e, b-(c+e)]
	Map<MidMemoryNode, List<GlobalExpr>> refToExprMap;
	Map<GlobalExpr, List<MidMemoryNode>> exprToRefMap;
	Map<MidMemoryNode, List<GlobalExpr>> mentionMap;

	public CSEGlobalState() {
		refToExprMap = new HashMap<MidMemoryNode, List<GlobalExpr>>();
		exprToRefMap = new HashMap<GlobalExpr, List<MidMemoryNode>>();
		mentionMap = new HashMap<MidMemoryNode, List<GlobalExpr>>();
	}

	@Override
	public CSEGlobalState getInitialState() {
		return new CSEGlobalState();
	}

	@Override
	public CSEGlobalState getBottomState() {
		return new CSEGlobalState();
	}

	@Override
	public CSEGlobalState join(CSEGlobalState s) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void genReference(MidMemoryNode node, GlobalExpr expr) {
		// Would potentially expand expr here.
	}
	
	public void killReferences(MidMemoryNode node) {
		
	}
	
	public List<MidMemoryNode> getReferences(GlobalExpr expr) {
		return exprToRefMap.get(expr);
	}

}
