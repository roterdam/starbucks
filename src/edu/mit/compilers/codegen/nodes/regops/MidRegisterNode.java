package edu.mit.compilers.codegen.nodes.regops;

import edu.mit.compilers.codegen.MidLabelManager;
import edu.mit.compilers.codegen.nodes.MidNode;

/**
 * Represents an instruction that stores something on a register.
 */
abstract public class MidRegisterNode extends MidNode {
	
	private String name;
	private String registerId;
	
	public MidRegisterNode() {
		name = "reg" + MidLabelManager.getNewId();
	}
	
	public String getName() {
		return name;
	}
	
	public String getRegisterId(){
		//assert location != null;
		return this.registerId;
	}
	
	public void setRegisterId(String registerId){
		this.registerId = registerId;
	}
	

}
