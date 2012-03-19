package edu.mit.compilers.codegen.nodes;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.LabelASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;
import edu.mit.compilers.crawler.VarType;

public class MidMethodDeclNode extends MidNode {
	private String name;
	private MidNodeList nodeList;
	private VarType varType;
	
	// Stores the number of lines needed for the local stack.
	private int localStackSize;

	public MidMethodDeclNode(String name, VarType varType) {
		super();
		this.name = name;
		this.varType = varType;
	}

	public String getName() {
		return name;
	}
	
	public void setNodeList(MidNodeList nodeList) {
		this.nodeList = nodeList;
	}

	public MidNodeList getNodeList() {
		return nodeList;
	}

	public String toString() {
		return "METHOD: " + nodeList.toString();
	}
	
	public void setLocalStackSize(int size) {
		localStackSize = size;
	}
	
	public int getLocalStackSize() {
		return localStackSize;
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
	
	public List<ASM> toASM() {
		List<ASM> out = new ArrayList<ASM>();
		out.add(new LabelASM("ENTERING " + this.getName(), this.getName()));		
		out.add(new OpASM(name, OpCode.ENTER, Integer.toString(localStackSize), "0"));
		out.addAll(nodeList.toASM());
		
		return out;
	}

}
