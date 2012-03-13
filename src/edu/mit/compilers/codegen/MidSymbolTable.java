package edu.mit.compilers.codegen;

import java.util.HashMap;
import java.util.Map;

import edu.mit.compilers.codegen.nodes.MidMethodDeclNode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidVarDeclNode;

public class MidSymbolTable {

	private Map<String, MidVarDeclNode> localVars;
	private Map<String, MidMethodDeclNode> methods;
	private MidSymbolTable parent;
	private MidNode breakableNode;

	public MidSymbolTable() {
		this(null, null);
	}
	
	public MidSymbolTable(MidSymbolTable p) {
		this(p, null);
	}
	// Breakable.
	public MidSymbolTable(MidSymbolTable p, MidNode breakableNode) {
		this.parent = p;
		this.breakableNode = breakableNode;
		this.localVars = new HashMap<String, MidVarDeclNode>();
		this.methods = parent == null ? new HashMap<String, MidMethodDeclNode>()
				: parent.getMethods();
	}

	public Map<String, MidMethodDeclNode> getMethods() {
		return methods;
	}

	public MidNode getBreakableNode() {
		if (breakableNode != null){
			return breakableNode;
		} else if(parent != null){
			return parent.getBreakableNode();
		} else {
			assert false : "Semantic Checker, where you at bro?";
			return null;
		}
	}

	public void addVar(String id, MidVarDeclNode var) {
		localVars.put(id, var);
	}

	/**
	 * Checks for var in local scope as well as all parent scopes.
	 */
	public MidNode getVar(String v) {
		if (localVars.containsKey(v)) {
			return localVars.get(v);
		} else if (parent != null) {
			return this.parent.getVar(v);
		} else {
			assert false : "Variables should be declared. Semantic Checker, what up?";
			return null;
		}
	}

	public MidNode getMethod(String method) {
		assert getMethods().containsKey(method);
		return getMethods().get(method);
	}

	public void addMethod(String method, MidMethodDeclNode node) {
		getMethods().put(method, node);
	}
	
	public MidSymbolTable getParent() {
		return this.parent;
	}
}
