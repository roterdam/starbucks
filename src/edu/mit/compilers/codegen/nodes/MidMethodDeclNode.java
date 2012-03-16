package edu.mit.compilers.codegen.nodes;

import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.crawler.VarType;

public class MidMethodDeclNode extends MidNode {
	private String name;
	private MidNodeList nodeList;
	private VarType varType;

	public MidMethodDeclNode(String name, VarType varType, MidNodeList nodeList) {
		super();
		this.name = name;
		this.nodeList = nodeList;
		this.varType = varType;
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

	public VarType getMidVarType() {
		return varType;
	}

}
