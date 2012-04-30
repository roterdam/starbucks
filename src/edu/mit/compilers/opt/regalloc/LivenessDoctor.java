package edu.mit.compilers.opt.regalloc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.MidNodeList;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.nodes.MidMethodDeclNode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.opt.Block;

/**
 * Processes a MidNodeList to produce a set of definitions and its corresponding
 * uses.
 * 
 * @author joshma
 */
public class LivenessDoctor {

	Map<MidSaveNode, List<MidLoadNode>> defUseMap;

	public LivenessDoctor() {
		defUseMap = new HashMap<MidSaveNode, List<MidLoadNode>>();
	}

	public Map<MidSaveNode, List<MidLoadNode>> analyze(
			MidSymbolTable symbolTable) {
		Map<String, MidMethodDeclNode> methods = symbolTable.getMethods();
		for (MidMethodDeclNode methodDeclNode : methods.values()) {
			analyze(methodDeclNode.getNodeList());
		}
		return defUseMap;
	}

	public void analyze(MidNodeList midNodeList) {
		List<Block> blocks = Block.getAllBlocks(midNodeList);
		LogCenter
				.debug("RA", "analyzing "
						+ Block.recursiveToString(blocks.get(0), new ArrayList<Block>(), 0));
		// Since this analysis is backwards, don't forget that out -> NODE ->
		// in!
		Map<Block, RegAllocState> inStates = new HashMap<Block, RegAllocState>();
		Map<Block, RegAllocState> outStates = new HashMap<Block, RegAllocState>();
		Block exit = findTail(blocks.get(0), new ArrayList<Block>());
		for (Block b : blocks) {
			if (b != exit) {
				inStates.put(b, new RegAllocState());
			}
		}
		outStates.put(exit, new RegAllocState());
		inStates.put(exit, livenessProcess(exit, outStates.get(exit)));

		List<Block> changed = new ArrayList<Block>(blocks);
		changed.remove(exit);
		while (changed.size() > 0) {
			Block n = changed.get(0);
			changed.remove(0);
			RegAllocState out = new RegAllocState();
			for (Block successor : n.getSuccessors()) {
				out = out.join(inStates.get(successor));
			}
			RegAllocState in = livenessProcess(n, out);
			if (!in.equals(inStates.get(n))) {
				inStates.put(n, in);
				changed.addAll(n.getPredecessors());
			}
		}
		LogCenter.debug("RA", "LIVENESS " + defUseMap);
	}

	private RegAllocState livenessProcess(Block block, RegAllocState state) {
		MidNode node = block.getTail();
		RegAllocState out = state.clone();
		while (true) {
			if (node instanceof MidLoadNode) {
				// Use.
				out.processUse((MidLoadNode) node);
			} else if (node instanceof MidSaveNode) {
				// Definition.
				out.processDefinition((MidSaveNode) node, this);
			}
			if (node == block.getHead()) {
				break;
			}
			node = node.getPrevNode();
		}
		return out;
	}

	private Block findTail(Block head, List<Block> visitedBlocks) {
		if (visitedBlocks.contains(head)) {
			return null;
		}
		visitedBlocks.add(head);
		List<Block> successors = head.getSuccessors();
		if (successors.size() == 0) {
			return head;
		}
		for (Block b : successors) {
			Block tail = findTail(b, new ArrayList<Block>(visitedBlocks));
			if (tail != null) {
				return tail;
			}
		}
		return null;
	}

	public void save(MidSaveNode node, List<MidLoadNode> useList) {
		defUseMap.put(node, useList);
	}

}
