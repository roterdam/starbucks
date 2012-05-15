package edu.mit.compilers.codegen.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import edu.mit.compilers.LogCenter;
import edu.mit.compilers.codegen.AsmVisitor;
import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.asm.ASM;
import edu.mit.compilers.codegen.nodes.regops.MidRegisterNode;
import edu.mit.compilers.opt.regalloc.RegisterAllocator;
import edu.mit.compilers.opt.regalloc.Web;
import edu.mit.compilers.opt.regalloc.nodes.LiveWebsActivist;

/**
 * Represents any call, whether to external libraries or internally.
 * 
 * @author joshma
 * 
 */
public abstract class MidCallNode extends MidRegisterNode implements
		LiveWebsActivist {

	private String name;
	private List<Web> liveWebs;
	private List<Reg> needToSaveRegisters;
	private int paramCount;
	private boolean saveValueDisabled;

	public MidCallNode(String name, int paramCount) {
		this.name = name;
		this.liveWebs = new ArrayList<Web>();
		this.paramCount = paramCount;
		this.needToSaveRegisters = new ArrayList<Reg>();
		this.saveValueDisabled = false;
	}

	@Override
	public String getName() {
		return name;
	}

	public int getParamCount() {
		return paramCount;
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
		LogCenter.debug("CALL", "APPLIED ALLOCATED MAPPINGS FOR " + getName()
				+ ": " + needToSaveRegisters);
	}

	@Override
	public List<ASM> toASM() {
		return AsmVisitor.methodCall(this, saveValueDisabled);
	}

	public boolean saveValueDisabled() {
		return saveValueDisabled;
	}

	public void disableSaveValue() {
		this.saveValueDisabled = true;
	}

	abstract public boolean isStarbucksCall();
	
	@Override
	public void setRegister(Reg reg) {
		assert !saveValueDisabled;
		super.setRegister(reg);
	}

}
