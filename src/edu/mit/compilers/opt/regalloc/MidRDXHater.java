package edu.mit.compilers.opt.regalloc;

import java.util.List;
import java.util.Map;

import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.nodes.regops.MidArithmeticNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;

/**
 * Indicates that a node will corrupt values in RDX. Requires special care to
 * push and pop to preserve RDX.
 * 
 * @author joshma
 * 
 */
public class MidRDXHater extends MidArithmeticNode implements LiveWebsActivist {

	public MidRDXHater(MidLoadNode leftOperand, MidLoadNode rightOperand) {
		super(leftOperand, rightOperand);
	}

	private List<Web> liveWebs;
	private boolean shouldPreserveRDX;

	public boolean isCommutative() {
		return false;
	}

	public void setLiveWebs(List<Web> liveWebs) {
		this.liveWebs = liveWebs;
	}

	public void applyAllocatedMapping(Map<Web, Reg> mapping) {
		for (Web web : liveWebs) {
			if (mapping.get(web) == Reg.RDX) {
				shouldPreserveRDX = true;
			}
		}
	}

	public boolean shouldPreserveRDX() {
		return shouldPreserveRDX;
	}

}
