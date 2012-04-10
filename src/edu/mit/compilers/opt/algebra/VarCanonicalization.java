package edu.mit.compilers.opt.algebra;

import java.util.HashMap;
import java.util.Map;


public class VarCanonicalization extends Canonicalization {
	protected String name;
	public VarCanonicalization(String name) {
		this.name = name;
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof VarCanonicalization)){
			return false;
		}
		return name.equals(((VarCanonicalization)o).name);
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
