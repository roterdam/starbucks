package edu.mit.compilers.opt.cse;

import java.util.Stack;

import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.regops.MidBinaryRegNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidRegisterNode;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.Transfer;

public class CSETransfer implements Transfer<CSEState> {
	
	public CSETransfer() {
	}	
	

	@Override
	public CSEState apply(Block b, CSEState s) {
		Stack<MidNode> nodes = new Stack();
		nodes.push(b.getHead());
		while (nodes.size() > 0) {
			MidNode node = nodes.pop();
			// is a node of the form a = x op y
			// want to map x, y, x+y, and a using symbolic values
			if (node instanceof MidSaveNode && ((MidSaveNode) node).savesRegister()) {
				MidRegisterNode regNode = ((MidSaveNode) node).getRegNode();
				if (regNode instanceof MidBinaryRegNode) {
					nodes.push(((MidBinaryRegNode) regNode).getLeftOperand());
					nodes.push(((MidBinaryRegNode) regNode).getRightOperand());
				}
			} else if (node instanceof MidLoadNode) {
				s.addVar((MidMemoryNode) node);
			}
		}
		return s;
	}

}
