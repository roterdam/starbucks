package edu.mit.compilers.opt.cm;

import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.regops.MidArithmeticNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidNegNode;
import edu.mit.compilers.codegen.nodes.regops.MidRegisterNode;
import edu.mit.compilers.opt.Analyzer;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.Transformer;
import edu.mit.compilers.opt.regalloc.LivenessDoctor;
import edu.mit.compilers.opt.regalloc.LivenessState;

public class CMTransformer extends Transformer<CMState> {
	
	Analyzer<LivenessState, LivenessDoctor> liveness;
	
	public CMTransformer(
			Analyzer<LivenessState, LivenessDoctor> livenessAnalyzer) {
		this.liveness = livenessAnalyzer;
	}

	@Override
	protected void transform(Block block, CMState state) {
		CMState local;
		if (state == null) {
			local = new CMState();
		} else {
			local = state.clone();
		}
		
		if (state.getLoop(block) == null) {
			return;
		}
		
		for (MidNode node : block) {
			if (node instanceof MidSaveNode && ((MidSaveNode) node).savesRegister()) {
				MidRegisterNode reg = (MidRegisterNode) ((MidSaveNode) node).getRegNode();
				if (reg instanceof MidArithmeticNode) {
					
				} else if (reg instanceof MidNegNode) {
					
				} else if (reg instanceof MidLoadNode) {
					
				}
			}
		}
	}

}
