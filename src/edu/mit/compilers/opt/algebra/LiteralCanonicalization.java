package edu.mit.compilers.opt.algebra;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class LiteralCanonicalization extends Canonicalization {
	protected long value;
	public LiteralCanonicalization(long value) {
		this.value = value;
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof LiteralCanonicalization)){
			return false;
		}
		return value == ((LiteralCanonicalization)o).value;
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
		return null;
	}
}
