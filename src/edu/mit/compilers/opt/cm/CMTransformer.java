package edu.mit.compilers.opt.cm;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.regops.MidArithmeticNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidNegNode;
import edu.mit.compilers.codegen.nodes.regops.MidRegisterNode;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.Transformer;

public class CMTransformer extends Transformer<CMState> {
	
	Map<MidSaveNode, Set<MidLoadNode>> defUse;
	Map<MidLoadNode, MidSaveNode> useDef;
	Map<MidSaveNode, Block> defBlock;
	
	public CMTransformer(Map<MidSaveNode, Set<MidLoadNode>> defUse, Map<MidSaveNode, Block> defBlock) {
		this.defUse = defUse;
		this.useDef = buildUseDef(defUse);
		this.defBlock = defBlock;
	}

	private Map<MidLoadNode, MidSaveNode> buildUseDef(Map<MidSaveNode, Set<MidLoadNode>> defUse) {
		HashMap<MidLoadNode, MidSaveNode> useDef = new HashMap<MidLoadNode, MidSaveNode>();
		for (Entry<MidSaveNode, Set<MidLoadNode>> e : defUse.entrySet()) {
			MidSaveNode sn = e.getKey();
			for (MidLoadNode ln : e.getValue()) {
				useDef.put(ln, sn);
			}
		}
		return useDef;
	}

	@Override
	protected void transform(Block block, CMState state) {
		CMState local;
		if (state == null) {
			local = new CMState();
		} else {
			local = state.clone();
		}
		
		Loop l = local.getLoop(block);
		if (l == null) {
			return;
		}
		
		boolean invariant = false;
		
		for (MidNode node : block) {
			if (node instanceof MidSaveNode && ((MidSaveNode) node).savesRegister()) {
				MidRegisterNode reg = (MidRegisterNode) ((MidSaveNode) node).getRegNode();
				if (reg instanceof MidArithmeticNode) {
					MidLoadNode left = ((MidArithmeticNode) reg).getLeftOperand();
					MidLoadNode right = ((MidArithmeticNode) reg).getRightOperand();
					Loop leftLoop = local.getLoop(defBlock.get(useDef.get(left)));
					Loop rightLoop = local.getLoop(defBlock.get(useDef.get(right)));
					if (l.compareTo(leftLoop) == 1 && l.compareTo(rightLoop) == 1) {
						invariant = true;
					}
				} else if (reg instanceof MidNegNode) {
					MidLoadNode neg = ((MidNegNode) reg).getOperand();
					Loop loop = local.getLoop(defBlock.get(useDef.get(neg)));
					if (l.compareTo(loop) == 1) {
						invariant = true;
					}
				} else if (reg instanceof MidLoadNode) {
					MidLoadNode load = (MidLoadNode) reg;
					Loop loop = local.getLoop(defBlock.get(useDef.get(load)));
					if (l.compareTo(loop) == 1) {
						invariant = true;
					}
				}
				
				LogCenter.debug("CM", "" + reg.toString() + " is invariant? " + invariant);
			}
		}
	}

}
