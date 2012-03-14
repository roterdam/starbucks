package edu.mit.compilers.codegen.nodes;

import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.MidSymbolTable;

public class MidMethodDeclNode extends MidNode {
	private String name;
	private MidNodeList nodeList;
	private MidSymbolTable methodSymbolTable;

	public MidMethodDeclNode(String name, MidNodeList nodeList, MidSymbolTable methodSymbolTable) {
		super();
		this.name = name;
		this.nodeList = nodeList;
		// Link each method to its symbol table just for convenience.
		this.methodSymbolTable = methodSymbolTable;
	}

	public String getName() {
		return name;
	}

	public MidNodeList getNodeList() {
		return nodeList;
	}
	
	public String toString() {
		return "METHOD: " + nodeList.toString();
	}
	
	/**
	 * Only returns the relevant part of the graph, not the entire dot file.
	 */
	public String toDotSyntax(String rootName) {
		StringBuilder out = new StringBuilder();
		out.append(rootName + " [shape=rectangle];\n");
		out.append(nodeList.toDotSyntax(rootName));
		out.append(methodSymbolTable.toDotSyntax(rootName, false));
		return out.toString();
	}
	
}
