package edu.mit.compilers.opt.algebra;

import java.util.HashMap;
import java.util.Map;


// This gets very complicated.
//  - Boolean expressions
//  - c*b*d/a*h*f*g --> b*c*d/a*f*g*h
//  - b[0] + a[d*c*f] --> a[c*d*f] + b[0]

public class ComplexCanonicalization extends Canonicalization {
	
	public static void main(String[] args){
	}
	
	protected Map<Canonicalization, Integer> terms;
	public ComplexCanonicalization(Map<Canonicalization, Integer> terms){
		this.terms = terms;
	}
	public Canonicalization add(Canonicalization x){
		Map<Canonicalization, Integer> freqs = new HashMap<Canonicalization, Integer>();
		for(Canonicalization c : terms.keySet()){
			freqs.put(c, terms.get(c));
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
		return new ComplexCanonicalization(freqs);
	}
	
	public Canonicalization mult(Canonicalization x){
		Map<Canonicalization, Integer> freqs = new HashMap<Canonicalization, Integer>();
		for(Canonicalization c : terms.keySet()){
			for(Canonicalization d : x.getTerms().keySet()){
				Canonicalization prod = c.mult(d);
				if(freqs.containsKey(prod)){
					freqs.put(prod, 0);
				}
				freqs.put(prod, freqs.get(prod)+terms.get(c)*x.getTerms().get(c));
				if(freqs.get(prod) == 0){
					freqs.remove(prod);
				}
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
		
	@Override
	public boolean equals(Object o){
		if(!(o instanceof ComplexCanonicalization)){
			return false;
		}
		return getTerms().equals(((ComplexCanonicalization)o).getTerms());
	}
	
	@Override
	public boolean isDiscrete() {
		return false;
	}
	@Override
	public Map<Canonicalization, Integer> getTerms() {
		return terms;
	}
}
