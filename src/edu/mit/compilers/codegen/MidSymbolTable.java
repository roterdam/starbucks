package edu.mit.compilers.codegen;

import java.util.HashMap;
import java.util.Map;

import edu.mit.compilers.codegen.nodes.MidLabelNode;
import edu.mit.compilers.codegen.nodes.MidMethodDeclNode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;

public class MidSymbolTable {

	private Map<String, MidMemoryNode> localVars;
	private Map<String, MidMethodDeclNode> methods;
	private Map<String, MidMethodDeclNode> starbucksMethods;
	private MidSymbolTable parent;
	private MidLabelNode continueLabel;
	private MidLabelNode breakLabel;
	private MidMemoryNode currentMethodNameNode = null;

	public MidSymbolTable() {
		this(null, null, null);
	}

	public MidSymbolTable(MidSymbolTable p) {
		this(p, null, null);
	}

	public MidSymbolTable(MidSymbolTable p, MidMemoryNode methodNameNode) {
		this(p, null, null);
		this.currentMethodNameNode = methodNameNode;
	}

	public void setCurrentMethodNameNode(MidMemoryNode methodNameNode) {
		this.currentMethodNameNode = methodNameNode;
	}

	public MidMemoryNode getCurrentMethodNameNode() {
		if (currentMethodNameNode == null) {
			return parent.getCurrentMethodNameNode();
		}
		return currentMethodNameNode;
	}

	// Breakable.
	public MidSymbolTable(MidSymbolTable p, MidLabelNode continueLabel,
			MidLabelNode breakLabel) {
		this.parent = p;
		this.continueLabel = continueLabel;
		this.breakLabel = breakLabel;
		this.localVars = new HashMap<String, MidMemoryNode>();
		this.methods = (parent == null) ? new HashMap<String, MidMethodDeclNode>()
				: parent.getMethods();
		this.starbucksMethods = (parent == null) ? new HashMap<String, MidMethodDeclNode>()
				: parent.getStarbucksMethods();
	}

	public Map<String, MidMethodDeclNode> getMethods() {
		return methods;
	}

	public Map<String, MidMethodDeclNode> getStarbucksMethods() {
		return starbucksMethods;
	}

	public MidLabelNode getBreakLabel() {
		if (breakLabel != null) {
			return breakLabel;
		} else if (parent != null) {
			return parent.getBreakLabel();
		} else {
			assert false : "Semantic Checker, where you at bro?";
			return null;
		}
	}

	public MidLabelNode getContinueLabel() {
		if (continueLabel != null) {
			return continueLabel;
		} else if (parent != null) {
			return parent.getContinueLabel();
		} else {
			assert false : "Semantic Checker, where you at bro?";
			return null;
		}
	}

	public void addVar(String id, MidMemoryNode var) {
		localVars.put(id, var);
	}

	/**
	 * Checks for var in local scope as well as all parent scopes.
	 */
	public MidMemoryNode getVar(String v) {
		if (localVars.containsKey(v)) {
			assert localVars.get(v) != null : v;
			return localVars.get(v);
		} else if (parent != null) {
			assert parent.getVar(v) != null : v;
			return parent.getVar(v);
		} else {
			assert false : "Variables should be declared. Semantic Checker, what up?";
			return null;
		}
	}

	public Map<String, MidMemoryNode> getLocalVars() {
		return localVars;
	}

	public MidMethodDeclNode getMethod(String method) {
		assert getMethods().containsKey(method);
		return getMethods().get(method);
	}

	public void addMethod(String method, MidMethodDeclNode node) {
		getMethods().put(method, node);
	}

	public MidSymbolTable getParent() {
		return parent;
	}

	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		out.append("=== MidSymbolTable ===\n  Fields: ");
		String prefix = "";
		for (String field : localVars.keySet()) {
			out.append(prefix);
			out.append(field);
			prefix = ", ";
		}
		out.append("\n  Methods:\n");
		prefix = "  ";
		for (String method : methods.keySet()) {
			out.append(prefix);
			out.append(method);
			out.append(": ");
			out.append(methods.get(method).toString());
			prefix = "\n  ";
		}
		return out.toString();
	}

	/**
	 * Converts symbolTable to dot syntax
	 * 
	 * @param rootName
	 *            The name of the root of the graph.
	 * @param topLevel
	 *            Whether or not this is a top level symbol table. Used to
	 *            determine whether or not to show the methods table, useful for
	 *            child symbol tables. Also prints the digraph statement.
	 * @return
	 */
	public String toDotSyntax(boolean topLevel) {
		StringBuilder out = new StringBuilder();
		if (topLevel) {
			out.append("digraph MidLevelIR {\n");
		}

		String rootName = Integer.toString(hashCode());
		out.append(rootName + " [label=\"PROGRAM\"]");

		// Weird hack because rootName is an int, i.e. 12345, so 12345_fields is
		// treated by dot as two names. So we need to do 12345000 or 12345999
		// (as in the below case) to trick it into seeing one name.
		String fieldsNodeName = rootName + "000";
		out.append(rootName + " -> " + fieldsNodeName + ";\n");
		out.append(fieldsNodeName + " [label=\"fields\"]");
		for (String fieldName : localVars.keySet()) {
			MidNode fieldNode = localVars.get(fieldName);
			out.append(fieldNode.toDotSyntax());
			out.append(fieldsNodeName + " -> " + fieldNode.hashCode() + ";\n");
		}

		if (topLevel) {
			String methodsNodeName = rootName + "999";
			out.append(rootName + " -> " + methodsNodeName + ";\n");
			out.append(methodsNodeName + " [label=\"methods\"];\n");
			for (String method : methods.keySet()) {
				out.append(methodsNodeName + " -> " + method + ";\n");
				out.append(methods.get(method).toDotSyntax(method));
			}
		}

		if (topLevel) {
			out.append("}");
		}
		return out.toString();
	}

	/**
	 * Differentiate between "Starbucks" compiler-specific methods and
	 * user-defined methods.
	 * 
	 * @param name
	 * @param methodDecl
	 */
	public void addStarbucksMethod(String name, MidMethodDeclNode methodDecl) {
		starbucksMethods.put(name, methodDecl);
	}

	public MidMethodDeclNode getStarbucksMethod(String methodName) {
		return starbucksMethods.get(methodName);
	}
}
