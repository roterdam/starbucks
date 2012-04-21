package edu.mit.compilers.opt.cp;

import java.util.HashMap;
import java.util.Map;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.memory.MidTempDeclNode;
import edu.mit.compilers.opt.State;

public class CPState implements State<CPState> {

	private Map<MidTempDeclNode, MidMemoryNode> tempMap;
	private Map<MidMemoryNode, MidTempDeclNode> mentionMap;

	public CPState() {
		tempMap = new HashMap<MidTempDeclNode, MidMemoryNode>();
		mentionMap = new HashMap<MidMemoryNode,MidTempDeclNode>();
	}

	@Override
	public CPState getInitialState() {
		return new CPState();
	}

	@Override
	public CPState getBottomState() {
		return new CPState();
	}

	@Override
	public CPState join(CPState s) {
		return new CPState();
	}

	/**
	 * Stores t1 -> a
	 */
	public void putTempReference(MidTempDeclNode tempNode,
			MidMemoryNode sourceNode) {
		LogCenter.debug("[CPS] Mapping " + tempNode + " to " + sourceNode);
		tempMap.put(tempNode, sourceNode);
		mentionMap.put(sourceNode, tempNode);
	}

	/**
	 * Looks up what t1 maps to, i.e. if b = t1, t1->a, then we'd like to know
	 * that b = a. Returns the node that we can replace t1 with, and null if
	 * none exist.
	 */
	public MidMemoryNode getReplacement(MidTempDeclNode tempNode) {
		LogCenter.debug("[CPS] Checking if " + tempNode + " maps to anything: " + tempMap.containsKey(tempNode));
		return tempMap.get(tempNode);
	}

	public void killReferences(MidMemoryNode destinationNode) {
		MidTempDeclNode tempNode = mentionMap.get(destinationNode);
		tempMap.remove(tempNode);
		mentionMap.remove(destinationNode);
	}

}
