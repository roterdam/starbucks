package edu.mit.compilers.opt.regalloc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;

public class RegAllocState {

	private Map<MidMemoryNode, Set<MidLoadNode>> uses;

	public RegAllocState() {
		uses = new HashMap<MidMemoryNode, Set<MidLoadNode>>();
	}

	public RegAllocState(Map<MidMemoryNode, Set<MidLoadNode>> uses) {
		this.uses = uses;
	}

	public RegAllocState join(RegAllocState regAllocState) {
		Map<MidMemoryNode, Set<MidLoadNode>> joinedUses = new HashMap<MidMemoryNode, Set<MidLoadNode>>();
		for (Entry<MidMemoryNode, Set<MidLoadNode>> entry : uses.entrySet()) {
			MidMemoryNode memNode = entry.getKey();
			Set<MidLoadNode> thisUses = new HashSet<MidLoadNode>(
					entry.getValue());
			Set<MidLoadNode> otherUses = regAllocState.getUses(memNode);
			if (otherUses != null) {
				thisUses.addAll(otherUses);
			}
			joinedUses.put(memNode, thisUses);
		}
		// Catch any memory nodes that weren't in the first set.
		for (Entry<MidMemoryNode, Set<MidLoadNode>> entry : regAllocState
				.getUses().entrySet()) {
			MidMemoryNode memNode = entry.getKey();
			// If already have it, skip it.
			if (joinedUses.containsKey(memNode)) {
				continue;
			}
			Set<MidLoadNode> thisUses = new HashSet<MidLoadNode>(
					entry.getValue());
			Set<MidLoadNode> otherUses = getUses(memNode);
			if (otherUses != null) {
				thisUses.addAll(otherUses);
			}
			joinedUses.put(memNode, thisUses);
		}
		return new RegAllocState(joinedUses);
	}

	public Set<MidLoadNode> getUses(MidMemoryNode memNode) {
		return uses.get(memNode);
	}

	public RegAllocState clone() {
		Map<MidMemoryNode, Set<MidLoadNode>> newUses = new HashMap<MidMemoryNode, Set<MidLoadNode>>();
		for (Entry<MidMemoryNode, Set<MidLoadNode>> entry : getUses().entrySet()) {
			newUses.put(entry.getKey(), new HashSet<MidLoadNode>(entry
					.getValue()));
		}
		return new RegAllocState(newUses);
	}

	public void processUse(MidLoadNode node) {
		MidMemoryNode memNode = node.getMemoryNode();
		Set<MidLoadNode> currentUses = uses.get(memNode);
		if (currentUses == null) {
			currentUses = new HashSet<MidLoadNode>();
			uses.put(memNode, currentUses);
		}
		currentUses.add(node);
	}

	public void processDefinition(MidSaveNode node,
			LivenessDoctor livenessAnalyzer) {
		LogCenter.debug("RA", "Processing def " + node);
		MidMemoryNode destNode = node.getDestinationNode();
		Set<MidLoadNode> useList = uses.get(destNode);
		if (useList == null) {
			return;
		}
		uses.remove(destNode);
		livenessAnalyzer.save(node, useList);
	}

	public Map<MidMemoryNode, Set<MidLoadNode>> getUses() {
		return uses;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof RegAllocState)) {
			return false;
		}
		RegAllocState otherState = (RegAllocState) o;
		boolean isEqual = getUses().equals(otherState.getUses());
		return isEqual;
	}

}
