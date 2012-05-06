package edu.mit.compilers.codegen.nodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.asm.LabelASM;
import edu.mit.compilers.codegen.asm.OpASM;
import edu.mit.compilers.codegen.asm.OpCode;
import edu.mit.compilers.crawler.SemanticRules;
import edu.mit.compilers.crawler.VarType;
import edu.mit.compilers.opt.regalloc.RegisterAllocator;

public class MidMethodDeclNode extends MidNode {
	private String name;
	private String userDefinedName;

	private MidNodeList nodeList;
	private VarType varType;

	// Stores the number of lines needed for the local stack.
	private int localStackSize;

	public MidMethodDeclNode(String name, String userDefinedName,
			VarType varType) {
		super();
		this.name = name;
		this.userDefinedName = userDefinedName;
		this.varType = varType;
	}

	public String getUserDefinedName() {
		return userDefinedName;
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
		out.add(new OpASM(name, OpCode.ENTER, Integer.toString(localStackSize),
				"0"));

		List<ASM> mainList = nodeList.toASM();

		// Only save items if we're going to return (and not exit) at the end.
		if (mainList.get(mainList.size() - 1).isRet()) {
			// Save callee-saved registers. Traverse the tree to figure out
			// which ones we need to save - assume that any ones that we need to
			// save will be identified in a save node.
			LogCenter.debug("RA", "Saving callee-saved registers.");
			Set<Reg> needToSaveRegs = new HashSet<Reg>();
			for (MidNode node : nodeList) {
				if (node instanceof MidSaveNode) {
					MidSaveNode loadNode = (MidSaveNode) node;
					Reg usedReg = loadNode.getAllocatedRegister();
					if (usedReg == null) {
						continue;
					}
					for (Reg usableReg : RegisterAllocator.CALLEE_SAVED_REGISTERS) {
						if (usableReg == usedReg) {
							needToSaveRegs.add(usableReg);
							break;
						}
					}
				}
			}
			LogCenter.debug("RA", "Found " + needToSaveRegs.size()
					+ " registers to save: " + needToSaveRegs);
			// Convert to a list so that it's ordered consistently.
			List<Reg> orderedSaveRegs = new ArrayList<Reg>(needToSaveRegs);
			for (Reg reg : orderedSaveRegs) {
				out.add(new OpASM("Callee-saved", OpCode.PUSH, reg.name()));
			}

			out.addAll(mainList);

			// Caution, this modifies the original array.
			Collections.reverse(orderedSaveRegs);
			for (Reg reg : orderedSaveRegs) {
				// Insert it right before the last one.
				out.add(out.size() - 2, new OpASM("Callee-saved", OpCode.POP,
						reg.name()));
			}
		} else {
			out.addAll(mainList);
		}

		return out;
	}
}
