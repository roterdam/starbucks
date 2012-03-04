package edu.mit.compilers.crawler;

import java.util.HashMap;
import java.util.Map;

public class Scope {

	private Map<String, VarType> localVars;
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
		localVars = new HashMap<String, VarType>();
	}

	public BlockType getBlockType() {
		return blockType;
	}

	public void addVar(String v, VarType t) {
		localVars.put(v, t);
	}

	public boolean hasVar(String v) {
		return this.localVars.containsKey(v);
	}

	public boolean seesVar(String v) {
		return hasVar(v) || parent != null && parent.seesVar(v);
	}

	public VarType getType(String v) {
		return hasVar(v) ? localVars.get(v) : parent.getType(v);
	}

	public Scope getParent() {
		return this.parent;
	}
}