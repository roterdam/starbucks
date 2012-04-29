package edu.mit.compilers.opt.regalloc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;

public class RegAllocState {

	private Map<MidMemoryNode, List<MidLoadNode>> uses;

	public RegAllocState() {
		uses = new HashMap<MidMemoryNode, List<MidLoadNode>>();
	}

	public RegAllocState(Map<MidMemoryNode, List<MidLoadNode>> uses) {
		this.uses = uses;
	}

	public RegAllocState join(RegAllocState regAllocState) {
		Map<MidMemoryNode, List<MidLoadNode>> joinedUses = new HashMap<MidMemoryNode, List<MidLoadNode>>();
		for (Entry<MidMemoryNode, List<MidLoadNode>> entry : uses.entrySet()) {
			MidMemoryNode memNode = entry.getKey();
			List<MidLoadNode> otherUses = regAllocState.getUses(memNode);
			List<MidLoadNode> newEntries = new ArrayList<MidLoadNode>();
			for (MidLoadNode loadNode : entry.getValue()) {
				if (otherUses.contains(loadNode)) {
					newEntries.add(loadNode);
				}
			}
			if (newEntries.size() > 0) {
				joinedUses.put(memNode, newEntries);
			}
		}
		return new RegAllocState(joinedUses);
	}

	public List<MidLoadNode> getUses(MidMemoryNode memNode) {
		return uses.get(memNode);
	}

	public RegAllocState clone() {
		Map<MidMemoryNode, List<MidLoadNode>> newUses = new HashMap<MidMemoryNode, List<MidLoadNode>>();
		for (Entry<MidMemoryNode, List<MidLoadNode>> entry : newUses.entrySet()) {
			newUses.put(entry.getKey(), new ArrayList<MidLoadNode>(entry
					.getValue()));
		}
		return new RegAllocState(newUses);
	}

	public void processUse(MidLoadNode node) {
		MidMemoryNode memNode = node.getMemoryNode();
		List<MidLoadNode> currentUses = uses.get(memNode);
		if (currentUses == null) {
			currentUses = new ArrayList<MidLoadNode>();
			uses.put(memNode, currentUses);
		}
		currentUses.add(node);
	}

	public void processDefinition(MidSaveNode node,
			LivenessAnalyzer livenessAnalyzer) {
		MidMemoryNode destNode = node.getDestinationNode();
		List<MidLoadNode> useList = uses.get(destNode);
		if (useList == null) {
			return;
		}
		uses.remove(destNode);
		livenessAnalyzer.save(node, useList);
	}

}
