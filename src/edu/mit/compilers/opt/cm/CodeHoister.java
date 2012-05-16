package edu.mit.compilers.opt.cm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.memory.MidArrayElementNode;
import edu.mit.compilers.codegen.nodes.regops.MidArithmeticNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidNegNode;
import edu.mit.compilers.codegen.nodes.regops.MidRegisterNode;
import edu.mit.compilers.codegen.nodes.regops.MidUseNode;
import edu.mit.compilers.opt.AnalyzerHelpers;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.regalloc.LivenessDoctor;

public class CodeHoister {

	private final LoopGenerator generator;
	private final LivenessDoctor doctor;
	private final Map<MidSaveNode, Loop> invariantSaves;

	public CodeHoister(LoopGenerator generator, LivenessDoctor doctor) {
		this.generator = generator;
		this.doctor = doctor;
		this.invariantSaves = new HashMap<MidSaveNode, Loop>();
	}

	public void hoist() {
		findInvariantSaveNodes();
		for (Entry<MidSaveNode, Loop> entry : invariantSaves.entrySet()) {
			MidSaveNode saveNode = entry.getKey();
			if (saveNode.usesArrayRegister()) {
				continue;
			}
			LogCenter.debug("CM",
					"Hoisting " + saveNode + " => " + saveNode.getRegNode());
			Block ownerBlock = generator.getBlock(saveNode);
			MidRegisterNode regNode = saveNode.getRegNode();
			List<MidNode> deleted = null;
			if (regNode instanceof MidLoadNode) {
				// MidLoadNode loadNode = (MidLoadNode) regNode;
				// if (loadNode.usesArrayRegister()) {
				// continue;
				// }
				deleted = AnalyzerHelpers.completeDeleteAssignment(saveNode,
						ownerBlock);
			} else if (regNode instanceof MidArithmeticNode) {
				deleted = AnalyzerHelpers.completeDeleteBinary(saveNode,
						ownerBlock);
			} else if (regNode instanceof MidNegNode) {
				deleted = AnalyzerHelpers.completeDeleteUnary(saveNode,
						ownerBlock);
			}

			if (deleted != null) {
				Loop loop = entry.getValue();
				Block preHeader = loop.getPreheaderBlock();
				for (MidNode node : deleted) {
					preHeader.add(node);
				}
			}
		}
	}

	private void findInvariantSaveNodes() {
		Map<MidLoadNode, Set<MidSaveNode>> useDefMap = new HashMap<MidLoadNode, Set<MidSaveNode>>();
		for (Entry<MidSaveNode, Set<MidUseNode>> entry : doctor.getDefUseMap()
				.entrySet()) {
			for (MidUseNode useNode : entry.getValue()) {
				if (useNode instanceof MidLoadNode) {
					MidLoadNode loadNode = (MidLoadNode) useNode;
					Set<MidSaveNode> saveSet = useDefMap.get(loadNode);
					if (saveSet == null) {
						saveSet = new LinkedHashSet<MidSaveNode>();
						useDefMap.put(loadNode, saveSet);
					}
					saveSet.add(entry.getKey());
				}
			}
		}

		for (Block block : generator.getAllBlocks()) {
			for (MidNode node : block) {
				if (node instanceof MidSaveNode
						&& ((MidSaveNode) node).savesRegister()) {
					MidSaveNode saveNode = (MidSaveNode) node;

					List<MidLoadNode> operands = new ArrayList<MidLoadNode>();
					MidRegisterNode regNode = saveNode.getRegNode();

					if (regNode instanceof MidLoadNode) {
						operands.add((MidLoadNode) regNode);
					} else if (regNode instanceof MidArithmeticNode) {
						MidArithmeticNode arithNode = (MidArithmeticNode) regNode;
						operands.add(arithNode.getLeftOperand());
						operands.add(arithNode.getRightOperand());
					} else if (regNode instanceof MidNegNode) {
						MidNegNode negNode = (MidNegNode) regNode;
						operands.add(negNode.getOperand());
					}

					Set<Loop> loops = generator.getLoops(block);
					Loop innerMostLoop = findInnerMostLoop(loops);
					// Check if operands are constants.
					boolean allConstant = true;
					for (MidLoadNode operand : operands) {
						if (!operand.getMemoryNode().isConstant()) {
							allConstant = false;
							break;
						}
					}
					if (allConstant) {
						invariantSaves.put(saveNode, innerMostLoop);
						continue;
					}

					// Check if all reaching defs of x and y are outside the
					// inner-most loop.
					boolean isInvariant = true;
					for (MidLoadNode operand : operands) {
						Block operandParent = generator.getBlock(operand);
						Set<Loop> operandLoops = generator
								.getLoops(operandParent);
						if (operandLoops.contains(innerMostLoop)) {
							isInvariant = false;
							break;
						}
					}
					if (isInvariant) {
						invariantSaves.put(saveNode, innerMostLoop);
					}
				}
			}
		}
	}

	private Loop findInnerMostLoop(Set<Loop> loops) {
		Loop out = null;
		int domSetSize = -1;
		DominanceRecord dr = generator.getRecord();
		for (Loop loop : loops) {
			Block start = loop.getStart();
			Set<Block> dominatedSet = dr.getBlocks(start);
			int newSize = dominatedSet.size();
			if (newSize > domSetSize) {
				out = loop;
				domSetSize = newSize;
			}
		}
		return out;
	}

}
