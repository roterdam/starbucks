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
		doActualHoisting();

	}

	private void doActualHoisting() {
		for (Entry<MidSaveNode, Loop> entry : invariantSaves.entrySet()) {
			MidSaveNode saveNode = entry.getKey();
			if (saveNode.usesArrayRegister()) {
				continue;
			}
			Block ownerBlock = generator.getBlock(saveNode);
			MidRegisterNode regNode = saveNode.getRegNode();
			List<MidNode> deleted = null;
			if (regNode instanceof MidLoadNode) {
				// MidLoadNode loadNode = (MidLoadNode) regNode;
				// if (loadNode.usesArrayRegister()) {
				// continue;
				// }
				deleted = AnalyzerHelpers
						.completeDeleteAssignment(saveNode, ownerBlock);
			} else if (regNode instanceof MidArithmeticNode) {
				deleted = AnalyzerHelpers
						.completeDeleteBinary(saveNode, ownerBlock);
			} else if (regNode instanceof MidNegNode) {
				deleted = AnalyzerHelpers
						.completeDeleteUnary(saveNode, ownerBlock);
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
		Map<MidUseNode, Set<MidSaveNode>> useDefMap = new HashMap<MidUseNode, Set<MidSaveNode>>();
		for (Entry<MidSaveNode, Set<MidUseNode>> entry : doctor.getDefUseMap()
				.entrySet()) {
			MidSaveNode saveNode = entry.getKey();
			Set<MidUseNode> useNodes = entry.getValue();

			for (MidUseNode useNode : useNodes) {
				Set<MidSaveNode> saveNodes = useDefMap.get(useNode);
				if (saveNodes == null) {
					saveNodes = new LinkedHashSet<MidSaveNode>();
					useDefMap.put(useNode, saveNodes);
				}
				saveNodes.add(saveNode);
			}
		}

		for (Block block : generator.getAllBlocks()) {
			Set<Loop> loops = generator.getLoops(block);
			Loop innerMostLoop = findInnerMostLoop(loops);

			for (MidNode node : block) {
				if (node instanceof MidSaveNode) {

					MidSaveNode saveNode = (MidSaveNode) node;
					if (((MidSaveNode) node).savesRegister()) {

						List<MidLoadNode> operands = new ArrayList<MidLoadNode>();
						MidRegisterNode regNode = saveNode.getRegNode();

						if (regNode instanceof MidLoadNode) {
							MidLoadNode loadNode = (MidLoadNode) regNode;
							operands.add(loadNode);
							if (loadNode.usesArrayRegister()) {
								// Skip hoisting array accesses for now.
								continue;
//								MidArrayElementNode elNode = loadNode.getMidArrayElementNode();
//								operands.add(elNode.getLoadNode());
							}
						} else if (regNode instanceof MidArithmeticNode) {
							MidArithmeticNode arithNode = (MidArithmeticNode) regNode;
							operands.add(arithNode.getLeftOperand());
							operands.add(arithNode.getRightOperand());
						} else if (regNode instanceof MidNegNode) {
							MidNegNode negNode = (MidNegNode) regNode;
							operands.add(negNode.getOperand());
						}

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
							Set<Loop> operandSaveLoops = new LinkedHashSet<Loop>();
							Set<MidSaveNode> saveNodes = useDefMap.get(operand);
							if (saveNodes == null) {
								// No other save nodes, so it must be invariant.
								continue;
							}
							for (MidSaveNode operandSave : saveNodes) {
								Block operandParent = generator
										.getBlock(operandSave);
								operandSaveLoops.addAll(generator
										.getLoops(operandParent));
							}
							if (operandSaveLoops.contains(innerMostLoop)) {
								isInvariant = false;
								break;
							}
						}
						if (isInvariant) {
							invariantSaves.put(saveNode, innerMostLoop);
						}
					} else {
						// Otherwise it saves a constant so it's invariant.
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
