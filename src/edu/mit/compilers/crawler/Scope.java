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
		return localVars.contains(v) || parent != null && parent.hasVar(v);
	}

	public Scope getParent() {
		return this.parent;
	}
}