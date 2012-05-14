package edu.mit.compilers.opt.dce;

import java.util.Set;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.regops.MidArithmeticNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidNegNode;
import edu.mit.compilers.opt.AnalyzerHelpers;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.Transformer;
import edu.mit.compilers.opt.regalloc.LivenessState;

public class DeadCodeElim extends Transformer<LivenessState> {

	@Override
	protected void transform(Block b, LivenessState state) {
		
		LivenessState localState;
		if (state == null){
			//Should be the return block
			assert b.getSuccessors().isEmpty();
			localState = new LivenessState();
		} else {
			localState = state.clone();		
		}
			
		
		for (MidNode node : b.reverse()) {
			if (node instanceof MidLoadNode) {
				// Use.
				localState.processUse((MidLoadNode) node);
			} else if (node instanceof MidSaveNode) {				
				Set<MidLoadNode> uses = localState.getUses(((MidSaveNode)node).getDestinationNode());
				if (uses == null || uses.isEmpty()){
					// Delete dead code
					deleteSaveNodeEtAl((MidSaveNode) node);
				} else {
					// Definition.
					localState.processDefinition((MidSaveNode) node);
				}
			}
		}

	}

	private void deleteSaveNodeEtAl(MidSaveNode saveNode) {
		LogCenter.debug("DCE", "DELETING " + saveNode);
		if (saveNode.getRegNode() instanceof MidLoadNode) {
			saveNode.getRegNode().delete();
		}
		// a = -x
		if (saveNode.getRegNode() instanceof MidNegNode) {
			AnalyzerHelpers.completeDeleteUnary(saveNode);
		}
		// a = x + y
		if (saveNode.getRegNode() instanceof MidArithmeticNode) {
			AnalyzerHelpers.completeDeleteBinary(saveNode);
		}
		saveNode.deactivate();
	}

}
