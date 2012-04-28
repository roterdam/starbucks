package edu.mit.compilers.opt.dce;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.memory.MidTempDeclNode;
import edu.mit.compilers.opt.HashMapUtils;
import edu.mit.compilers.opt.State;

public class DCEGlobalState implements State<DCEGlobalState> {

	HashMap<MidMemoryNode, MidMemoryNode> definitionMap;
	HashMap<MidMemoryNode, List<MidMemoryNode>> mentionMap;

	public DCEGlobalState() {
		reset();
	}

	public DCEGlobalState(HashMap<MidMemoryNode, MidMemoryNode> definitionMap,
			HashMap<MidMemoryNode, List<MidMemoryNode>> mentionMap) {
		this.definitionMap = definitionMap;
		this.mentionMap = mentionMap;
	}

	public DCEGlobalState clone() {
		return new DCEGlobalState(HashMapUtils.deepClone(definitionMap),
				HashMapUtils.deepCloneList(mentionMap));
	}

	public void reset() {
		definitionMap = new HashMap<MidMemoryNode, MidMemoryNode>();
		mentionMap = new HashMap<MidMemoryNode, List<MidMemoryNode>>();
	}

	public DCEGlobalState getInitialState() {
		return new DCEGlobalState();
	}

	public DCEGlobalState getBottomState() {
		return new DCEGlobalState();
	}

	public DCEGlobalState join(DCEGlobalState s) {
		if (s == null) {
			return this.clone();
		}
		LogCenter.debug("[DCE] #########\n[CP] JOINING STATES: ");
		LogCenter.debug("[DCE] THIS: " + toString());
		LogCenter.debug("[DCE]");
		LogCenter.debug("[DCE] THAT: " + s.toString());
		DCEGlobalState out = new DCEGlobalState();
		HashMap<MidMemoryNode, MidMemoryNode> otherDefinitionMap = s
				.getDefinitionMap();
		for (MidMemoryNode m : definitionMap.keySet()) {
			MidMemoryNode ref = definitionMap.get(m);
			if (otherDefinitionMap.containsKey(m)
					&& otherDefinitionMap.get(m).equals(ref)) {
				out.saveDefinition(m, ref);
			}
		}
		// Purposely leave out the alias map - temps shouldn't persist after the
		// block.
		LogCenter.debug("[DCE]");
		LogCenter.debug("[DCE] RESULT: " + out.toString() + "\n[DCE] #####");
		return out;
	}

	public HashMap<MidMemoryNode, MidMemoryNode> getDefinitionMap() {
		return definitionMap;
	}

	public HashMap<MidMemoryNode, List<MidMemoryNode>> getMentionMap() {
		return mentionMap;
	}

	public void killReferences(MidMemoryNode memNode) {
		killReferences(memNode, true);
	}

	public void killReferences(MidMemoryNode memNode, boolean followLinkedNode) {
		LogCenter.debug("[DCE] Killing references to " + memNode);
		// Kill references, i.e. clear references to a after a = expr.
		List<MidMemoryNode> references = mentionMap.get(memNode);
		if (references != null) {
			// For all b = a + c, etc.
			for (MidMemoryNode m : references) {
				// Clear b = a+c
				definitionMap.remove(m);
			}
			mentionMap.remove(memNode);
		}
		// If this is a temp node whose references we're killing, we need to
		// clear references to the linked temp node as well.
		if (followLinkedNode && memNode instanceof MidTempDeclNode) {
			MidTempDeclNode tempNode = (MidTempDeclNode) memNode;
			MidTempDeclNode linkedNode = tempNode.getLink();
			if (linkedNode != null) {
				killReferences(linkedNode, false);
			}
		}
	}

	public void saveDefinition(MidMemoryNode memNode, MidMemoryNode refNode) {
		definitionMap.put(memNode, refNode);
		List<MidMemoryNode> mentions = mentionMap.get(refNode);
		if (mentions == null) {
			mentions = new ArrayList<MidMemoryNode>();
			mentionMap.put(refNode, mentions);
		}
		mentions.add(memNode);
	}

	public MidMemoryNode lookupDefinition(MidMemoryNode refNode) {
		LogCenter.debug("[DCE] Looking up definition for " + refNode);
		return definitionMap.get(refNode);
	}

	@Override
	public String toString() {
		return "DCEGlobalState => definitionMap:\n[CP]  "
				+ HashMapUtils.toMapString("DCE", definitionMap)
				+ "\n[DCE] mentionMap:\n[DCE]  "
				+ HashMapUtils.toMapString("DCE", mentionMap);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof DCEGlobalState)) {
			return false;
		}
		DCEGlobalState global = (DCEGlobalState) o;
		return (definitionMap.equals(global.getDefinitionMap()) && mentionMap
				.equals(global.getMentionMap()));
	}
}