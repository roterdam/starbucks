package edu.mit.compilers.opt.algebra;

import java.util.HashMap;
import java.util.Map;


// This gets very complicated.
//  - Boolean expressions
//  - c*b*d/a*h*f*g --> b*c*d/a*f*g*h
//  - b[0] + a[d*c*f] --> a[c*d*f] + b[0]

public class LinearCombinationCanonicalization extends Canonicalization {
	
	protected Map<Canonicalization, Long> terms;
	public LinearCombinationCanonicalization(Map<Canonicalization, Long> terms){
		this.terms = terms;
	}
	public Canonicalization add(Canonicalization x){
		if(x == null) return null;
		Map<Canonicalization, Long> freqs = new HashMap<Canonicalization, Long>();
		for(Canonicalization c : terms.keySet()){
			freqs.put(c, terms.get(c));
		}
		for(Canonicalization c : x.getTerms().keySet()){
			if(!freqs.containsKey(c)){
				freqs.put(c, 0L);
			}
			freqs.put(c, freqs.get(c) + x.getTerms().get(c));
			if(freqs.get(c) == 0){
				freqs.remove(c);
			}
		}
		return new LinearCombinationCanonicalization(freqs);
	}
	
	public Canonicalization mult(Canonicalization x){
		if(x == null) return null;
		Map<Canonicalization, Long> freqs = new HashMap<Canonicalization, Long>();
		for(Canonicalization c : terms.keySet()){
			for(Canonicalization d : x.getTerms().keySet()){
				Canonicalization prod = c.mult(d); //At some point a discrete term will be reached.
				if(!freqs.containsKey(prod)){
					freqs.put(prod, 0L);
				}
				Long myTerms = terms.get(c);
				assert myTerms != null;
				Long yourTerms = x.getTerms().get(d);
				assert yourTerms != null : x.getClass().toString();
				
				assert freqs.get(prod) != null;
				assert prod != null;
				
				freqs.put(prod, freqs.get(prod)+terms.get(c)*x.getTerms().get(d));
				if(freqs.get(prod) == 0){
					freqs.remove(prod);
				}
			}
		}
		if(freqs.keySet().size() == 0){
			return UnitLiteralCanonicalization.makeLiteral(0);
		}else if(freqs.keySet().size() == 1){
			Canonicalization c = (Canonicalization) freqs.keySet().toArray()[0];
			return c.mult(UnitLiteralCanonicalization.makeLiteral(freqs.get(c)));
		}
		return new LinearCombinationCanonicalization(freqs);
	}
		
	@Override
	public boolean equals(Object o){
		if(!(o instanceof LinearCombinationCanonicalization)){
			return false;
		}
		return getTerms().equals(((LinearCombinationCanonicalization)o).getTerms());
	}
	
	@Override
	public boolean isDiscrete() {
		return false;
	}
	@Override
	public Map<Canonicalization, Long> getTerms() {
		return terms;
	}
	
	@Override
	public String toString(){
		StringBuilder x = new StringBuilder("Complex<");
		for(Canonicalization c : terms.keySet()){
			x.append(terms.get(c)+": "+c.toString()+" + ");
		}
		x.append(">");
		return x.toString();
	}
}
