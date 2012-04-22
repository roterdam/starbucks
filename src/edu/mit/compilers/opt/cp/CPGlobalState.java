package edu.mit.compilers.opt.cp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sun.media.sound.AlawCodec;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.memory.MidTempDeclNode;
import edu.mit.compilers.opt.HashMapUtils;
import edu.mit.compilers.opt.State;

public class CPGlobalState implements State<CPGlobalState> {

	HashMap<MidTempDeclNode, MidMemoryNode> aliasMap;
	HashMap<MidMemoryNode, List<MidTempDeclNode>> aliasMentionMap;
	HashMap<MidMemoryNode, MidMemoryNode> definitionMap;
	HashMap<MidMemoryNode, List<MidMemoryNode>> mentionMap;

	public CPGlobalState() {
		reset();
	}

	public CPGlobalState(HashMap<MidTempDeclNode, MidMemoryNode> aliasMap,
			HashMap<MidMemoryNode, List<MidTempDeclNode>> aliasMentionMap,
			HashMap<MidMemoryNode, MidMemoryNode> definitionMap,
			HashMap<MidMemoryNode, List<MidMemoryNode>> mentionMap) {
		this.aliasMap = aliasMap;
		this.aliasMentionMap = aliasMentionMap;
		this.definitionMap = definitionMap;
		this.mentionMap = mentionMap;
	}

	public CPGlobalState clone() {
		return new CPGlobalState(HashMapUtils.deepClone(aliasMap),
				HashMapUtils.deepCloneList(aliasMentionMap),
				HashMapUtils.deepClone(definitionMap),
				HashMapUtils.deepCloneList(mentionMap));
	}

	public void reset() {
		aliasMap = new HashMap<MidTempDeclNode, MidMemoryNode>();
		aliasMentionMap = new HashMap<MidMemoryNode, List<MidTempDeclNode>>();
		definitionMap = new HashMap<MidMemoryNode, MidMemoryNode>();
		mentionMap = new HashMap<MidMemoryNode, List<MidMemoryNode>>();
	}

	@Override
	public CPGlobalState getInitialState() {
		return new CPGlobalState();
	}

	@Override
	public CPGlobalState getBottomState() {
		return new CPGlobalState();
	}

	@Override
	public CPGlobalState join(CPGlobalState s) {
		if (s == null) {
			return this.clone();
		}
		CPGlobalState out = new CPGlobalState();
		HashMap<MidMemoryNode, MidMemoryNode> otherDefinitionMap = s
				.getDefinitionMap();
		for (MidMemoryNode m : definitionMap.keySet()) {
			MidMemoryNode ref = definitionMap.get(m);
			if (otherDefinitionMap.containsKey(m)
					&& otherDefinitionMap.get(m) == ref) {
				out.saveDefinition(m, ref);
			}
		}
		// Purposely leave out the alias map - temps shouldn't persist after the
		// block.
		return out;
	}

	public HashMap<MidTempDeclNode, MidMemoryNode> getAliasMap() {
		return aliasMap;
	}

	public HashMap<MidMemoryNode, MidMemoryNode> getDefinitionMap() {
		return definitionMap;
	}

	public HashMap<MidMemoryNode, List<MidTempDeclNode>> getAliasMentionMap() {
		return aliasMentionMap;
	}

	public HashMap<MidMemoryNode, List<MidMemoryNode>> getMentionMap() {
		return mentionMap;
	}

	public void saveAlias(MidTempDeclNode tempNode, MidMemoryNode refNode) {
		// Remembers that t1 = a, so b = t1 can be translated to b = a.
		aliasMap.put(tempNode, refNode);
		List<MidTempDeclNode> mentions = aliasMentionMap.get(refNode);
		if (mentions == null) {
			mentions = new ArrayList<MidTempDeclNode>();
			aliasMentionMap.put(refNode, mentions);
		}
		mentions.add(tempNode);
	}
	
	public void killReferences(MidMemoryNode memNode) {
		killReferences(memNode, true);
	}

	public void killReferences(MidMemoryNode memNode, boolean followLinkedNode) {
		LogCenter.debug("[CP] Killing references to " + memNode);
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
		// Clear aliases that reference memNode, i.e. t1 -> a
		List<MidTempDeclNode> aliasReferences = aliasMentionMap.get(memNode);
		if (aliasReferences != null) {
			for (MidTempDeclNode t : aliasReferences) {
				aliasMap.remove(t);
			}
			aliasMentionMap.remove(memNode);
		}
		// If this is a temp node whose references we're killing, we need to
		// clear aliases that involve _linked_ temp nodes. We cannot assume that
		// this temp node has overwritten the alias, because it may have been
		// skipped if it doesn't save a load node.
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

	public MidMemoryNode lookupAlias(MidTempDeclNode refNode) {
		// If the temp node is linked to another (due to something like global
		// CSE) then we use the existing temp node if possible.
		MidTempDeclNode linkedNode = refNode.getLink();
		if (linkedNode != null && aliasMap.containsKey(linkedNode)) {
			refNode = linkedNode;
		}

		return aliasMap.get(refNode);
	}

	public MidMemoryNode lookupDefinition(MidMemoryNode refNode) {
		LogCenter.debug("[CP] Looking up definition for " + refNode);
		return definitionMap.get(refNode);
	}

	@Override
	public String toString() {
		return "CPGlobalState => definitionMap:\n[CP]  "
				+ HashMapUtils.toMapString("CP", definitionMap)
				+ "\n[CP] mentionMap:\n[CP]  "
				+ HashMapUtils.toMapString("CP", mentionMap)
				+ "\n[CP] aliasMap:\n[CP]  "
				+ HashMapUtils.toMapString("CP", aliasMap)
				+ "\n[CP] aliasMentionMap:\n[CP] "
				+ HashMapUtils.toMapString("CP", aliasMentionMap);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CPGlobalState)) {
			return false;
		}
		CPGlobalState global = (CPGlobalState) o;
		return (definitionMap.equals(global.getDefinitionMap())
				&& mentionMap.equals(global.getMentionMap())
				&& aliasMap.equals(global.getAliasMap()) && aliasMentionMap
					.equals(global.getAliasMentionMap()));
	}
}