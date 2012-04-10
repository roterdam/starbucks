package edu.mit.compilers.opt.algebra;

import java.util.HashMap;
import java.util.Map;

import edu.mit.compilers.opt.algebra.DMMCanonicalization;
import edu.mit.compilers.opt.algebra.DMMCanonicalization.DMMType;

public abstract class Canonicalization {
	public Canonicalization add(Canonicalization x){
		
		Map<Canonicalization, Integer> freqs = new HashMap<Canonicalization, Integer>();
		for(Canonicalization c : getTerms().keySet()){
			freqs.put(c, getTerms().get(c));
		}
		for(Canonicalization c : x.getTerms().keySet()){
			if(!freqs.containsKey(c)){
				freqs.put(c, 0);
			}
			freqs.put(c, freqs.get(c) + x.getTerms().get(c));
			if(freqs.get(c) == 0){
				freqs.remove(c);
			}
		}
		if(freqs.entrySet().size() == 0){
			return new LiteralCanonicalization(0);
		}else if(freqs.entrySet().size() == 1){
			Canonicalization c = (Canonicalization) freqs.entrySet().toArray()[0];
			return c.mult(new LiteralCanonicalization(freqs.get(c)));
		}
		
		return new ComplexCanonicalization(freqs);
	}
	
	public abstract Canonicalization mult(Canonicalization x);
	
	public Canonicalization sub(Canonicalization x){
		return this.add(x.mult(new LiteralCanonicalization(-1)));
	}
	
	public Canonicalization div(Canonicalization x) {
		return new DMMCanonicalization(this, x, DMMType.DIV);
	}
	
	public Canonicalization mod(Canonicalization x) {
		return new DMMCanonicalization(this, x, DMMType.MOD);
	}
	
	@Override
	public abstract boolean equals(Object o);
	
	public abstract boolean isDiscrete();
	public abstract Map<Canonicalization, Integer> getTerms();
}
