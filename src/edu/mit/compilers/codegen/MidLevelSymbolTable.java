package edu.mit.compilers.codegen;

import java.util.HashMap;
import java.util.Map;

import edu.mit.compilers.crawler.Scope.BlockType;

public class MidLevelSymbolTable {

	private Map<String, MidLevelNode> localVars;
	private Map<String, MidLevelNode> methods;
	private MidLevelSymbolTable parent;
	private BlockType blockType;
	private MidLevelNode node;

	public MidLevelSymbolTable(MidLevelNode node, BlockType type) {
		this(null, node, type);
	}

	public MidLevelSymbolTable(MidLevelSymbolTable p, MidLevelNode node,
			BlockType type) {
		this.parent = p;
		this.blockType = type;
		this.localVars = new HashMap<String, MidLevelNode>();
		this.methods = parent == null ? new HashMap<String, MidLevelNode>()
				: parent.getMethods();
	}

	public Map<String, MidLevelNode> getMethods() {
		return methods;
	}

	public MidLevelNode getFirstParentByType(BlockType type) {
		if (blockType == type) {
			return node;
		} else if (parent != null) {
			return parent.getFirstParentByType(type);
		} else {
			assert false : "Semantic Checker, where you at bro?";
			return null;
		}
	}

	public void addVar(String id, MidLevelNode var) {
		localVars.put(id, var);
	}

	/**
	 * Checks for var in local scope as well as all parent scopes.
	 */
	public MidLevelNode getVar(String v) {
		if (localVars.containsKey(v)) {
			return this.localVars.get(v);
		} else if (parent != null) {
			return this.parent.getVar(v);
		} else {
			assert false : "Variables should be declared. Semantic Checker, what up?";
			return null;
		}
	}

	public MidLevelNode getMethod(String method) {
		assert getMethods().containsKey(method);
		return getMethods().get(method);
	}

	public void addMethod(String method, MidLevelNode node) {
		getMethods().put(method, node);
	}
	
	public MidLevelSymbolTable getParent() {
		return this.parent;
	}
}
