package edu.mit.compilers.opt.cmold;

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
import edu.mit.compilers.codegen.nodes.regops.MidUseNode;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.Transformer;

public class CMTransformer extends Transformer<CMState> {

	Map<MidSaveNode, Set<MidUseNode>> defUse;
	Map<MidLoadNode, MidSaveNode> useDef;
	Map<MidSaveNode, Block> defBlock;

	public CMTransformer(Map<MidSaveNode, Set<MidUseNode>> defUse,
			Map<MidSaveNode, Block> defBlock) {
		this.defUse = defUse;
		this.useDef = buildUseDef(defUse);
		this.defBlock = defBlock;
	}

	private Map<MidLoadNode, MidSaveNode> buildUseDef(Map<MidSaveNode, Set<MidUseNode>> defUse) {
		HashMap<MidLoadNode, MidSaveNode> useDef = new HashMap<MidLoadNode, MidSaveNode>();
		for (Entry<MidSaveNode, Set<MidUseNode>> e : defUse.entrySet()) {
			MidSaveNode sn = e.getKey();
			for (MidUseNode un : e.getValue()) {
				if (un instanceof MidLoadNode) {
					MidLoadNode ln = (MidLoadNode) un;
					useDef.put(ln, sn);
				}
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

		if (l.getDepth() == 0) {
			LogCenter.debug("CM", "" + block.getHead()
					+ " not a loop, skipping");
			return;
		}
		
		boolean invariant;
		
		for (MidNode node : block) {
			invariant = false;
			if (node instanceof MidSaveNode && ((MidSaveNode) node).savesRegister()) {
				MidRegisterNode reg = (MidRegisterNode) ((MidSaveNode) node).getRegNode();
				if (reg instanceof MidArithmeticNode) {
					MidLoadNode left = ((MidArithmeticNode) reg).getLeftOperand();
					MidLoadNode right = ((MidArithmeticNode) reg).getRightOperand();
					Loop checkLeft = local.getLoop(defBlock.get(useDef.get(left)));
					Loop checkRight = local.getLoop(defBlock.get(useDef.get(right)));
					LogCenter.debug("CM", "loop " + l.getDepth());
					LogCenter.debug("CM", "left " + checkLeft);
					LogCenter.debug("CM", "right " + checkRight);
					if (checkLeft.getNum() < l.getNum() && checkRight.getNum() < l.getNum()) {
						invariant = true;
					}
				} else if (reg instanceof MidNegNode) {
					MidLoadNode neg = ((MidNegNode) reg).getOperand();
					Loop loop = local.getLoop(defBlock.get(useDef.get(neg)));
					if (loop.getNum() < l.getNum()) {
						invariant = true;
					}
				} else if (reg instanceof MidLoadNode) {
					MidLoadNode load = (MidLoadNode) reg;
					Loop loop = local.getLoop(defBlock.get(useDef.get(load)));
					if (loop.getNum() < l.getNum()) {
						invariant = true;
					}
				}

				LogCenter.debug("CM", "" + node.toString() + " is invariant? "
						+ invariant);
			}
		}
	}

}
