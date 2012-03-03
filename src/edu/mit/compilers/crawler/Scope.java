package edu.mit.compilers.crawler;

import java.util.HashMap;
import java.util.Map;

public class Scope {

	private Map<String, VarType> localVars;
	private Scope parent;

	public Scope() {
		this(null);
	}

	public Scope(Scope parent) {
		this.parent = parent;
		localVars = new HashMap<String, VarType>();
	}

	public void addVar(String v, VarType t) {
		localVars.put(v, t);
	}

	public boolean hasVar(String v) {
		if (this.localVars.containsKey(v)) {
			return true;
		}
		Scope upScope = this.getParent();
		while (upScope != null) {
			if (upScope.hasVar(v)) {
				return true;
			} else {
				upScope = upScope.getParent();
			}
		}
		return false;
	}
	
	public VarType getType(String v) {
		return localVars.get(v);
	}
	
	public Scope getParent() {
		return this.parent;
	}
}