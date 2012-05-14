package edu.mit.compilers.opt.regalloc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.regops.MidUseNode;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.Transfer;

/**
 * Processes a MidNodeList to produce a set of definitions and its corresponding
 * uses.
 * 
 * @author joshma
 */
public class LivenessDoctor implements Transfer<LivenessState> {

	private Map<MidSaveNode, Set<MidUseNode>> defUseMap;

	public LivenessDoctor() {
		defUseMap = new HashMap<MidSaveNode, Set<MidUseNode>>();
	}

	public void save(MidSaveNode node, Set<MidUseNode> useList) {
		defUseMap.put(node, useList);
	}

	@Override
	public LivenessState apply(Block block, LivenessState s) {
		LivenessState out = s.clone();
		for (MidNode node : block.reverse()) {
			if (node instanceof MidUseNode) {
				// Use.
				out.processUse((MidUseNode) node);
			} else if (node instanceof MidSaveNode) {
				// Definition.
				out.processDefinition((MidSaveNode) node, this);
			}
		}
		return out;
	}

	public Map<MidSaveNode, Set<MidUseNode>> getDefUseMap() {
		return defUseMap;
	}

}
