package edu.mit.compilers.opt.cse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.memory.MidTempDeclNode;
import edu.mit.compilers.opt.State;

public class CSEGlobalState implements State<CSEGlobalState> {

	// Note: we use a List<GlobalExpr> here *in case* we want to store multiple
	// ways of representing a memory node
	// i.e. a = b+c, d = a-e could produce d = [a-e, b+c-e)]
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
		// Take common expressions, only if they're temp vars or equal non-temp
		// vars.
		Set<GlobalExpr> sharedSet = exprToRefMap.keySet();
		sharedSet.retainAll(s.exprToRefMap.keySet());
		Map<GlobalExpr, List<MidMemoryNode>> newExprToRefMap = new HashMap<GlobalExpr, List<MidMemoryNode>>();
		for (GlobalExpr e : sharedSet) {
			MidMemoryNode newMemNode = null;
			List<MidMemoryNode> thisMemoryNodes = exprToRefMap.get(e);
			List<MidMemoryNode> thatMemoryNodes = s.exprToRefMap.get(e);
			// Find just one shared reference for the given expression to keep.
			// Join identical memory nodes (not temp nodes).
			for (MidMemoryNode memNode : thisMemoryNodes) {
				if (thatMemoryNodes.contains(memNode)) {
					newMemNode = memNode;
					break;
				}
			}
			if (newMemNode == null) {
				// Get the first instances of temps.
				for (MidMemoryNode m1 : thisMemoryNodes) {
					if (m1 instanceof MidTempDeclNode) {
						for (MidMemoryNode m2 : thatMemoryNodes) {
							if (m2 instanceof MidTempDeclNode) {
								// Link the two temps.
								((MidTempDeclNode) m1)
										.linkTempDecl((MidTempDeclNode) m2);
								// Return just one of them.
								newMemNode = m1;
								break;
							}
						}
						break;
					}
				}
			}
			assert newMemNode != null;
			List<MidMemoryNode> newMemNodes = new ArrayList<MidMemoryNode>();
			newMemNodes.add(newMemNode);
			newExprToRefMap.put(e, newMemNodes);
		}
		return null;
	}

	public void genReference(MidMemoryNode node, GlobalExpr expr) {
		// Would potentially expand expr here.
		List<GlobalExpr> exprs = new ArrayList<GlobalExpr>();
		exprs.add(expr);
		refToExprMap.put(node, exprs); // assuming killref already got called?

		for (GlobalExpr e : exprs) {
			if (!exprToRefMap.containsKey(e))
				exprToRefMap.put(e, new ArrayList<MidMemoryNode>());
			exprToRefMap.get(e).add(node);

			for (MidMemoryNode m : expr.getMemoryNodes()) {
				if (!mentionMap.containsKey(m))
					mentionMap.put(m, new ArrayList<GlobalExpr>());
				mentionMap.get(m).add(e);
			}
		}

	}

	// TODO function calls need to killreferences to all field decls
	// TODO (this always gets called before gen reference, so just make them one
	// method) ?
	public void killReferences(MidMemoryNode node) {
		if (mentionMap.containsKey(node)) {
			// Remove stuff for each expr that is affected by the node.
			for (GlobalExpr e : mentionMap.get(node)) {
				// Remove reference to this expr for every mentioned variable
				for (MidMemoryNode m : e.getMemoryNodes()) {
					if (mentionMap.containsKey(m))
						mentionMap.get(m).remove(e);
				}

				// Remove reference to this expr for every variable it defines
				if (exprToRefMap.containsKey(e)) { // this should always be
													// true?
					for (MidMemoryNode m : exprToRefMap.get(e)) {
						if (refToExprMap.containsKey(m)) { // this should always
															// be true?
							refToExprMap.get(m).remove(e);
						}
					}
					exprToRefMap.remove(e);
				}
			}

		}
	}

	public Map<GlobalExpr, List<MidMemoryNode>> getExpressionsMap() {
		return exprToRefMap;
	}

	public List<MidMemoryNode> getReferences(GlobalExpr expr) {
		if (exprToRefMap.containsKey(expr)) {
			return exprToRefMap.get(expr);
		}
		return new ArrayList<MidMemoryNode>();
	}

}
