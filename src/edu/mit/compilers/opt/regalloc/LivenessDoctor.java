package edu.mit.compilers.opt.regalloc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.mit.compilers.codegen.nodes.MidCallNode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.regops.MidCompareNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidRegisterNode;
import edu.mit.compilers.opt.Block;
import edu.mit.compilers.opt.Transfer;

/**
 * Processes a MidNodeList to produce a set of definitions and its corresponding
 * uses.
 * 
 * @author joshma
 */
public class LivenessDoctor implements Transfer<LivenessState> {

	private Map<MidSaveNode, Set<MidRegisterNode>> defUseMap;
	private Set<MidRegisterNode> importantNodes;

	public LivenessDoctor() {
		defUseMap = new HashMap<MidSaveNode, Set<MidRegisterNode>>();
		importantNodes = new HashSet<MidRegisterNode>();
	}

	public void save(MidSaveNode node, Set<MidRegisterNode> useList) {
		defUseMap.put(node, useList);
	}

	public LivenessState apply(Block block, LivenessState s) {
		LivenessState out = s.clone();
		for (MidNode node : block.reverse()) {
			if (node instanceof MidLoadNode) {
				// Use.
				out.processUse((MidLoadNode) node);
			} else if (node instanceof MidCallNode){
				for (MidLoadNode ln : ((MidCallNode)node).getParamNodes()){
					out.processUse(ln);
					importantNodes.add(ln);
				}
				importantNodes.add((MidCallNode)node);
			} else if (node instanceof MidCompareNode){
				importantNodes.addAll(((MidCompareNode)node).getOperandRegisterNodes());
			} else if (node instanceof MidSaveNode) {
				// Definition.
				out.processDefinition((MidSaveNode) node, this);
			} 
		}
		return out;
	}
	
	public Map<MidSaveNode, Set<MidRegisterNode>> getDefUseMap() {
		return defUseMap;
	}
	
	public Set<MidRegisterNode> getImportantNodes(){
		return importantNodes;
	}

}
