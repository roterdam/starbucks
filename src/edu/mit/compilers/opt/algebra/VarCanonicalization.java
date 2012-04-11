package edu.mit.compilers.opt.algebra;

import java.util.HashMap;
import java.util.Map;


public class VarCanonicalization extends Canonicalization {
	protected String name;
	protected Canonicalization index;
	protected boolean isArray;
	
	public VarCanonicalization(String name) {
		this.name = name;
		this.index = null;
		this.isArray = false;
	}
	public VarCanonicalization(String name, Canonicalization index) {
		this.name = name;
		this.index = index;
		this.isArray = true;
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof VarCanonicalization)){
			return false;
		}
		VarCanonicalization v = (VarCanonicalization)o;
		if(!name.equals(v.name) || isArray != v.isArray){
			return false;
		}
		return !isArray || index != null && index.equals(v.index); 
	}

	@Override
	public Canonicalization add(Canonicalization x) {
		assert false : "var is part of a bigger discrete unit.";
		return null;
	}

	@Override
	public Canonicalization mult(Canonicalization x) {
		assert false : "var is part of a bigger discrete unit.";
		return null;
	}

	@Override
	public boolean isDiscrete() {
		return true;
	}
	
	@SuppressWarnings("serial")
	@Override
	public Map<Canonicalization, Long> getTerms() {
		return new HashMap<Canonicalization, Long>(){{
			put(VarCanonicalization.this, 1L);
		}};
	}
	
	@Override
	public String toString(){
		return name;
	}
}
