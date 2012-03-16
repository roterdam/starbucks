package edu.mit.compilers.codegen.nodes;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.AsmVisitor;
import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.LabelASM;
import edu.mit.compilers.codegen.asm.OpASM;
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

	public List<ASM> toASM() {

		List<ASM> out = new ArrayList<ASM>();

		out.add(new LabelASM(this.getName(), "ENTERING " + this.getName()));

		// TODO: GET THE NUMBER OF STACK VARS NEEDED BY SWEEPING THROUGH WITH
		// MEM-ALLOCATING VISITOR. When you have this method decl node
		// accurately provisioning local stack space, please don't forget to
		// have local and temp decls NOT allocate any more space.
		int stackVars = 0;
		out.add(new OpASM(name, OpASM.OpCode.ENTER, Integer.toString(stackVars)
				+ " * " + AsmVisitor.ADDRESS_SIZE_STRING, ""));

		out.addAll(nodeList.toASM());

		return out;
	}

}
