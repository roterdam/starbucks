package edu.mit.compilers.crawler;

import java.util.HashMap;
import java.util.Map;

public class Scope {

	private Map<String, VarDecl> localVars;
	private Map<String, MethodDecl> methods;
	private Scope parent;
	private BlockType blockType;

	public enum BlockType {
		FOR, CLASS, IF, WHILE, ANON, ELSE;
	}

	public Scope(BlockType blockType) {
		this(null, blockType);
	}

	public Scope(Scope parent, BlockType blockType) {
		this.parent = parent;
		this.blockType = blockType;
		localVars = new HashMap<String, VarDecl>();
		if (parent == null) {
			methods = new HashMap<String, MethodDecl>();
		} else {
			methods = parent.getMethods();
		}
	}

	public Map<String, MethodDecl> getMethods() {
		return methods;
	}

	public BlockType getBlockType() {
		return blockType;
	}

	public void addVar(String id, VarDecl var) {
		localVars.put(id, var);
	}

	public boolean hasVar(String v) {
		return this.localVars.containsKey(v);
	}

	public boolean seesVar(String v) {
		return hasVar(v) || parent != null && parent.seesVar(v);
	}

	public VarType getType(String v) {
		return hasVar(v) ? localVars.get(v).getReturnType() : parent.getType(v);
	}

	public Scope getParent() {
		return this.parent;
	}
}