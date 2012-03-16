package edu.mit.compilers.codegen.nodes;

import edu.mit.compilers.codegen.MidNodeList;

public class MidMethodDeclNode extends MidNode {
	private String name;
	private MidNodeList nodeList;

	public MidMethodDeclNode(String name, MidNodeList nodeList) {
		super();
		this.name = name;
		this.nodeList = nodeList;
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
		return out.toString();
	}
	
}
