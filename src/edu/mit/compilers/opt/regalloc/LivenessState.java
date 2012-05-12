package edu.mit.compilers.opt.regalloc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.MidCalloutNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidRegisterNode;
import edu.mit.compilers.opt.State;

public class LivenessState implements State<LivenessState> {

	private Map<MidMemoryNode, Set<MidRegisterNode>> uses;

	public LivenessState() {
		uses = new HashMap<MidMemoryNode, Set<MidRegisterNode>>();
	}

	public LivenessState(Map<MidMemoryNode, Set<MidRegisterNode>> uses) {
		this.uses = uses;
	}

	public LivenessState join(LivenessState livenessState) {
		if (livenessState == null) {
			return this.clone();
		}
		Map<MidMemoryNode, Set<MidRegisterNode>> joinedUses = new HashMap<MidMemoryNode, Set<MidRegisterNode>>();
		for (Entry<MidMemoryNode, Set<MidRegisterNode>> entry : uses.entrySet()) {
			MidMemoryNode memNode = entry.getKey();
			Set<MidRegisterNode> thisUses = new HashSet<MidRegisterNode>(
					entry.getValue());
			Set<MidRegisterNode> otherUses = livenessState.getUses(memNode);
			if (otherUses != null) {
				thisUses.addAll(otherUses);
			}
			joinedUses.put(memNode, thisUses);
		}
		// Catch any memory nodes that weren't in the first set.
		for (Entry<MidMemoryNode, Set<MidRegisterNode>> entry : livenessState
				.getUses().entrySet()) {
			MidMemoryNode memNode = entry.getKey();
			// If already have it, skip it.
			if (joinedUses.containsKey(memNode)) {
				continue;
			}
			Set<MidRegisterNode> thisUses = new HashSet<MidRegisterNode>(
					entry.getValue());
			Set<MidRegisterNode> otherUses = getUses(memNode);
			if (otherUses != null) {
				thisUses.addAll(otherUses);
			}
			joinedUses.put(memNode, thisUses);
		}
		return new LivenessState(joinedUses);
	}

	public Set<MidRegisterNode> getUses(MidMemoryNode memNode) {
		return uses.get(memNode);
	}

	@Override
	public LivenessState clone() {
		Map<MidMemoryNode, Set<MidRegisterNode>> newUses = new HashMap<MidMemoryNode, Set<MidRegisterNode>>();
		for (Entry<MidMemoryNode, Set<MidRegisterNode>> entry : getUses()
				.entrySet()) {
			newUses.put(entry.getKey(), new HashSet<MidRegisterNode>(entry
					.getValue()));
		}
		return new LivenessState(newUses);
	}

	public void processUse(MidLoadNode node) {
		MidMemoryNode memNode = node.getMemoryNode();
		Set<MidRegisterNode> currentUses = uses.get(memNode);
		if (currentUses == null) {
			currentUses = new HashSet<MidRegisterNode>();
			uses.put(memNode, currentUses);
		}
		currentUses.add(node);
	}
	
	public void processCalloutUse(MidCalloutNode node) {
		List<MidMemoryNode> memNodes = node.getParams();
		for (MidMemoryNode memNode : memNodes){
			Set<MidRegisterNode> currentUses = uses.get(memNode);
			if (currentUses == null) {
				currentUses = new HashSet<MidRegisterNode>();
				uses.put(memNode, currentUses);
			}
			currentUses.add(node);
		}
	}


	public void processDefinition(MidSaveNode node,
			LivenessDoctor livenessAnalyzer) {
		LogCenter.debug("RA", "Processing def " + node);
		MidMemoryNode destNode = node.getDestinationNode();
		Set<MidRegisterNode> useList = uses.get(destNode);
		if (useList == null) {
			return;
		}
		uses.remove(destNode);
		livenessAnalyzer.save(node, useList);
	}

	public Map<MidMemoryNode, Set<MidRegisterNode>> getUses() {
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

	public LivenessState getInitialState() {
		return new LivenessState();
	}

	public LivenessState getBottomState() {
		return new LivenessState();
	}

}
