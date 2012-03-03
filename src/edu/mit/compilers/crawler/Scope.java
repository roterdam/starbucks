package edu.mit.compilers.crawler;

import java.util.HashSet;

public class Scope {
	private HashSet<String> localVars;
	private Scope parent;

	public Scope() {
		this(null);
	}

	public Scope(Scope parent) {
		this.parent = parent;
	}

	public void addVar(String v) {
		localVars.add(v);
	}

	public boolean hasVar(String v) {
		if (this.localVars.contains(v)) {
			return true;
		}
		Scope upScope = this.getParent();
		while (upScope != null) {
			if (upScope.hasVar(v)) {
				return true;
			}
			else {
				upScope = upScope.getParent();
			}
		}
		return false;
	}

	public Scope getParent() {
		return this.parent;
	}
}