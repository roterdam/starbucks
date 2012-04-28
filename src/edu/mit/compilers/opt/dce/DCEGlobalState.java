package edu.mit.compilers.opt.dce;

import java.util.HashSet;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.opt.State;

public class DCEGlobalState implements State<DCEGlobalState> {

	HashSet<MidMemoryNode> needed;

	public DCEGlobalState() {
		reset();
	}

	public DCEGlobalState(HashSet<MidMemoryNode> needed) {
		this.needed = needed;
	}

	@SuppressWarnings("unchecked")
	public DCEGlobalState clone() {
		return new DCEGlobalState((HashSet)needed.clone());
	}

	public void reset() {
		needed = new HashSet<MidMemoryNode>();
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
		
		this.needed.addAll(s.getNeeded());
		
		// Purposely leave out the alias map - temps shouldn't persist after the
		// block.
		LogCenter.debug("[DCE]");
		LogCenter.debug("[DCE] RESULT: " + out.toString() + "\n[DCE] #####");
		return out;
	}

	public HashSet<MidMemoryNode> getNeeded() {
		return needed;
	}

	public void addNeeded(MidMemoryNode memNode){
		needed.add(memNode);
		System.out.println("Needed list: " + needed.toString());
	}
	
	public Boolean isNeeded(MidMemoryNode memNode){
		System.out.println("Needed list: " + needed.toString());
		if (needed.contains(memNode)){
			needed.remove(memNode);
			return true;
		} else {
			return false;
		}
	}

//	public void saveDefinition(MidMemoryNode memNode, MidMemoryNode refNode) {
//		definitionMap.put(memNode, refNode);
//		List<MidMemoryNode> mentions = mentionMap.get(refNode);
//		if (mentions == null) {
//			mentions = new ArrayList<MidMemoryNode>();
//			mentionMap.put(refNode, mentions);
//		}
//		mentions.add(memNode);
//	}
//
//	public MidMemoryNode lookupDefinition(MidMemoryNode refNode) {
//		LogCenter.debug("[DCE] Looking up definition for " + refNode);
//		return definitionMap.get(refNode);
//	}

	@Override
	public String toString() {
		return "DCEGlobalState => definitionMap:\n[CP]  "
				+ needed.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof DCEGlobalState)) {
			return false;
		}
		DCEGlobalState global = (DCEGlobalState) o;
		return (needed.equals(global.getNeeded()) );
	}
}