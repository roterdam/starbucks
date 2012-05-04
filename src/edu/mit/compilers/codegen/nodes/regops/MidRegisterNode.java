package edu.mit.compilers.codegen.nodes.regops;

import java.util.ArrayList;
import java.util.List;

import edu.mit.compilers.codegen.MidLabelManager;
import edu.mit.compilers.codegen.Reg;
import edu.mit.compilers.codegen.nodes.MidNode;
import edu.mit.compilers.codegen.nodes.MidSaveNode;

/**
 * Represents an instruction that stores something on a register.
 */
abstract public class MidRegisterNode extends MidNode {

	String name;
	private Reg register = null;
	private List<MidSaveNode> referencingSaveNodes;

	public MidRegisterNode() {
		name = "reg" + MidLabelManager.getNewId();
		referencingSaveNodes = new ArrayList<MidSaveNode>();
	}

	public String getName() {
		return name;
	}

	public void setRegister(Reg r) {
		register = r;
	}

	public Reg getRegister() {
		assert register != null : "Called get register before register was set.";
		return register;
	}

	/**
	 * Returns whether this RegisterNode already has a register assigned. Useful
	 * for when params require an explicit register to be used and has set it in
	 * advance.
	 * 
	 * @return
	 */
	public boolean hasRegister() {
		return register != null;
	}
	
	public void record(MidSaveNode saveNode) {
		referencingSaveNodes.add(saveNode);
	}
	
	@Override
	public void delete() {
		super.delete();
		for (MidSaveNode s : referencingSaveNodes) {
			s.delete();
		}
	}

}
