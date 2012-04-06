package edu.mit.compilers.opt.cse;

import java.util.ArrayList;

import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.regops.MidArithmeticNode;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.Temp;
import edu.mit.compilers.opt.Transfer;
import edu.mit.compilers.opt.Value;

public class CSETransfer implements Transfer<CSEState> {
	
	ArrayList<MidSaveNode> assignments;
	
	public CSETransfer() {
		this.assignments = new ArrayList<MidSaveNode>();
	}	

	@Override
	public CSEState apply(Block b, CSEState s) {
		MidNode node = b.getHead();
		while (node != null) {
			// a = x + y
			if (node instanceof MidSaveNode && ((MidSaveNode) node).savesRegister()) {
				if (((MidSaveNode) node).getRegNode() instanceof MidArithmeticNode) {
					this.assignments.add((MidSaveNode) node);
				}
			} 
		}
		
		for (MidSaveNode m : this.assignments) {
			MidArithmeticNode r = (MidArithmeticNode) m.getRegNode();
			Value v1 = s.addVar(r.getLeftOperand());
			Value v2 = s.addVar(r.getRightOperand());
			Value v3 = s.addExpr(v1, v2, r.getNodeClass());
			s.addVarVal(m, v3);
			MidSaveNode tempNode = s.addTemp(v3, m.getDestinationNode());
		}
		
		return s;
	}

}
