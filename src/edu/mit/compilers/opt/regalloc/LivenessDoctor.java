package edu.mit.compilers.opt.regalloc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.Transfer;

/**
 * Processes a MidNodeList to produce a set of definitions and its corresponding
 * uses.
 * 
 * @author joshma
 */
public class LivenessDoctor implements Transfer<LivenessState> {

	private Map<MidSaveNode, Set<MidLoadNode>> defUseMap;

	public LivenessDoctor() {
		defUseMap = new HashMap<MidSaveNode, Set<MidLoadNode>>();
	}

	public void save(MidSaveNode node, Set<MidLoadNode> useList) {
		defUseMap.put(node, useList);
	}

	@Override
	public LivenessState apply(Block block, LivenessState s) {
		LivenessState out = s.clone();
		for (MidNode node : block.reverse()) {
			if (node instanceof MidLoadNode) {
				// Use.
				out.processUse((MidLoadNode) node);
			} else if (node instanceof MidSaveNode) {
				// Definition.
				out.processDefinition((MidSaveNode) node, this);
			}
		}
		return out;
	}

	public Map<MidSaveNode, Set<MidLoadNode>> getDefUseMap() {
		return defUseMap;
	}

}
