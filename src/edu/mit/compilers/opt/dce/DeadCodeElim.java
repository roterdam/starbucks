package edu.mit.compilers.opt.dce;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.MidSymbolTable;
import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.nodes.MidMethodDeclNode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.regops.MidArithmeticNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidNegNode;
import edu.mit.compilers.codegen.nodes.regops.MidRegisterNode;
import edu.mit.compilers.opt.BackwardsAnalyzer;
import edu.mit.compilers.opt.regalloc.LiveWebsActivist;
import edu.mit.compilers.opt.regalloc.LivenessDoctor;
import edu.mit.compilers.opt.regalloc.LivenessState;

public class DeadCodeElim {

	private final MidSymbolTable symbolTable;
	// MidMethodDeclNode honors CALLEE_SAVED_REGISTERS.
	public final static Reg[] CALLEE_SAVED_REGISTERS = { Reg.RBX, Reg.R12,
			Reg.R13, Reg.R14, Reg.R15 };
	public final static Reg[] CALLER_SAVED_REGISTERS = { Reg.RCX, Reg.RDX,
			Reg.RSI, Reg.RDI, Reg.R8, Reg.R9 };
	// Sorted in "good idea to use" to "fine, use if necessary."
	public final static Reg[] USABLE_REGISTERS = {
			// Callee saved.
			Reg.RBX, Reg.R12, Reg.R13, Reg.R14, Reg.R15,
			// Caller saved.
			Reg.RCX, Reg.RSI, Reg.RDI, Reg.R8, Reg.R9,
			// Caller saved, but also used in DIV and MOD.
			Reg.RDX };

	public DeadCodeElim(MidSymbolTable symbolTable) {
		this.symbolTable = symbolTable;
	}

	public void run() {
		LivenessDoctor doctor = new LivenessDoctor();
		BackwardsAnalyzer<LivenessState, LivenessDoctor> analyzer = new BackwardsAnalyzer<LivenessState, LivenessDoctor>(
				new LivenessState().getBottomState(), doctor);
		analyzer.analyze(symbolTable);
		System.out.println("--------------------");
		System.out.println("--------------------");
		System.out.println("--map");
		Map<MidSaveNode, Set<MidRegisterNode>> map = doctor.getDefUseMap();
		for (Map.Entry entry : map.entrySet()) {
		    System.out.println(entry.getKey() + ", " + entry.getValue());
		}
		
		Map<MidRegisterNode, Set<MidSaveNode>> reverseMap = new HashMap<MidRegisterNode, Set<MidSaveNode>>();
		for (Map.Entry entry : map.entrySet()) {
			for (MidRegisterNode regNode : (Set<MidRegisterNode>)entry.getValue()){
				if (reverseMap.containsKey(regNode)){
					Set<MidSaveNode> oldSet = reverseMap.get(regNode);
					oldSet.add((MidSaveNode) entry.getKey());
					reverseMap.put(regNode, oldSet);
				} else {
					Set<MidSaveNode> newSet = new HashSet<MidSaveNode>();
					newSet.add((MidSaveNode) entry.getKey());
					reverseMap.put(regNode, newSet);
				}
			}
		}

		System.out.println("--------------------");
		System.out.println("--reverse map");

		for (Map.Entry entry : reverseMap.entrySet()) {
		    System.out.println(entry.getKey() + ", " + entry.getValue());
		}
		System.out.println("--------------------");

		
		//find important nodes.
		//A node is important if one of it's decendents is a callout node
		
		Set<MidSaveNode> allSaveNodes = allSaveNodes();
		
		Set<MidRegisterNode> importantNodes = new HashSet<MidRegisterNode>();
		importantNodes.addAll(doctor.getImportantNodes());
		System.out.println("important " + importantNodes);
		System.out.println("---");

		Set<MidSaveNode> neededSaveNodes = new HashSet<MidSaveNode>();
		for (MidRegisterNode calloutNode : importantNodes){
			if (reverseMap.containsKey(calloutNode)){
				neededSaveNodes.addAll(reverseMap.get(calloutNode));
			} else {
				LogCenter.debug("DCE", "Why you no have important callout node?" + calloutNode.toString());
			}
		}
		while (neededSaveNodes.size() > 0){
			//Add all the nodes that the save nodes need to important nodes
			for (MidSaveNode saveNode : neededSaveNodes){
				importantNodes.addAll(saveNodeDependsOn(saveNode));
			}
			//Then remove the save nodes
			allSaveNodes.removeAll(neededSaveNodes);
			neededSaveNodes.clear();
			
			System.out.println("important " + importantNodes);
			System.out.println("---");
			
			//Now find all the interesting save nodes
			for (MidRegisterNode registerNode : importantNodes){
				if (reverseMap.containsKey(registerNode)){
					neededSaveNodes.addAll(reverseMap.get(registerNode));
				} else {
					LogCenter.debug("DCE", "Why you no have important register node?" + registerNode.toString());
				}
			}

			//But only the ones not added yet
			neededSaveNodes.retainAll(allSaveNodes);
		}
		System.out.println("Cruft remaining: " + allSaveNodes);
		
		
		
		System.out.println("--------------------");
		deleteCruft(allSaveNodes);
		System.out.println("--------------------");
		System.out.println("--------------------");

	}
	
	private Set<MidSaveNode> allSaveNodes(){
		Set<MidSaveNode> out = new HashSet<MidSaveNode>();
		for (Entry<String, MidMethodDeclNode> entry : symbolTable.getMethods().entrySet()) {
			MidMethodDeclNode methodDeclNode = entry.getValue();
			for (MidNode node : methodDeclNode.getNodeList()) {
				if (node instanceof MidSaveNode) {
					out.add((MidSaveNode)node);
				}
			}
		}
		return out;
		
	}

	private void deleteCruft(Set<MidSaveNode> cruft) {
		for (MidSaveNode crufty : cruft){
			deleteSaveNodeEtAl(crufty);
		}
	}
	
	private void deleteSaveNodeEtAl(MidSaveNode saveNode){
		LogCenter.debug("DCE","DELETING " + saveNode);
		if (saveNode.getRegNode() instanceof MidLoadNode) {
			saveNode.getRegNode().delete();
		}
		// a = -x
		if (saveNode.getRegNode() instanceof MidNegNode) {
			MidNegNode negNode = (MidNegNode) saveNode.getRegNode();
			LogCenter.debug("DCE", "|_DELETING " + negNode);

			negNode.getOperand().delete();
			negNode.delete();
		}
		// a = x + y
		if (saveNode.getRegNode() instanceof MidArithmeticNode) {
			MidArithmeticNode arithNode = (MidArithmeticNode) saveNode.getRegNode();
			LogCenter.debug("DCE","|_DELETING " + arithNode);

			arithNode.getLeftOperand().delete();
			arithNode.getRightOperand().delete();
			arithNode.delete();
		}
		saveNode.delete();
	}

	private Set<MidRegisterNode> saveNodeDependsOn(MidSaveNode saveNode){
		Set<MidRegisterNode> out = new HashSet<MidRegisterNode>();
		// a = x
		if (saveNode.getRegNode() instanceof MidLoadNode) {
			out.add(saveNode.getRegNode());
		}
		// a = -x
		if (saveNode.getRegNode() instanceof MidNegNode) {
			out.add(((MidNegNode)saveNode.getRegNode()).getOperand());
		}
		// a = x + y
		if (saveNode.getRegNode() instanceof MidArithmeticNode) {
			out.add(((MidArithmeticNode)saveNode.getRegNode()).getLeftOperand());
			out.add(((MidArithmeticNode)saveNode.getRegNode()).getRightOperand());
		}	
		return out;
	}
	


}
