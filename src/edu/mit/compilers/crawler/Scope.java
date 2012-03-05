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

	/**
	 * Checks for declaration of symbol in both variable and method tables.
	 */
	public boolean hasSymbol(String symbol) {
		return (hasVar(symbol) || getMethods().containsKey(symbol));
	}
	
	/**
	 * Only checks for var in local scope.
	 */
	public boolean hasLocalVar(String v) {
		return this.localVars.containsKey(v);
	}

	/**
	 * Checks for var in local scope as well as all parent scopes.
	 */
	public boolean hasVar(String v) {
		return hasLocalVar(v) || parent != null && parent.hasVar(v);
	}
	
	/**
	 * Checks for method, but takes into account shadowing by local vars.
	 */
	public boolean hasMethod(String method) {
		return !hasVar(method) && getMethods().containsKey(method);
	}

	public VarType getType(String v) {
		if (hasLocalVar(v)){
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