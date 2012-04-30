package edu.mit.compilers.opt.dce;

import java.util.HashSet;
import java.util.List;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.nodes.MidCalloutNode;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;
import edu.mit.compilers.codegen.nodes.memory.MidArrayElementNode;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.regops.MidArithmeticNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidNegNode;
import edu.mit.compilers.opt.State;

public class DCEGlobalState implements State<DCEGlobalState> {

	private HashSet<MidMemoryNode> seeking;
	private HashSet<MidSaveNode> needed;
	
	public DCEGlobalState() {
		reset();
	}

	public DCEGlobalState(HashSet<MidSaveNode> needed) {
		this.needed = needed;
		this.seeking = new HashSet<MidMemoryNode>();
	}

	@SuppressWarnings("unchecked")
	public DCEGlobalState clone() {
		return new DCEGlobalState((HashSet)needed.clone());
	}

	public void reset() {
		needed = new HashSet<MidSaveNode>();
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
		LogCenter.debug("[DCE] #########\n[DCE] JOINING STATES: ");
		LogCenter.debug("[DCE] THIS: " + toString());
		LogCenter.debug("[DCE]");
		LogCenter.debug("[DCE] THAT: " + s.toString());
		DCEGlobalState out = new DCEGlobalState();
		
		out.needed.addAll(getNeeded());
		out.needed.addAll(s.getNeeded());
		
		LogCenter.debug("[DCE]");
		LogCenter.debug("[DCE] RESULT: " + out.toString() + "\n[DCE] #####");
		return out;
	}

	public HashSet<MidSaveNode> getNeeded() {
		return needed;
	}
	
	public boolean isNeeded(MidNode midNode) {
		return needed.contains(midNode);
	}

	private void addSeeking(MidMemoryNode memNode){
		seeking.add(memNode);
		System.out.println("Adding " + memNode.toString());
		System.out.println("Seeking list: " + needed.toString());
	}
	
	private void addSeeking(List<MidMemoryNode> memNodes){
		seeking.addAll(memNodes);
		System.out.println("Adding List " + memNodes.toString());
		System.out.println("Seeking list: " + needed.toString());
	}
	
	private void addSeeking(MidSaveNode saveNode){
		// a = x
		if (saveNode.getRegNode() instanceof MidLoadNode) {
			addSeeking(((MidLoadNode)saveNode.getRegNode()).getMemoryNode());
		}
		// a = -x
		if (saveNode.getRegNode() instanceof MidNegNode) {
			addSeeking(((MidNegNode)saveNode.getRegNode()).getOperand().getMemoryNode());
		}
		// a = x + y
		if (saveNode.getRegNode() instanceof MidArithmeticNode) {

			addSeeking(((MidArithmeticNode)saveNode.getRegNode()).getLeftOperand().getMemoryNode());
			addSeeking(((MidArithmeticNode)saveNode.getRegNode()).getRightOperand().getMemoryNode());
		}	
	}
	
	public void processSaveNode(MidSaveNode saveNode){
		if (!saveNode.savesRegister() || 
				(saveNode.getRegNode() instanceof MidLoadNode 
						&& ((MidLoadNode)saveNode.getRegNode()).getMemoryNode() instanceof MidArrayElementNode )){
			return;
		}
		
		if (seeking.contains(saveNode.getDestinationNode())){
			seeking.remove(saveNode.getDestinationNode());
			addSeeking(saveNode);
			//Add to needed
			needed.add(saveNode);
		}
	}

	public void processCalloutNode(MidCalloutNode calloutNode){
		addSeeking(calloutNode.getParams());
	}

	
	@Override
	public String toString() {
		return "DCEGlobalState " + needed.size();
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