package edu.mit.compilers.opt.algebra;

import java.util.HashMap;
import java.util.Map;

import edu.mit.compilers.opt.algebra.DMMCanonicalization;
import edu.mit.compilers.opt.algebra.DMMCanonicalization.DMMType;

public abstract class Canonicalization {
	
	public static void main(String[] args){
		
		Canonicalization x = ProductCanonicalization.makeVariable("a");
		Canonicalization y = ProductCanonicalization.makeVariable("b");
		Canonicalization z = ProductCanonicalization.makeVariable("c");
		Canonicalization w = x.add(y).add(z);
		
		Canonicalization a = ProductCanonicalization.makeVariable("a");
		Canonicalization b = ProductCanonicalization.makeVariable("b");
		Canonicalization c = ProductCanonicalization.makeVariable("c");
		Canonicalization e = ProductCanonicalization.makeVariable("e");
		Canonicalization d = a.add(c).add(b);
		
		System.out.println(w.equals(d));
		
		
		Canonicalization t1 = x.add(y).mult(z);
		Canonicalization t2 = x.mult(z).add(z.mult(y));
		
		System.out.println(t1.equals(t2));
		System.out.println(t1);
		System.out.println(t2);
		
		Canonicalization t3 = a.div(b).mult(c).mult(d);
		Canonicalization t4 = a.div(b).mult(c.mult(d));
		
		System.out.println(t3.equals(t4));
		System.out.println(t3);
		System.out.println(t4);
				
		
	}
	public Canonicalization add(Canonicalization x){
		
		Map<Canonicalization, Long> freqs = new HashMap<Canonicalization, Long>();
		for(Canonicalization c : getTerms().keySet()){
			freqs.put(c, getTerms().get(c));
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
		if(freqs.entrySet().size() == 0){
			return UnitLiteralCanonicalization.makeLiteral(0);
		}else if(freqs.entrySet().size() == 1){
			Canonicalization c = (Canonicalization) freqs.entrySet().toArray()[0];
			return c.mult(UnitLiteralCanonicalization.makeLiteral(freqs.get(c)));
		}
		
		return new ComplexCanonicalization(freqs);
	}
	
	public abstract Canonicalization mult(Canonicalization x);
	
	public Canonicalization sub(Canonicalization x){
		return this.add(x.mult(UnitLiteralCanonicalization.makeLiteral(-1)));
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
	public abstract Map<Canonicalization, Long> getTerms();
	
	@Override
	public int hashCode(){
		// TODO, make better hash.
		return 0;
	}
}
