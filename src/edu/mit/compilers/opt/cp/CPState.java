package edu.mit.compilers.opt.cp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.memory.MidConstantNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.memory.MidTempDeclNode;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.HashMapUtils;
import edu.mit.compilers.opt.State;

public class CPState implements State<CPState> {

	HashMap<MidMemoryNode, MidMemoryNode> definitionMap;

	public CPState() {
		definitionMap = new HashMap<MidMemoryNode, MidMemoryNode>();
	}

	public CPState(HashMap<MidMemoryNode, MidMemoryNode> definitionMap) {
		this.definitionMap = definitionMap;
	}

	public HashMap<MidMemoryNode, MidMemoryNode> getDefinitionMap() {
		return definitionMap;
	}

	public void reset() {
		definitionMap.clear();
	}

	@Override
	public CPState getInitialState(Block b) {
		return new CPState();
	}

	@Override
	public CPState getBottomState() {
		return new CPState();
	}

	@Override
	public CPState join(CPState s) {
		if (s == null) {
			return this.clone();
		}
		HashMap<MidMemoryNode, MidMemoryNode> outMap = new HashMap<MidMemoryNode, MidMemoryNode>();

		HashMap<MidMemoryNode, MidMemoryNode> otherMap = s.getDefinitionMap();
		Set<MidMemoryNode> keySet = new HashSet<MidMemoryNode>(
				definitionMap.keySet());
		keySet.addAll(otherMap.keySet());
		for (MidMemoryNode key : keySet) {
			MidMemoryNode map1 = definitionMap.get(key);
			MidMemoryNode map2 = otherMap.get(key);
			if (map1 instanceof MidConstantNode
					&& map2 instanceof MidConstantNode) {
				long val1 = ((MidConstantNode) map1).getValue();
				long val2 = ((MidConstantNode) map2).getValue();
				if (val1 == val2) {
					outMap.put(key, map1);
				}
			} else if (map1 != null && map1 == map2) {
				outMap.put(key, map1);
			}
		}
		return new CPState(outMap);
	}

	public CPState clone() {
		return new CPState(HashMapUtils.deepClone(definitionMap));
	}

	public void killReferences(MidMemoryNode destNode) {
		// Clear existing references.
		for (Entry<MidMemoryNode, MidMemoryNode> entry : new ArrayList<Entry<MidMemoryNode, MidMemoryNode>>(
				definitionMap.entrySet())) {
			if (entry.getValue() == destNode) {
				definitionMap.remove(entry.getKey());
			}
		}
		definitionMap.remove(destNode);
	}

	public void processDef(MidMemoryNode fromNode, MidMemoryNode destNode) {
		// Avoid mapping a non-temp node to a temp node.
		if (fromNode instanceof MidTempDeclNode
				&& !(destNode instanceof MidTempDeclNode)) {
			return;
		}

		LogCenter.debug("CP", "Processing def " + destNode + " <- " + fromNode);

		MidMemoryNode lookedUpNode = definitionMap.get(fromNode);
		if (lookedUpNode != null) {
			definitionMap.put(destNode, lookedUpNode);
			LogCenter.debug("CP", "Ended up mapping " + destNode + " <- "
					+ lookedUpNode);
		} else {
			definitionMap.put(destNode, fromNode);
		}
	}

	public MidMemoryNode lookup(MidMemoryNode memNode) {
		if (!definitionMap.containsKey(memNode)) {
			return memNode;
		}
		return definitionMap.get(memNode);
	}

	@Override
	public String toString() {
		return "CPState => definitionMap:\n[CP]  "
				+ HashMapUtils.toMapString(definitionMap);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CPState)) {
			return false;
		}
		CPState global = (CPState) o;
		return definitionMap.equals(global.getDefinitionMap());
	}
}
