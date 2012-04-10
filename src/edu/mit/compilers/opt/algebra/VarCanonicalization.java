package edu.mit.compilers.opt.algebra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VarCanonicalization extends Canonicalization {
	protected long value;
	public VarCanonicalization(long value) {
		this.value = value;
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof VarCanonicalization)){
			return false;
		}
		return value == ((VarCanonicalization)o).value;
	}

	@Override
	public Canonicalization add(Canonicalization x) {
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Canonicalization mult(Canonicalization x) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDiscrete() {
		return true;
	}
	@Override
	public Map<Canonicalization, Integer> getTerms() {
		return new HashMap<Canonicalization, Integer>(){{
			put(VarCanonicalization.this, 1);
		}};
	}
}
