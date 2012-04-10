package edu.mit.compilers.opt.algebra;

import java.util.HashMap;
import java.util.Map;


public class UnitLiteralCanonicalization extends Canonicalization {
	
	public static Canonicalization makeLiteral(long value){
		Map<Canonicalization, Long> freqs = new HashMap<Canonicalization, Long>();
		freqs.put(new UnitLiteralCanonicalization(), value);
		return new ComplexCanonicalization(freqs);
		
	}

	@Override
	public boolean equals(Object o){
		return o instanceof UnitLiteralCanonicalization;
	}

	@Override
	public Canonicalization add(Canonicalization x) {
		return x;
	}

	@Override
	public Canonicalization mult(Canonicalization x) {
		return x;
	}

	@Override
	public boolean isDiscrete() {
		return true;
	}
	@Override
	public Map<Canonicalization, Long> getTerms() {
		return null;
	}
	
	@Override
	public String toString(){
		return "1";
	}
}
