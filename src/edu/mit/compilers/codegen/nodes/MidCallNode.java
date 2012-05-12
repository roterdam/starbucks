package edu.mit.compilers.codegen.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import edu.mit.compilers.codegen.AsmVisitor;
import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.nodes.memory.MidMemoryNode;
import edu.mit.compilers.codegen.nodes.regops.MidLoadNode;
import edu.mit.compilers.codegen.nodes.regops.MidRegisterNode;
import edu.mit.compilers.opt.regalloc.LiveWebsActivist;
import edu.mit.compilers.opt.regalloc.RegisterAllocator;
import edu.mit.compilers.opt.regalloc.Web;

/**
 * Represents any call, whether to external libraries or internally.
 * 
 * @author joshma
 * 
 */
public class MidCallNode extends MidRegisterNode implements LiveWebsActivist {

	private String name;
	private List<Web> liveWebs;
	private List<Reg> needToSaveRegisters;
	private List<MidLoadNode> params;

	public MidCallNode(String name, List<MidMemoryNode> params) {
		this.name = name;
		this.params = new ArrayList<MidLoadNode>();
		for (MidMemoryNode node : params){
			this.params.add(new MidLoadNode(node));
		}
		this.needToSaveRegisters = new ArrayList<Reg>();
		this.liveWebs = new ArrayList<Web>();
	}

	@Override
	public String getName() {
		return name;
	}

	public List<MidMemoryNode> getParams() {
		List<MidMemoryNode> out = new ArrayList<MidMemoryNode>();
		for (MidLoadNode node : params){
			out.add(node.getMemoryNode());
		}
		return out;
	}
	
	public List<MidLoadNode> getParamNodes() {
		return params;
	}

	@Override
	public void setLiveWebs(List<Web> liveWebs) {
		this.liveWebs = liveWebs;
	}

	public List<Reg> getNeedToSaveRegisters() {
		return needToSaveRegisters;
	}

	@Override
	public void applyAllocatedMapping(Map<Web, Reg> mapping) {
		// Careful, "asList" is a view onto the backing array. Don't modify!
		List<Reg> callerSavedRegs = Arrays
				.asList(RegisterAllocator.CALLER_SAVED_REGISTERS);
		for (Web web : liveWebs) {
			Reg r = mapping.get(web);
			if (callerSavedRegs.contains(r)) {
				needToSaveRegisters.add(r);
			}
		}
	}

	@Override
	public List<ASM> toASM() {
		return AsmVisitor.methodCall(this);
	}

}
