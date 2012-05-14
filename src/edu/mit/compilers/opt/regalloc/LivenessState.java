package edu.mit.compilers.opt.regalloc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.regops.MidUseNode;
import edu.mit.compilers.opt.State;

public class LivenessState implements State<LivenessState> {

	private Map<MidMemoryNode, Set<MidUseNode>> uses;

	public LivenessState() {
		uses = new HashMap<MidMemoryNode, Set<MidUseNode>>();
	}

	public LivenessState(Map<MidMemoryNode, Set<MidUseNode>> uses) {
		this.uses = uses;
	}

	@Override
	public LivenessState join(LivenessState livenessState) {
		if (livenessState == null) {
			return this.clone();
		}
		Map<MidMemoryNode, Set<MidUseNode>> joinedUses = new HashMap<MidMemoryNode, Set<MidUseNode>>();
		for (Entry<MidMemoryNode, Set<MidUseNode>> entry : uses.entrySet()) {
			MidMemoryNode memNode = entry.getKey();
			Set<MidUseNode> thisUses = new HashSet<MidUseNode>(
					entry.getValue());
			Set<MidUseNode> otherUses = livenessState.getUses(memNode);
			if (otherUses != null) {
				thisUses.addAll(otherUses);
			}
			joinedUses.put(memNode, thisUses);
		}
		// Catch any memory nodes that weren't in the first set.
		for (Entry<MidMemoryNode, Set<MidUseNode>> entry : livenessState
				.getUses().entrySet()) {
			MidMemoryNode memNode = entry.getKey();
			// If already have it, skip it.
			if (joinedUses.containsKey(memNode)) {
				continue;
			}
			Set<MidUseNode> thisUses = new HashSet<MidUseNode>(
					entry.getValue());
			Set<MidUseNode> otherUses = getUses(memNode);
			if (otherUses != null) {
				thisUses.addAll(otherUses);
			}
			joinedUses.put(memNode, thisUses);
		}
		return new LivenessState(joinedUses);
	}

	public Set<MidUseNode> getUses(MidMemoryNode memNode) {
		return uses.get(memNode);
	}

	@Override
	public LivenessState clone() {
		Map<MidMemoryNode, Set<MidUseNode>> newUses = new HashMap<MidMemoryNode, Set<MidUseNode>>();
		for (Entry<MidMemoryNode, Set<MidUseNode>> entry : getUses()
				.entrySet()) {
			newUses.put(entry.getKey(), new HashSet<MidUseNode>(entry
					.getValue()));
		}
		return new LivenessState(newUses);
	}

	public void processUse(MidUseNode node) {
		MidMemoryNode memNode = node.getMemoryNode();
		Set<MidUseNode> currentUses = uses.get(memNode);
		if (currentUses == null) {
			currentUses = new HashSet<MidUseNode>();
			uses.put(memNode, currentUses);
		}
		currentUses.add(node);
	}
	
	public void processDefinition(MidSaveNode node,
			LivenessDoctor livenessAnalyzer) {
		LogCenter.debug("RA", "Processing def " + node);
		MidMemoryNode destNode = node.getDestinationNode();
		Set<MidUseNode> useList = uses.get(destNode);
		if (useList == null) {
			return;
		}
		uses.remove(destNode);
		if (livenessAnalyzer != null){
			livenessAnalyzer.save(node, useList);
		}
	}

	public void processDefinition(MidSaveNode node) {
		processDefinition(node, null);
	}
	
	public Map<MidMemoryNode, Set<MidUseNode>> getUses() {
		return uses;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof LivenessState)) {
			return false;
		}
		LivenessState otherState = (LivenessState) o;
		boolean isEqual = getUses().equals(otherState.getUses());
		return isEqual;
	}

	@Override
	public LivenessState getInitialState() {
		return new LivenessState();
	}

	@Override
	public LivenessState getBottomState() {
		return new LivenessState();
	}

}
