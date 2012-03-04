package edu.mit.compilers.crawler;

import java.util.HashMap;
import java.util.Map;

public class Scope {

	private Map<String, VarDecl> localVars;
	private Map<String, MethodDecl> methods;
	private Scope parent;
	private BlockType blockType;
	private VarType returnType;

	public enum BlockType {
		FOR, CLASS, METHOD, IF, WHILE, ANON, ELSE;
	}

	public Scope(BlockType blockType) {
		this(null, blockType, VarType.VOID);
	}
	
	public Scope(Scope parent, BlockType blockType) {
		this(parent, blockType, parent.getReturnType());
	}

	public Scope(Scope parent, BlockType blockType, VarType returnType) {
		this.parent = parent;
		this.blockType = blockType;
		this.returnType = returnType;
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
	
	public VarType getReturnType() {
		return returnType;
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
		if (hasVar(v)){
			return localVars.get(v).getReturnType();
		} else if (parent != null){
			return parent.getType(v);
		}
		return VarType.UNDECLARED;
	}

	public Scope getParent() {
		return this.parent;
	}
}